package com.songm.dormrepair.model.about;

/**
 * Created by SongM on 2017/9/21.
 * APP信息
 */

public class App {

    /**
     * updateInfo : 测试阶段
     * version : 1.1
     */

    private String updateInfo;
    private double version;

    public String getUpdateInfo() {
        return updateInfo;
    }

    public void setUpdateInfo(String updateInfo) {
        this.updateInfo = updateInfo;
    }

    public double getVersion() {
        return version;
    }

    public void setVersion(double version) {
        this.version = version;
    }
}
