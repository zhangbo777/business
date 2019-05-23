package com.neuedu.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePayRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPayResult;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayMonitorService;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayMonitorServiceImpl;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.service.impl.AlipayTradeWithHBServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.neuedu.alipay.Main;
import com.neuedu.common.Const;
import com.neuedu.common.ResponseCode;
import com.neuedu.common.ServerResponse;
import com.neuedu.dao.OrderItemMapper;
import com.neuedu.dao.OrderMapper;
import com.neuedu.dao.PayInfoMapper;
import com.neuedu.pojo.*;
import com.neuedu.service.ICartService;
import com.neuedu.service.IOrderService;
import com.neuedu.service.IProductService;
import com.neuedu.service.IShippingService;
import com.neuedu.utils.BigDecimalUtils;
import com.neuedu.utils.DateUtils;
import com.neuedu.vo.*;
import com.sun.org.apache.xpath.internal.operations.Or;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.ws.Response;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class OrderServiceImpl implements IOrderService {
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    OrderItemMapper orderItemMapper;
    @Autowired
    ICartService cartService;
    @Autowired
    IProductService productService;
    @Autowired
    IShippingService shippingService;
    @Value("${business.imageHost}")
    private String imageHost;
    @Autowired
    PayInfoMapper payInfoMapper;

    /*
     * 创建订单
     * */
    @Override
    public ServerResponse createorder(Integer userId, Integer shippingId) {
        //参数判断
        if (shippingId == null) {
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.PARAM_NOT_NULL, "收货地址不能为空");
        }
        //获取用户购物车中以选中商品
        ServerResponse serverResponse = cartService.selectcheckedproduct(userId);
        List<Cart> cartList = (List<Cart>) serverResponse.getDate();
        //list<Cart>==>orderItemVO
        ServerResponse serverResponse1 = getCartOrderItem(userId, cartList);
        if (!serverResponse1.isSucess()) {
            return serverResponse1;
        }


        //判断购物车中是否有商品
        List<OrderItem> orderItemList = (List<OrderItem>) serverResponse1.getDate();
        if (orderItemList == null || orderItemList.size() == 0) {
            return ServerResponse.ServerResponsecreateByFail("购物车为空");
        }
        BigDecimal orderTotalPrice = getOrderPrice(orderItemList);

        //开始生成订单
        Order order = createOrder(userId, shippingId, orderTotalPrice);
        if (order == null) {
            return ServerResponse.ServerResponsecreateByFail("订单创建失败");
        }
        //修改商品明细表里的订单编号
        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderNo(order.getOrderNo());
        }
        orderItemMapper.insertBatch(orderItemList);  //批量插入 （以用户购买商品不同的种类分条插入）
        //扣库存
        reduceProductStock(orderItemList);
        //购物车中清空已下单的商品
        cleanCart(cartList);

        //返回，OrderVO
        OrderVO orderVO = assembleOrderVO(order, orderItemList, shippingId);
        return ServerResponse.ServerResponsecreateBySucess(orderVO);
    }

    /*
     * 取消订单
     * */
    @Override
    public ServerResponse cancelOrder(Integer userId, Long orderNo) {
        if (orderNo == null) {
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.PARAM_NOT_NULL, "订单编号不能为空");
        }
        Order order = orderMapper.selectOederByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.ORDER_NOT_EXISTENCE, "该订单不存在");
        }
        if (order.getStatus() == Const.OrderStatusEnum.ORDER_PAYED.getCode()) {
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.ORDER_NOT_EXISTENCE, "订单不可取消");
        }
        order.setStatus(Const.OrderStatusEnum.ORDER_CANCELED.getCode());
        int result = orderMapper.updateByPrimaryKey(order);
        if (result == 0) {
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.ORDER_ERROR, "取消失败");
        }
        return ServerResponse.ServerResponsecreateBySucess("已取消该订单");
    }

    /*
     * 获取用户订单中商品明细
     * */
    @Override
    public ServerResponse getorderproductdeail(Integer userId) {
        ServerResponse serverResponse = cartService.selectcheckedproduct(userId);
        List<Cart> cartList = (List<Cart>) serverResponse.getDate();
        if (cartList == null || cartList.size() == 0) {
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.CART_NULL, "您的购物车为空");
        }
        ServerResponse response = getCartOrderItem(userId, cartList);
        List<OrderItem> orderItemList = (List<OrderItem>) response.getDate();
        List<OrderItemVO> orderItemVOList = Lists.newArrayList();
        for (OrderItem orderItem : orderItemList) {
            OrderItemVO orderItemVO = assembleOrderItemVO(orderItem);
            orderItemVOList.add(orderItemVO);
        }

        CartOrderItemVO cartOrderItemVO = new CartOrderItemVO();
        cartOrderItemVO.setImageHost(imageHost);
        cartOrderItemVO.setOrderItemVOList(orderItemVOList);
        cartOrderItemVO.setTotalPrice(getOrderPrice(orderItemList));
        return ServerResponse.ServerResponsecreateBySucess(cartOrderItemVO);
    }

    /*
     * 根据订单号获取订单详情
     * */
    @Override
    public ServerResponse orderdetails(Integer userId, Long orderNo) {
        if (orderNo == null) {

            return ServerResponse.ServerResponsecreateByFail(ResponseCode.PARAM_NOT_NULL, "订单编号不能为空");
        }
        Order order = orderMapper.findOrderByOrderNo(orderNo);
        List<OrderItem> orderItemList = orderItemMapper.findOrderItemByOrderNo(orderNo);
        OrderVO orderVo = assembleOrderVO(order, orderItemList, order.getShippingId());
        return ServerResponse.ServerResponsecreateBySucess(orderVo);
    }

    @Override
    public ServerResponse orderlist(Integer userId, Integer pageNum, Integer pageSize) {
        Page page = PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = Lists.newArrayList();
        if (userId == null) {//查看全部
            orderList = orderMapper.selectAll();
        } else { //查看某人的
            orderList = orderMapper.findOrderByUserId(userId);
        }
        if (orderList == null || orderList.size() == 0) {
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.ORDER_ERROR, "查不到此订单");
        }
        List<OrderVO> orderVOS = Lists.newArrayList();
        for (Order order : orderList) {
            List<OrderItem> orderItemList = orderItemMapper.findOrderItemByOrderNo(order.getOrderNo());
            OrderVO orderVO = assembleOrderVO(order, orderItemList, order.getShippingId());
            orderVOS.add(orderVO);
        }
        PageInfo pageInfo = new PageInfo(page);
        return ServerResponse.ServerResponsecreateBySucess(pageInfo);
    }

    @Override
    public ServerResponse pay(Integer userId, Long orderNo) {
        if (orderNo == null) {
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.PARAM_NOT_NULL, "订单编号不能为空");
        }
        Order order = orderMapper.findOrderByOrderNo(orderNo);
        if (order == null) {
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.ORDER_ERROR, "没有该订单");
        }
        return pay(order);
    }

    @Override
    public String callback(Map<String, String> requestParams) {
        //订单号
        String orderNo= requestParams.get("out_trade_no");
        //流水号
        String tradeNo=requestParams.get("trade_no");
        //支付状态
        String tradeStatus=requestParams.get("trade_status");
        //支付时间
        String paymetTime=requestParams.get("gmt_payment");

       Order order= orderMapper.findOrderByOrderNo(Long.parseLong(orderNo));

       if(order==null){
           return "fail";
       }
       if(tradeStatus.equals("TRADE_SUCCESS")){
           Order order1=new Order();
           if(order.getStatus()!=20){
               order1.setStatus(Const.OrderStatusEnum.ORDER_PAYED.getCode());
               order1.setPaymentTime(DateUtils.strToDate(paymetTime));
               order1.setOrderNo(Long.parseLong(orderNo));
               int result=orderMapper.updateOrderStatus(order1);
               if(result<0){
                   return "FAIL";
               }
               PayInfo payInfo=new PayInfo();
               payInfo.setOrderNo(Long.parseLong(orderNo));
               payInfo.setUserId(order.getUserId());
               payInfo.setPayPlatform(Const.PaymentEnum.ONLINE.getCode());
               payInfo.setPlatformNumber(tradeNo);
               payInfo.setPlatformStatus(tradeStatus);
               int result1=payInfoMapper.insert(payInfo);
               if(result1<0){
                   return "FAIL";
               }
           }

       }




        return "SUCCESS";
    }

    @Override
    public ServerResponse selectorderstatus(Long orderNo) {
        if(orderNo==null){
            return  ServerResponse.ServerResponsecreateByFail("订单号不能为空");
        }
        Order order= orderMapper.findOrderByOrderNo(orderNo);
        if(order==null){
            return ServerResponse.ServerResponsecreateByFail("订单不存在");
        }
        if(order.getStatus()==Const.OrderStatusEnum.ORDER_PAYED.getCode()){
            return ServerResponse.ServerResponsecreateBySucess(true);
        }
        return ServerResponse.ServerResponsecreateBySucess(false);

    }

    @Override
    public ServerResponse sendgoods(Long orderNo) {
      Order order=  orderMapper.findOrderByOrderNo(orderNo);
      if(order.getStatus()==Const.OrderStatusEnum.ORDER_UN_PAY.getCode()){
          return ServerResponse.ServerResponsecreateByFail("他没给钱");
      }
      if(order.getStatus()==Const.OrderStatusEnum.ORDER_PAYED.getCode()){
          order.setStatus(Const.OrderStatusEnum.ORDER_SEND.getCode());
          int result=orderMapper.updateOrderStatus(order);
          if(result==0){
              return ServerResponse.ServerResponsecreateByFail("失效的订单");
          }
      }
        return ServerResponse.ServerResponsecreateBySucess("已发货");
    }


    private static Log log = LogFactory.getLog(Main.class);

    // 支付宝当面付2.0服务
    private static AlipayTradeService tradeService;

    // 支付宝当面付2.0服务（集成了交易保障接口逻辑）
    private static AlipayTradeService tradeWithHBService;

    // 支付宝交易保障接口服务，供测试接口api使用，请先阅读readme.txt
    private static AlipayMonitorService monitorService;

    static {
        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

        // 支付宝当面付2.0服务（集成了交易保障接口逻辑）
        tradeWithHBService = new AlipayTradeWithHBServiceImpl.ClientBuilder().build();

        /** 如果需要在程序中覆盖Configs提供的默认参数, 可以使用ClientBuilder类的setXXX方法修改默认参数 否则使用代码中的默认设置 */
        monitorService = new AlipayMonitorServiceImpl.ClientBuilder()
                .setGatewayUrl("http://mcloudmonitor.com/gateway.do").setCharset("GBK")
                .setFormat("json").build();
    }

    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
    }

    // 测试当面付2.0生成支付二维码
    public ServerResponse pay(Order order) {
        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = String.valueOf(order.getOrderNo());

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = "睿乐购购物商场";

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = String.valueOf(order.getPayment());

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = "购买商品共" + order.getPayment() + "元";

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";
        //根据order查询订单明细
        List<OrderItem> orderItemList = orderItemMapper.findOrderItemByOrderNo(order.getOrderNo());
        if (orderItemList == null || orderItemList.size() == 0) {
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.ORDER_ERROR, "没有可购买的商品");
        }


        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();

        for(OrderItem orderItem:orderItemList){
            // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
            GoodsDetail goods1 = GoodsDetail.newInstance(orderItem.getProductId().toString(), orderItem.getProductName(), orderItem.getCurrentUnitPrice().intValue(), orderItem.getQuantity());
            // 创建好一个商品后添加至商品明细列表
            goodsDetailList.add(goods1);
        }




        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl("http://ie9h3s.natappfree.cc/cart/order/callback.do")//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                // 需要修改为运行机器上的路径
                String filePath = String.format("f:/upload/qr-%s.png",
                        response.getOutTradeNo());
                log.info("filePath:" + filePath);
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, filePath);
                payVO payVO=new payVO(order.getOrderNo(),imageHost+"qr-"+response.getOutTradeNo()+".png");
                return ServerResponse.ServerResponsecreateBySucess(payVO);


            case FAILED:
                log.error("支付宝预下单失败!!!");
                break;

            case UNKNOWN:
                log.error("系统异常，预下单状态未知!!!");
                break;

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                break;
        }
        return ServerResponse.ServerResponsecreateByFail("下单失败");
    }






    /*
    * 判断用户购物车中商品是否勾选，并将其转换为cartVO （判断库存）
    * */
    private  ServerResponse getCartOrderItem(Integer userId,List<Cart> cartList){

        if(cartList==null||cartList.size()==0){
            return ServerResponse.ServerResponsecreateByFail("购物车空");
        }
        List<OrderItem> orderItemList= Lists.newArrayList();

        for(Cart cart:cartList){

            OrderItem orderItem=new OrderItem();
            orderItem.setUserId(userId);
            ServerResponse serverResponse=productService.product_dateil(cart.getProductId());
            Product product=(Product)serverResponse.getDate();
            if(product==null){
                return  ServerResponse.ServerResponsecreateByFail("id为"+cart.getProductId()+"的商品不存在");
            }
            if(product.getStatus() == Const.product_status.STATUS_LOWER.getStatus()){//商品下架
                return ServerResponse.ServerResponsecreateByFail("id为"+product.getId()+"的商品已经下架");
            }
            if(product.getStock()<cart.getQuantity()){//库存不足
                return ServerResponse.ServerResponsecreateByFail("id为"+product.getId()+"的商品库存不足");
            }
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setProductId(product.getId());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setProductName(product.getName());
            orderItem.setTotalPrice(BigDecimalUtils.mul(product.getPrice().doubleValue(),cart.getQuantity().doubleValue()));

            orderItemList.add(orderItem);
        }

        return  ServerResponse.ServerResponsecreateBySucess(orderItemList);
    }
    /*
    * 计算购物车中以勾选商品的总价格
    * */
    private  BigDecimal getOrderPrice(List<OrderItem> orderItemList){

        BigDecimal bigDecimal=new BigDecimal("0");

        for(OrderItem orderItem:orderItemList){
            bigDecimal=BigDecimalUtils.add(bigDecimal.doubleValue(),orderItem.getTotalPrice().doubleValue());
        }

        return bigDecimal;
    }

    /*
    * 生成订单明细（商品详情）
    * */
    private OrderItemVO assembleOrderItemVO(OrderItem orderItem){
        OrderItemVO orderItemVO=new OrderItemVO();

        if(orderItem!=null){

            orderItemVO.setQuantity(orderItem.getQuantity());
            orderItemVO.setCreateTime(DateUtils.dateToStr(orderItem.getCreateTime()));
            orderItemVO.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
            orderItemVO.setOrderNo(orderItem.getOrderNo());
            orderItemVO.setProductId(orderItem.getProductId());
            orderItemVO.setProductImage(orderItem.getProductImage());
            orderItemVO.setProductName(orderItem.getProductName());
            orderItemVO.setTotalPrice(orderItem.getTotalPrice());

        }

        return orderItemVO;
    }

    /*
    * 收货地址视图
    * */
    private ShippingVO assmbleShippingVO(Shipping shipping){
        ShippingVO shippingVO=new ShippingVO();

        if(shipping!=null){
            shippingVO.setReceiverAddress(shipping.getReceiverAddress());
            shippingVO.setReceiverCity(shipping.getReceiverCity());
            shippingVO.setReceiverDistrict(shipping.getReceiverDistrict());
            shippingVO.setReceiverMobile(shipping.getReceiverMobile());
            shippingVO.setReceiverName(shipping.getReceiverName());
            shippingVO.setReceiverPhone(shipping.getReceiverPhone());
            shippingVO.setReceiverProvince(shipping.getReceiverProvince());
            shippingVO.setReceiverZip(shipping.getReceiverZip());
        }
        return shippingVO;
    }
    /*
    * 生成订单
    * */
    private Order createOrder(Integer userId, Integer shippingId, BigDecimal orderTotalPrice) {
        Order order = new Order();
        order.setOrderNo(generateOrderNO());
        order.setUserId(userId);
        order.setShippingId(shippingId);
        order.setStatus(Const.OrderStatusEnum.ORDER_UN_PAY.getCode());
        //订单金额
        order.setPayment(orderTotalPrice);
        order.setPostage(0);
        order.setPaymentType(Const.PaymentEnum.ONLINE.getCode());

        //保存订单
        int result = orderMapper.insert(order);
        if (result > 0) {
            return order;

        }
        return null;
    }

    /*
    * 生成订单编号（当前时间戳+0-100的随机数）
    * */
    private  Long generateOrderNO(){

        return System.currentTimeMillis()+new Random().nextInt(100);
    }

    /*
     * 扣库存
     * */
    private  void  reduceProductStock(List<OrderItem> orderItemList){

        if(orderItemList!=null&&orderItemList.size()>0){

            for(OrderItem orderItem:orderItemList){
                Integer productId= orderItem.getProductId();
                Integer quantity=orderItem.getQuantity();
                ServerResponse serverResponse=productService.findProductById(productId);
                Product product=(Product) serverResponse.getDate();
                product.setStock(product.getStock()-quantity);
                productService.updateStock(product);
            }

        }

    }
    /**
     * 清空购物车中已选中的商品
     * */

    private  void  cleanCart(List<Cart> cartList){

        if(cartList!=null&&cartList.size()>0){
            cartService.batchDelete(cartList);
        }

    }
    /*
    * 生成orderVO
    * */

    private OrderVO assembleOrderVO(Order order, List<OrderItem> orderItemList, Integer shippingId){
        OrderVO orderVO=new OrderVO();

        List<OrderItemVO> orderItemVOList=Lists.newArrayList();
        for(OrderItem orderItem:orderItemList){
            OrderItemVO orderItemVO= assembleOrderItemVO(orderItem);
            orderItemVOList.add(orderItemVO);
        }
        orderVO.setOrderItemVoList(orderItemVOList);
        orderVO.setImageHost(imageHost);
        ServerResponse serverResponse=shippingService.seleteeshipp(shippingId);
        Shipping shipping=(Shipping) serverResponse.getDate();
        if(shipping!=null){
            orderVO.setShippingId(shippingId);
            ShippingVO shippingVO= assmbleShippingVO(shipping);
            orderVO.setShippingVo(shippingVO);
            orderVO.setReceiverName(shipping.getReceiverName());
        }

        orderVO.setStatus(order.getStatus());
        Const.OrderStatusEnum orderStatusEnum= Const.OrderStatusEnum.codeOf(order.getStatus());
        if(orderStatusEnum!=null){
            orderVO.setStatusDesc(orderStatusEnum.getDesc());
        }

        orderVO.setPostage(0);
        orderVO.setPayment(order.getPayment());
        orderVO.setPaymentType(order.getPaymentType());
        Const.PaymentEnum paymentEnum=Const.PaymentEnum.codeOf(order.getPaymentType());
        if(paymentEnum!=null){
            orderVO.setPaymentTypeDesc(paymentEnum.getDesc());
        }
        orderVO.setOrderNo(order.getOrderNo());



        return orderVO;
    }
}
