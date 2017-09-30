package com.songm.dormrepair.model.myorder;

import java.util.List;

/**
 * Created by SongM on 2017/9/20.
 * 我的报修单列表
 */

public class Order {

    /**
     * find : success
     * orderList : [{"orderId":12,"stuId":"1","orderState":12,"orderStartTime":1505533226000,"orderOverTime":1505558179000,"hmrId":null,"orderRoom":"A1-101","orderSort":3,"adminId":null,"repairerId":5,"orderInfo":"报修测试！","listImgUrl":"imgs/2017_09_16/11_40_26/46.jpg"}]
     */

    private String find;
    private List<OrderListBean> orderList;

    public String getFind() {
        return find;
    }

    public void setFind(String find) {
        this.find = find;
    }

    public List<OrderListBean> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<OrderListBean> orderList) {
        this.orderList = orderList;
    }

    public static class OrderListBean {
        /**
         * orderId : 12
         * stuId : 1
         * orderState : 12
         * orderStartTime : 1505533226000
         * orderOverTime : 1505558179000
         * hmrId : null
         * orderRoom : A1-101
         * orderSort : 3
         * adminId : null
         * repairerId : 5
         * orderInfo : 报修测试！
         * listImgUrl : imgs/2017_09_16/11_40_26/46.jpg
         */

        private int orderId;
        private String stuId;
        private int orderState;
        private long orderStartTime;
        private long orderOverTime;
        private Object hmrId;
        private String orderRoom;
        private int orderSort;
        private Object adminId;
        private int repairerId;
        private String orderInfo;
        private String listImgUrl;

        public int getOrderId() {
            return orderId;
        }

        public void setOrderId(int orderId) {
            this.orderId = orderId;
        }

        public String getStuId() {
            return stuId;
        }

        public void setStuId(String stuId) {
            this.stuId = stuId;
        }

        public int getOrderState() {
            return orderState;
        }

        public void setOrderState(int orderState) {
            this.orderState = orderState;
        }

        public long getOrderStartTime() {
            return orderStartTime;
        }

        public void setOrderStartTime(long orderStartTime) {
            this.orderStartTime = orderStartTime;
        }

        public long getOrderOverTime() {
            return orderOverTime;
        }

        public void setOrderOverTime(long orderOverTime) {
            this.orderOverTime = orderOverTime;
        }

        public Object getHmrId() {
            return hmrId;
        }

        public void setHmrId(Object hmrId) {
            this.hmrId = hmrId;
        }

        public String getOrderRoom() {
            return orderRoom;
        }

        public void setOrderRoom(String orderRoom) {
            this.orderRoom = orderRoom;
        }

        public int getOrderSort() {
            return orderSort;
        }

        public void setOrderSort(int orderSort) {
            this.orderSort = orderSort;
        }

        public Object getAdminId() {
            return adminId;
        }

        public void setAdminId(Object adminId) {
            this.adminId = adminId;
        }

        public int getRepairerId() {
            return repairerId;
        }

        public void setRepairerId(int repairerId) {
            this.repairerId = repairerId;
        }

        public String getOrderInfo() {
            return orderInfo;
        }

        public void setOrderInfo(String orderInfo) {
            this.orderInfo = orderInfo;
        }

        public String getListImgUrl() {
            return listImgUrl;
        }

        public void setListImgUrl(String listImgUrl) {
            this.listImgUrl = listImgUrl;
        }
    }
}
