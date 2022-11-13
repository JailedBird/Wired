package com.jailedbird.wired.lib_api.service

import java.lang.reflect.Type

/**
 * Used for parse json string.
 */
interface SerializationService  {
    /**
     * Object to json
     *
     * @param instance obj
     * @return json string
     */
    fun object2Json(instance: Any?): String?

    /**
     * Parse json to object
     *
     * @param input json string
     * @param clazz object type
     * @return instance of object
     */
    fun <T> parseObject(input: String?, clazz: Type?): T
}