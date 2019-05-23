package com.neuedu.service;

import com.neuedu.common.ServerResponse;

import java.util.Map;

public interface IOrderService {
     /*
     * 创建订单
     * */
     ServerResponse createorder(Integer userId,Integer shippingId);
     /*
     * 取消订单
     * */
     ServerResponse cancelOrder(Integer userId,Long orderNo);
     /*
     * 获得订单中商品的详细信息
     * */
     ServerResponse getorderproductdeail(Integer userId);
     /*
     * 订单详情
     * */
     ServerResponse orderdetails(Integer userId,Long orderNo);
     /*
     * 订单列表
     * */
     ServerResponse orderlist(Integer userId,Integer pageNum,Integer pageSize);
     /*
     * 支付接口
     * */
     ServerResponse pay(Integer userId,Long orderNo);
     /*
     * 支付宝回调接口
     * */
     String callback(Map<String,String> requestParams);
     /*
     * 查看订单的支付状态
     * */
     ServerResponse selectorderstatus(Long orderNo);
     /*
     * 发货
     * */
     ServerResponse sendgoods(Long orderNo);
}
