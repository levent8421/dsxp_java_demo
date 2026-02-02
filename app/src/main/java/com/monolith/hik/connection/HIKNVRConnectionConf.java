package com.monolith.hik.connection;

import com.monolith.dsxp.define.DsxpConnectionDefinition;
import com.monolith.dsxp.util.DeviceDefinitionUtils;

/**
 * Date: 2025/6/30 15:19
 * Author: Levent
 * 连接配置
 */
public class HIKNVRConnectionConf {
    private String host;
    private int port;
    private String username;
    private String password;

    public HIKNVRConnectionConf(DsxpConnectionDefinition def) {
        this.host = def.getTarget();
        this.port = DeviceDefinitionUtils.getConnectionParamAsInt(def, 0, 8000);
        this.username = def.getAuthUsername();
        this.password = def.getAuthPassword();
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
