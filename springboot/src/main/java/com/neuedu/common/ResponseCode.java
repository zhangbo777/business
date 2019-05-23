package com.neuedu.common;

public class ResponseCode {

    /**
     * 成功的状态码
     * */
    public static final  int SUCESS=0;

    /**
     * 失败时通用状态码
     * */
    public static  final int ERROR=100;


    /**
     * 参数不能为空
     * */
    public static final  int PARAM_NOT_NULL=1;
    /**
     * 用户名已存在
     * */
    public static final  int USERNAME_EXISTS=2;
    /**
     * 邮箱已存在
     * */
    public static final  int EMAIL_EXISTS=3;
    /*
    * 用户名不能为空
    * */
    public static final int USER_NOT_NULL=4;

    /*
    * 密码不可为空
    * */
    public static final int PASSWORD_NOT_NULL=5;
    /*
    * 用户名不存在
    * */
    public static final int USER_NOT_EXISTS=6;
    /*
    * 密码错误
    * */

    public static final int PASSWORD_INCORRECT=7;

    /*
    * 密保问题错误
    * */
    public static final int SECRETPROTECTION_ERROR=8;

    /*
    * 未登录
    * */
    public static final int USER_NOT_LOGIN=9;
    /*
    * 登录越权
    * */
    public static final int LOGIG_ULTRA_VIRES=10;
    /*
    * 未查到问题
    * */
    public static final int QUESTION_NOT_SEEK=11;
    /*
    * 答案错误
    * */
    public static final int ANSWER_ERROR=12;
    /*
    * token过期
    * */
    public static final int TOKEN_OVERDUE=13;
    /*
    * token失效
    * */
    public static final int TOKEN_INVALID=14;
    /*
    * 无权限
    * */
    public static final int USER_NOT_VIERS=15;
    /*
    * 添加失败
    * */
    public static final int ADD_ERROR=16;
    /*
    * 更新失败
    * */
    public static final int UPDATE_ERROR=17;

    /*
    * 图片不能为空
    * */
    public static final int PICTURE_NOT_NULL=18;
    /*
    * 未查到商品
    * */
    public static final int PRODUCT_NOT_FIND=19;
    /*
    * 商品已下架
    * */
    public static final int  PRODUCT_LOWER_SHELF=20;
    /*
    * 商品售罄
    * */
    public static final int PRODUCT_SELL_OUT=21;
    /*
    * 商品添加购物车失败
    * */
    public static final int PRODUCT_ADD_CART_ERROR=22;
    /*
    * 创建地址失败
    * */
    public static final int CREATE_SHIPPING_ERROR=23;
    /*
     * 更新地址失败
     * */
    public static final int UPDATE_SHIPPING_ERROR=24;
    /*
    * 订单不存在
    * */
    public static final int ORDER_NOT_EXISTENCE=25;
    /*
    * 订单错误
    * */
    public static final int ORDER_ERROR=26;
    /*
    * 购物车为空
    * */
    public static final int CART_NULL=27;


}
