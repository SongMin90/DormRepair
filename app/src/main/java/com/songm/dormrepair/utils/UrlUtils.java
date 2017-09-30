package com.songm.dormrepair.utils;

/**
 * Created by SongM on 2017/9/17.
 * URL列表
 */

public class UrlUtils {

    /**
     * HOST
     */
    public static final String HOST = "http://118.89.101.23:8080/";
    /**
     * 通过订单ID查询订单信息
     * @param orderId 订单ID
     * @return
     */
    public static String orderFindByOrderId(int orderId) {
        return HOST + "order/findById?orderId=" + orderId;
    }
    /**
     * 通过订单ID查询图片信息
     * @param orderId 订单ID
     * @return
     */
    public static String imgFindByOrderId(int orderId) {
        return HOST + "img/findByOrderId?orderId=" + orderId;
    }
    /**
     * 通过订单创建人ID查询创建人信息
     * @param isStu 是否为学生
     * @param authorId 创建人ID
     * @return
     */
    public static String authorFindByAuthorId(boolean isStu, String authorId) {
        String str = HOST + "stu/findFullInfo?idOrPhone=" + authorId;
        if(!isStu) {
            str = HOST + "hmr/findFullInfo?idOrPhone=" + authorId;
        }
        return str;
    }
    /**
     * 通过维修员ID查询维修员信息
     * @param repairerId 维修员ID
     * @return
     */
    public static String repairerFindByRepairerId(int repairerId) {
        return HOST + "repairer/findFullInfo?idOrPhone=" + repairerId;
    }
    /**
     * 通过订单ID查询评价
     * @param orderId 订单ID
     * @return
     */
    public static String evalFindByOrderId(int orderId) {
        return HOST + "eval/getEvalByOrderId?orderId=" + orderId;
    }
    /**
     * 维修员抢单
     */
    public static final String grabOrder = HOST + "order/repairerGetOrder";
    /**
     * 维修员维修完成
     */
    public static final String orderIsRepair = HOST + "order/orderIsRepair";
    /**
     * 添加评价
     */
    public static final String addEval = HOST + "eval/addEval";
    /**
     * 学生登录
     */
    public static final String stuLogin = HOST + "stu/login";
    /**
     * 宿管登录
     */
    public static final String hmrLogin = HOST + "hmr/login";
    /**
     * 学生详细信息
     */
    public static String stuInfoByStuIdOrPhone(String idOrPhone) {
        return HOST + "stu/findFullInfo?idOrPhone=" + idOrPhone;
    }
    /**
     * 宿管详细信息
     */
    public static String hmrInfoByHmrIdOrPhone(String idOrPhone) {
        return HOST + "hmr/findFullInfo?idOrPhone=" + idOrPhone;
    }
    /**
     * 学生注册
     */
    public static final String stuRegister = HOST + "stu/register";
    /**
     * 宿管注册
     */
    public static final String hmrRegister = HOST + "hmr/register";
    /**
     * 学生重置密码
     */
    public static final String stuUpdatePwd = HOST + "stu/updatePwd";
    /**
     * 宿管重置密码
     */
    public static final String hmrUpdatePwd = HOST + "hmr/updatePwd";
    /**
     * 新增报修单
     */
    public static final String addOrder = HOST + "order/addOrder";
    /**
     * 上传报修单图片
     */
    public static final String uploadImg = HOST + "img/addImg";
    /**
     * 学生报修单
     */
    public static String orderByStu(int orderState, String stuId) {
        return HOST + "order/getCommittedOrders?orderState=" + orderState + "&stuId=" + stuId;
    }
    /**
     * 宿管报修单
     */
    public static String orderByHmr(int orderState, String stuId) {
        return HOST + "order/getCommittedOrders?orderState=" + orderState + "&hmrId=" + stuId;
    }
    /**
     * 用户头像
     */
    public static String userIcon(String userId, String profession) {
        return HOST + "img/findIconUrl?userId=" + userId + "&profession=" + profession;
    }
    /**
     * 更新用户头像
     */
    public static String updateUserIcon = HOST + "img/updateUserIcon";
    /**
     * 报修端最新版本号
     */
    public static String appBaoxiu = HOST + "webset/getBaoxiuInfo";
}
