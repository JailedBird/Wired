package com.jailedbird.wired.lib_compiler.utils

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.jailedbird.wired.lib_annotation.enums.RouteType
import com.jailedbird.wired.lib_annotation.enums.TypeKind

/**
 * AutoWire Inject Field Type check and convert
 * */
internal fun KSPropertyDeclaration.typeExchange(): Int {
    val type = this.type.resolve()
    return when (type.declaration.qualifiedName?.asString()) {
        Consts.KBYTE -> TypeKind.BYTE.ordinal
        Consts.KSHORT -> TypeKind.SHORT.ordinal
        Consts.KINTEGER -> TypeKind.INT.ordinal
        Consts.KLONG -> TypeKind.LONG.ordinal
        Consts.KFLOAT -> TypeKind.FLOAT.ordinal
        Consts.KDOUBEL -> TypeKind.DOUBLE.ordinal
        Consts.KBOOLEAN -> TypeKind.BOOLEAN.ordinal
        Consts.KCHAR -> TypeKind.CHAR.ordinal
        Consts.KSTRING -> TypeKind.STRING.ordinal
        else -> {
            when (this.isSubclassOf(listOf(Consts.PARCELABLE, Consts.SERIALIZABLE))) {
                0 -> TypeKind.PARCELABLE.ordinal
                1 -> TypeKind.SERIALIZABLE.ordinal
                else -> TypeKind.OBJECT.ordinal
            }
        }
    }
}

private val ROUTE_TYPE_LIST = listOf(
    Consts.ACTIVITY,// 0
    Consts.ACTIVITY_ANDROIDX, // 1
    Consts.FRAGMENT, // 2
    Consts.FRAGMENT_V4, // 3
    Consts.FRAGMENT_ANDROIDX, // 4
)

internal val KSClassDeclaration.routeType: RouteType
    get() = when (isSubclassOf(ROUTE_TYPE_LIST)) {
        0, 1 -> RouteType.ACTIVITY
        2, 3, 4 -> RouteType.FRAGMENT
        else -> RouteType.OTHER
    }
