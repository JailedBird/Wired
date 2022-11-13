package com.jailedbird.wired.lib_api.launcher

import com.jailedbird.wired.lib_api.impl.AutowiredServiceImpl
import com.jailedbird.wired.lib_api.service.AutowiredService

object WiredInjector {
    fun inject(obj: Any) {
        val autowiredService: AutowiredService = AutowiredServiceImpl()
        autowiredService.autowire(obj)
    }
}