package com.songm.dormrepair.model.orderinfo;

import java.util.List;

/**
 * Created by SongM on 2017/9/16.
 * 报修单的图片
 */

public class Img {

    /**
     * find : success
     * imgList : [{"imgId":19,"imgUrl":"imgs/2017_09_16/19_47_43/9198790211652.jpg","imgDatetime":1505562463000,"orderId":14},{"imgId":20,"imgUrl":"imgs/2017_09_16/19_47_43/919879021165226867827.jpg","imgDatetime":1505562463000,"orderId":14},{"imgId":21,"imgUrl":"imgs/2017_09_16/19_47_43/9198790211652268678.jpg","imgDatetime":1505562463000,"orderId":14},{"imgId":22,"imgUrl":"imgs/2017_09_16/19_47_43/91987902116522686.jpg","imgDatetime":1505562463000,"orderId":14},{"imgId":23,"imgUrl":"imgs/2017_09_16/19_47_43/919879021165226.jpg","imgDatetime":1505562463000,"orderId":14}]
     */

    private String find;
    private List<ImgListBean> imgList;

    public String getFind() {
        return find;
    }

    public void setFind(String find) {
        this.find = find;
    }

    public List<ImgListBean> getImgList() {
        return imgList;
    }

    public void setImgList(List<ImgListBean> imgList) {
        this.imgList = imgList;
    }

    public static class ImgListBean {
        /**
         * imgId : 19
         * imgUrl : imgs/2017_09_16/19_47_43/9198790211652.jpg
         * imgDatetime : 1505562463000
         * orderId : 14
         */

        private int imgId;
        private String imgUrl;
        private long imgDatetime;
        private int orderId;

        public int getImgId() {
            return imgId;
        }

        public void setImgId(int imgId) {
            this.imgId = imgId;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public long getImgDatetime() {
            return imgDatetime;
        }

        public void setImgDatetime(long imgDatetime) {
            this.imgDatetime = imgDatetime;
        }

        public int getOrderId() {
            return orderId;
        }

        public void setOrderId(int orderId) {
            this.orderId = orderId;
        }
    }
}
