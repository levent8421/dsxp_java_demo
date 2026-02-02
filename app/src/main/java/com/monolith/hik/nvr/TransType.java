package com.monolith.hik.nvr;

import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class TransType extends Structure {
    public int type = 2;

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("type");
    }
}
