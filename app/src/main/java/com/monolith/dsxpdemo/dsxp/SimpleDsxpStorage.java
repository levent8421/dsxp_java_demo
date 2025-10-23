package com.monolith.dsxpdemo.dsxp;

import com.monolith.dsxp.storage.DsxpMemoryStorage;
import com.monolith.dsxp.storage.DsxpStorageException;

/**
 * Date: 2025/10/23 15:41
 * Author: Levent
 * 驱动内的存储管理（实际使用时请替换为持久化存储方案）
 */
public class SimpleDsxpStorage extends DsxpMemoryStorage {
    @Override
    public String load(String bucket, String key) throws DsxpStorageException {
        return super.load(bucket, key);
    }

    @Override
    public void save(String bucket, String key, String value) throws DsxpStorageException {
        super.save(bucket, key, value);
    }
}
