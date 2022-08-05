package com.github.klefstad_teaching.cs122b.gateway;

import java.sql.Timestamp;

public class GatewayRequestObject {

    private String ip_address;
    private Timestamp call_time;
    private String path;

    public String getIp_address() {
        return ip_address;
    }

    public GatewayRequestObject setIp_address(String ip_address) {
        this.ip_address = ip_address;
        return this;
    }

    public Timestamp getCall_time() {
        return call_time;
    }

    public GatewayRequestObject setCall_time(Timestamp call_time) {
        this.call_time = call_time;
        return this;
    }

    public String getPath() {
        return path;
    }

    public GatewayRequestObject setPath(String path) {
        this.path = path;
        return this;
    }
}
