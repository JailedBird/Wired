package com.jailedbird.wired.lib_annotation.annotation

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.BINARY)
annotation class Wired(val name: String = "", val required: Boolean = false)