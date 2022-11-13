package com.jailedbird.wired.lib_api.impl

import com.jailedbird.wired.lib_api.service.SerializationService
import com.jailedbird.wired.lib_api.utils.GsonUtils
import java.lang.reflect.Type

object SerializationServiceImpl : SerializationService {

    override fun object2Json(instance: Any?): String? {
        return GsonUtils.toJson(instance)
    }

    override fun <T> parseObject(input: String?, clazz: Type?): T {
        return GsonUtils.fromJson(input, clazz!!)
    }
}