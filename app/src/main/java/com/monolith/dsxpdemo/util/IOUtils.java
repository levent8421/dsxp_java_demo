package com.monolith.dsxpdemo.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * Date: 2025/10/20 16:23
 * Author: Levent
 * IOUtils
 */
public class IOUtils {
    public static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
    }

    public static String readAsString(InputStream in) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            copy(in, out);
            byte[] bytes = out.toByteArray();
            return new String(bytes, Charset.defaultCharset());
        }
    }
}
