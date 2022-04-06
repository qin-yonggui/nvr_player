package com.test.nvrsearch;

public class DetectNvrModel {
    private String ip;
    private String port;
    private String manufacturer; //1 dahua 2 haikang
    private String sn;
    private String modelCode;

    public DetectNvrModel(String ip, String port, String manufacturer, String sn, String modelCode) {
        this.ip = ip;
        this.port = port;
        this.manufacturer = manufacturer;
        this.sn = sn;
        this.modelCode = modelCode;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getPort() {
        return port;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getSn() {
        return sn;
    }

    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }

    public String getModelCode() {
        return modelCode;
    }

    @Override
    public String toString() {
        return "DetectNvrModel{" +
                "ip='" + ip + '\'' +
                ", port='" + port + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", sn='" + sn + '\'' +
                ", deviceType='" + modelCode + '\'' +
                '}';
    }
}
