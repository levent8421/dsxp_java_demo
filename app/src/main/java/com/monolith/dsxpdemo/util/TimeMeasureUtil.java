package com.monolith.dsxpdemo.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Create By YANYiZHI
 * Create Time: 2026/03/02 9:33
 * Description:
 * TimeMeasureUtil
 *
 * @author YANYiZHI
 */
public class TimeMeasureUtil {
    private static final Map<String, Long> cacheMap = new HashMap<>();

    public static void start(String tag) {
        cacheMap.put(tag, System.currentTimeMillis());
    }

    public static void stop(String tag) {
        Long startTime = cacheMap.remove(tag);
        if (startTime != null) {
            long diff = System.currentTimeMillis() - startTime;
            System.out.println(">>>>>>>>>>>>" +tag + "  耗时  " + diff);
        }
    }
}
