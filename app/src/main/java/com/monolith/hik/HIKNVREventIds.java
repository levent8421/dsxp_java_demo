package com.monolith.hik;

import com.monolith.dsxp.event.DsxpEventIds;

/**
 * Date: 2025/6/30 10:56
 * Author: Levent
 * HIK NVR 事件列表
 */
public class HIKNVREventIds {
    public static final int SDK_INITIALIZED = DsxpEventIds.eventId(DsxpEventIds.DRIVER_GROUP_EVENT, 0x07_00_01);
    public static final int SDK_INIT_FAILED = DsxpEventIds.eventId(DsxpEventIds.DRIVER_GROUP_EVENT, 0x07_00_02);
    public static final int DOWNLOAD_COMPLETE = DsxpEventIds.eventId(DsxpEventIds.DAU_EVENT, 0x07_00_01);
}
