package com.phyrelinx.cp.cmark;

public class ZipFIlenames {
    private String deviceid,time;

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "ZipFIlenames{" +
                "deviceid='" + deviceid + '\'' +
                ", time='" + time + '\'' +
                '}';
    }

    public String getFilename(){
        return CARUtil.getToday()+"_"+deviceid+"_"+time+".zip";
    }
}
