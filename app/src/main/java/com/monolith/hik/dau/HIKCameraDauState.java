package com.monolith.hik.dau;

import com.monolith.dsxp.driver.state.DauWorkerState;
import com.monolith.dsxp.util.MapUtils;

import java.util.Map;

/**
 * Date: 2025/6/30 10:51
 * Author: Levent
 * 采集点状态
 */
public class HIKCameraDauState extends DauWorkerState {
    private final Map<String, MediaDownloadTask> taskTable = MapUtils.newHashMap();

    public Map<String, MediaDownloadTask> getTaskTable() {
        return taskTable;
    }
}
