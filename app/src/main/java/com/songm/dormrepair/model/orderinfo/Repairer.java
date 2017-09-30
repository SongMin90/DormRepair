package com.songm.dormrepair.model.orderinfo;

/**
 * Created by SongM on 2017/9/17.
 */

public class Repairer {

    /**
     * repairerId : 5
     * find : success
     * repairerName : 宋师傅
     * repairerPhone : 13037232106
     */

    private String repairerId;
    private String find;
    private String repairerName;
    private String repairerPhone;

    public String getRepairerId() {
        return repairerId;
    }

    public void setRepairerId(String repairerId) {
        this.repairerId = repairerId;
    }

    public String getFind() {
        return find;
    }

    public void setFind(String find) {
        this.find = find;
    }

    public String getRepairerName() {
        return repairerName;
    }

    public void setRepairerName(String repairerName) {
        this.repairerName = repairerName;
    }

    public String getRepairerPhone() {
        return repairerPhone;
    }

    public void setRepairerPhone(String repairerPhone) {
        this.repairerPhone = repairerPhone;
    }
}
