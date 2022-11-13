package com.jailedbird.wired.lib_compiler

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import com.jailedbird.wired.lib_annotation.annotation.Wired
import com.jailedbird.wired.lib_annotation.enums.RouteType
import com.jailedbird.wired.lib_annotation.enums.TypeKind
import com.jailedbird.wired.lib_compiler.utils.*

@KotlinPoetKspPreview
class WiredSymbolProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return WiredSymbolProcessor(
            KSPLoggerWrapper(environment.logger), environment.codeGenerator
        )
    }

    @KotlinPoetKspPreview
    class WiredSymbolProcessor(
        private val logger: KSPLoggerWrapper,
        private val codeGenerator: CodeGenerator,
    ) : SymbolProcessor {
        @Suppress("SpellCheckingInspection")
        companion object {
            val WIRED_CLASS_NAME = Wired::class.qualifiedName!!
            private val ISYRINGE_CLASS_NAME =
                ClassName("com.jailedbird.wired.lib_api.template", "ISyringe")
            private val JSON_SERVICE_CLASS_NAME =
                ClassName("com.jailedbird.wired.lib_api.impl", "SerializationServiceImpl")
        }

        override fun process(resolver: Resolver): List<KSAnnotated> {
            val time = System.nanoTime()
            val symbol = resolver.getSymbolsWithAnnotation(WIRED_CLASS_NAME)

            val elements = symbol
                .filterIsInstance<KSPropertyDeclaration>()
                .toList()

            if (elements.isNotEmpty()) {
                logger.info(">>> WiredSymbolProcessor init. <<<")
                try {
                    parseWired(elements)
                } catch (e: Exception) {
                    logger.exception(e)
                }
                logger.info("WiredSymbolProcessor process time spend: ${(System.nanoTime() - time).toFloat() / 1000000f} ms")
            }
            return emptyList()
        }

        private fun parseWired(elements: List<KSPropertyDeclaration>) {
            logger.info(">>> Found Wired field, start... <<<")
            generateWiredFiles(categories(elements))
        }

        private fun categories(elements: List<KSPropertyDeclaration>): MutableMap<KSClassDeclaration, MutableList<KSPropertyDeclaration>> {
            val parentAndChildren =
                mutableMapOf<KSClassDeclaration, MutableList<KSPropertyDeclaration>>()
            for (element in elements) {
                // Class of the member
                logger.check(element.parentDeclaration is KSClassDeclaration) {
                    "Property annotated with @needNotice = false 's enclosingElement(property's class) must be non-null!"
                }
                val parent = element.parentDeclaration as KSClassDeclaration

                if (element.modifiers.contains(Modifier.PRIVATE)) {
                    throw  IllegalAccessException(
                        "The inject fields CAN NOT BE 'private'!!! please check field ["
                                + element.simpleName.asString() + "] in class [" + parent.qualifiedName?.asString() + "]"
                    )
                }
                if (parentAndChildren.containsKey(parent)) {
                    parentAndChildren[parent]!!.add(element)
                } else {
                    parentAndChildren[parent] = mutableListOf(element)
                }
            }
            parentAndChildren.forEach {
                logger.check(it.key.isKotlinClass(), it.key) {
                    "@Wired now can only annotated in kotlin file, ${it.key.qualifiedName?.asString()} is not a java class"
                }
            }
            logger.info("@needNotice = false categories finished.")
            return parentAndChildren
        }

        @Suppress("SpellCheckingInspection")
        private fun generateWiredFiles(parentAndChildren: MutableMap<KSClassDeclaration, MutableList<KSPropertyDeclaration>>) {
            /** target: Any? */
            val parameterName = "target"
            val parameterSpec = ParameterSpec.builder(
                parameterName,
                Any::class.asTypeName().copy(nullable = true)
            ).build()

            for (entry in parentAndChildren) {
                val parent: KSClassDeclaration = entry.key
                val children: List<KSPropertyDeclaration> = entry.value
                if (children.isEmpty()) continue
                logger.info(">>> Start process " + children.size + " field in " + parent.simpleName.asString() + " ... <<<")
                // Get input source (@needNotice = false) which gene the output file
                val dependency = mutableSetOf<KSFile>()
                parent.containingFile?.let {
                    dependency.add(it)
                }
                /** override fun inject(target: Any?) */
                val injectMethodBuilder: FunSpec.Builder = FunSpec
                    .builder(Consts.METHOD_INJECT)
                    .addModifiers(KModifier.OVERRIDE)
                    .addParameter(parameterSpec)

                val parentClassName = parent.toClassName()
                injectMethodBuilder.addStatement(
                    "val substitute = (target as? %T)?: throw IllegalStateException(\n·\"\"\"The target that needs to be injected must be %T, please check your code!\"\"\"\n·)",
                    parentClassName, parentClassName
                )

                val parentRouteType = parent.routeType
                logger.check((parentRouteType == RouteType.ACTIVITY) || (parentRouteType == RouteType.FRAGMENT)) {
                    "@${WIRED_CLASS_NAME} can only add to Fragment or Activity's feild, but now $WIRED_CLASS_NAME add in ${parentClassName.canonicalName}"
                }

                /**
                 *  Judge this file generate with isolating or aggregating mode
                 *  More detail: https://kotlinlang.org/docs/ksp-incremental.html#symbolprocessorprovider-the-entry-point
                 *  */
                // Generate method body, start inject.
                for (child in children) {
                    addActivityOrFragmentStatement(
                        child, injectMethodBuilder, TypeKind.values()[child.typeExchange()],
                        parentRouteType, parentClassName
                    )
                }

                val wiredClassName = parent.simpleName.asString() + Consts.NAME_OF_WIRED
                val qualifiedName = parent.qualifiedName!!.asString()
                val packageName = qualifiedName.substring(0, qualifiedName.lastIndexOf("."))

                val file =
                    FileSpec.builder(packageName, wiredClassName)
                        .addImport("android.util", "Log") // manual import (without %T)
                        .addImport(
                            "com.jailedbird.wired.lib_annotation.model",
                            "TypeWrapper"
                        )
                        .addType(
                            TypeSpec.classBuilder(ClassName(packageName, wiredClassName))
                                .addKdoc(Consts.WARNING_TIPS)
                                .addSuperinterface(ISYRINGE_CLASS_NAME)
                                .addFunction(injectMethodBuilder.build())
                                .build()
                        )
                        .build()

                file.writeTo(codeGenerator, false, dependency)
                logger.info(">>> " + parent.simpleName.asString() + " has been processed, " + wiredClassName + " has been generated. <<<")
            }
            logger.info(">>> needNotice = false processor stop. <<<")
        }

        /**
         * Inject field for activity and fragment
         * */
        private fun addActivityOrFragmentStatement(
            property: KSPropertyDeclaration,
            method: FunSpec.Builder,
            type: TypeKind,
            parentRouteType: RouteType,
            parentClassName: ClassName
        ) {
            val fieldName = property.simpleName.asString()
            val isNullable = property.type.resolve().isMarkedNullable
            val isActivity = when (parentRouteType) {
                RouteType.ACTIVITY -> true
                RouteType.FRAGMENT -> false
                else -> {
                    throw IllegalAccessException("The field [$fieldName] need needNotice = false from intent, its parent must be activity or fragment!")
                }
            }
            val intent = if (isActivity) "intent?.extras" else "arguments"
            val annotation = property.findAnnotationWithType<Wired>()!!
            val bundleName = annotation.name.ifEmpty { fieldName }

            val getPrimitiveTypeMethod: String = when (type) {
                TypeKind.BOOLEAN -> "getBoolean"
                TypeKind.BYTE -> "getByte"
                TypeKind.SHORT -> "getShort"
                TypeKind.INT -> "getInt"
                TypeKind.LONG -> "getLong"
                TypeKind.CHAR -> "getChar"
                TypeKind.FLOAT -> "getFloat"
                TypeKind.DOUBLE -> "getDouble"
                else -> ""
            }
            // Primitive type
            if (getPrimitiveTypeMethod.isNotEmpty()) {
                val primitiveCodeBlock = if (isNullable) {
                    CodeBlock.builder()
                        .beginControlFlow("substitute.${intent}?.let")
                        .beginControlFlow("if(it.containsKey(%S))", bundleName)
                        .addStatement(
                            "substitute.%L = it.${getPrimitiveTypeMethod}(%S)",
                            fieldName,
                            bundleName
                        )
                        .endControlFlow()
                        .endControlFlow()
                        .build()
                } else {
                    CodeBlock.builder()
                        .beginControlFlow("substitute.${intent}?.let")
                        .addStatement(
                            "substitute.%L = it.${getPrimitiveTypeMethod}(%S, substitute.%L)",
                            fieldName,
                            bundleName,
                            fieldName
                        )
                        .endControlFlow()
                        .build()
                }
                method.addCode(primitiveCodeBlock)
            } else {
                // such as: val param = List<JailedBird> ==> %T ==> List<JailedBird>
                val parameterClassName = property.getKotlinPoetTTypeGeneric()

                when (type) {
                    TypeKind.STRING -> {
                        method.addCode(
                            CodeBlock.builder()
                                .beginControlFlow("substitute.${intent}?.let")
                                .addStatement(
                                    "substitute.%L = it.getString(%S, substitute.%L)",
                                    fieldName,
                                    bundleName,
                                    fieldName
                                )
                                .endControlFlow()
                                .build()
                        )
                    }
                    TypeKind.SERIALIZABLE -> {
                        val beginStatement = if (isActivity) {
                            "(substitute.intent?.getSerializableExtra(%S) as? %T)?.let"
                        } else {
                            "(substitute.arguments?.getSerializable(%S) as? %T)?.let"
                        }
                        method.addCode(
                            CodeBlock.builder()
                                .beginControlFlow(beginStatement, bundleName, parameterClassName)
                                .addStatement("substitute.%L = it", fieldName)
                                .endControlFlow().build()
                        )
                    }
                    TypeKind.PARCELABLE -> {
                        val beginStatement = if (isActivity) {
                            "substitute.intent?.getParcelableExtra<%T>(%S)?.let"
                        } else {
                            "substitute.arguments?.getParcelable<%T>(%S)?.let"
                        }
                        method.addCode(
                            CodeBlock.builder()
                                .beginControlFlow(beginStatement, parameterClassName, bundleName)
                                .addStatement("substitute.%L = it", fieldName)
                                .endControlFlow().build()
                        )
                    }
                    TypeKind.OBJECT -> {
                        method.addCode(
                            CodeBlock.builder()
                                .beginControlFlow("substitute.${intent}?.let")
                                .addStatement(
                                    "substitute.%L = %T.parseObject(it.getString(%S), (object : TypeWrapper<%T>(){}).type)",
                                    fieldName,
                                    JSON_SERVICE_CLASS_NAME,
                                    bundleName,
                                    parameterClassName
                                )
                                .endControlFlow().build()
                        )
                    }
                    else -> {
                        // This branch will not be reach
                        error("This branch will not be reach")
                    }
                }
                // Validator, Primitive type wont be check.
                if (annotation.required) {
                    method.beginControlFlow("if (substitute.$fieldName == null)")
                        .addStatement(
                            "Log.e(\"${Consts.TAG}\" , \"\"\"The field '%L' in class '%L' is null!\"\"\")",
                            fieldName, parentClassName.simpleName
                        )
                    method.endControlFlow()
                }
            }

        }
    }

}

