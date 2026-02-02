package com.monolith.hik.driver;

import com.monolith.dsxp.driver.AbstractDsxpDriverGroupBuilder;
import com.monolith.dsxp.driver.DsxpDriverGroupWorker;
import com.monolith.dsxp.tree.DsxpDriverGroupNode;

/**
 * Date: 2025/6/30 16:33
 * Author: Levent
 * Driver Group Builder
 */
public class HIKNVRDriverGroupBuilder extends AbstractDsxpDriverGroupBuilder {
    public static final String VERSION_FILENAME = "hik_nvr.properties";

    public HIKNVRDriverGroupBuilder() {
        super(VERSION_FILENAME);
    }

    @Override
    public DsxpDriverGroupWorker buildDriverGroup(DsxpDriverGroupNode node) throws Exception {
        return new HIKNVRDriverGroupWorker(node);
    }
}
