package com.example.uavapplication.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UavEntity implements java.io.Serializable{
    private int id;

    private String name;

    private String ip;

    private String port;

    private int status;

    /*public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public UavEntity(int id, String name, String ip, String port, int status) {
        this.id = id;
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.status = status;
    }
    public UavEntity() {
    }*/
}
