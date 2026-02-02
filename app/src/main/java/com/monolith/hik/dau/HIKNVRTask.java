package com.monolith.hik.dau;

import com.monolith.hik.nvr.HIKDVRClient;

/**
 * Date: 2025/6/30 14:56
 * Author: Levent
 * 海康NVR任务
 */
public interface HIKNVRTask {
    void nvrTaskRun(HIKDVRClient client) throws Exception;

    void onTaskError(Exception err);
}
