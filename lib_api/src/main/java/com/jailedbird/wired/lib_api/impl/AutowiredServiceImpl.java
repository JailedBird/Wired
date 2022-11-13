package com.jailedbird.wired.lib_api.impl;

import android.util.LruCache;

import com.jailedbird.wired.lib_api.service.AutowiredService;
import com.jailedbird.wired.lib_api.template.ISyringe;
import com.jailedbird.wired.lib_api.utils.Consts;

import java.util.ArrayList;
import java.util.List;

/**
 * param inject service impl.
 *
 * @author zhilong <a href="mailto:zhilong.lzl@alibaba-inc.com">Contact me.</a>
 * @version 1.0
 * @since 2017/2/28 下午6:08
 */
public class AutowiredServiceImpl implements AutowiredService {
    private final LruCache<String, ISyringe> classCache = new LruCache<>(50);
    private final List<String> blackList = new ArrayList<>();

    @Override
    public void autowire(Object instance) {
        doInject(instance, null);
    }

    /**
     * Recursive injection
     *
     * @param instance who call me.
     * @param parent   parent of me.
     */
    private void doInject(Object instance, Class<?> parent) {
        Class<?> clazz = null == parent ? instance.getClass() : parent;

        ISyringe syringe = getSyringe(clazz);
        if (null != syringe) {
            syringe.inject(instance);
        }

        Class<?> superClazz = clazz.getSuperclass();
        // has parent and its not the class of framework.
        if (null != superClazz && !superClazz.getName().startsWith("android")) {
            doInject(instance, superClazz);
        }
    }

    private ISyringe getSyringe(Class<?> clazz) {
        String className = clazz.getName();

        try {
            if (!blackList.contains(className)) {
                ISyringe syringeHelper = classCache.get(className);
                if (null == syringeHelper) {  // No cache.
                    syringeHelper = (ISyringe) Class.forName(clazz.getName() + Consts.SUFFIX_AUTOWIRED).getConstructor().newInstance();
                }
                classCache.put(className, syringeHelper);
                return syringeHelper;
            }
        } catch (Exception e) {
            blackList.add(className);    // This instance need not autowired.
        }

        return null;
    }
}
