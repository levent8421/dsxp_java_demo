package com.monolith.hik.util;

import android.os.Environment;

import com.monolith.dsxp.util.DSxPSnowflake;

import java.io.File;
import java.io.IOException;

/**
 * Date: 2025/6/30 11:18
 * Author: Levent
 * 存储工具
 */
public class StorageUtils {
    public static final String TMP_FILE_DIR = "hik_tmp";
    private static final DSxPSnowflake TMP_FILE_NAMES = new DSxPSnowflake();

    public static File getStorageDir() {
        File directory = Environment.getExternalStorageDirectory();
        return new File(directory, "/proton");
    }

    public static File getStorageDir(String name) {
        File dir = getStorageDir();
        return new File(dir, name);
    }

    public static File getNextTmpVideoFile() throws IOException {
        File dir = getStorageDir(TMP_FILE_DIR);
        if (!dir.mkdirs()) {
            throw new IOException("Create tmp dir failed:" + dir.getAbsoluteFile());
        }
        String filename = TMP_FILE_NAMES.nextStr("", ".ps");
        return new File(dir, filename);
    }
}
