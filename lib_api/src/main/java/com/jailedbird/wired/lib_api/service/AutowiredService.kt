package com.jailedbird.wired.lib_api.service

/**
 * Service for autowired.
 *
 * @author zhilong [Contact me.](mailto:zhilong.lzl@alibaba-inc.com)
 * @version 1.0
 * @since 2017/2/28 下午6:06
 */
interface AutowiredService {
    /**
     * Autowired core.
     * @param instance the instance who need autowired.
     */
    fun autowire(instance: Any?)
}