package com.neuedu.controller.front;


import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.User;
import com.neuedu.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

@RestController
@RequestMapping("/cart/order")
public class OrderController {
    @Autowired
    IOrderService orderService;


    @RequestMapping("/createoeder.do")
    public ServerResponse createorder(HttpSession session,Integer shippingId){
        User user=(User)session.getAttribute(Const.CURRENTUSER);
        return orderService.createorder(user.getId(),shippingId);

    }

    /*
    * 获取订单的商品信息
    * */
    @RequestMapping("/getOrderProductDetail.do")
    public ServerResponse get_order_product_detail(HttpSession session){
        User user=(User)session.getAttribute(Const.CURRENTUSER);
        return orderService.getorderproductdeail(user.getId());
    }
    /*
    * 订单列表
    * */
    @RequestMapping("/orderlist.do")
    public ServerResponse orderlist(HttpSession session,
                                    @RequestParam(required = false,defaultValue = "1")Integer pageNum,
                                    @RequestParam(required = false,defaultValue = "10")Integer pageSize){
        User user=(User)session.getAttribute(Const.CURRENTUSER);
        return orderService.orderlist(user.getId(),pageNum,pageSize);
    }
    /*
    * 订单详情
    * */
    @RequestMapping("/orderdetails.do")
    public ServerResponse orderdetails(HttpSession session,Long orderNo){
        User user=(User)session.getAttribute(Const.CURRENTUSER);
        return orderService.orderdetails(user.getId(),orderNo);

    }
    /*
    * 取消订单
    * */
    @RequestMapping("/cancelorder.do")
    public ServerResponse cancelorder(HttpSession session,Long orderNo){
        User user=(User)session.getAttribute(Const.CURRENTUSER);

        return orderService.cancelOrder(user.getId(),orderNo);
    }

    /*
    * 支付
    * */
    @RequestMapping("/pay.do")
    public ServerResponse pay(HttpSession session,Long orderNo){
        User user=(User)session.getAttribute(Const.CURRENTUSER);

        return orderService.pay(user.getId(),orderNo);

    }
    /*
    * 支付宝回调页面
    * */
    @RequestMapping("/callback.do")
    public String alipay_callback(HttpServletRequest request){
        Map<String,String[]> callbackMap=request.getParameterMap();
        Map<String,String> requestparams= Maps.newHashMap();
        Iterator<String> iterator=callbackMap.keySet().iterator();

        while(iterator.hasNext()){
           String key= iterator.next();
           String[] values=callbackMap.get(key);
            StringBuffer value=new StringBuffer();
            if(values!=null&&values.length>0){
                for(int i=0;i<values.length;i++){
                    value.append(values[i]);
                    /*if(1!=values.length-1){
                        value.append(",");
                    }*/
                }
            }

            requestparams.put(key,value.toString());
        }
        System.out.println(requestparams);

        try {
            requestparams.remove("sign_type");

            boolean result= AlipaySignature.rsaCheckV2(requestparams,Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());
            if(result){
                System.out.println("111");
                return orderService.callback(requestparams);

            }else{
                System.out.println("222");
                return "fail";
            }

        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        return "success";
    }
   /*
   * 查询订单的支付状态
   * */
    @RequestMapping(value = "/selectorderstatus.do")
    public ServerResponse query_order_pay_status(HttpSession session,Long orderNo){

        User user=(User) session.getAttribute(Const.CURRENTUSER);


        return orderService.selectorderstatus(orderNo);
    }

    /*
    * 发货
    * */
    @RequestMapping("/sendgoods")
    public ServerResponse send_goods(Long orderNo){

        return orderService.sendgoods(orderNo);
    }


}
