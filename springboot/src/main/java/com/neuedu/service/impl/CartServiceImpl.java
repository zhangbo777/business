package com.neuedu.service.impl;

import com.google.common.collect.Lists;
import com.neuedu.common.CheckEnum;
import com.neuedu.common.ResponseCode;
import com.neuedu.common.ServerResponse;
import com.neuedu.dao.CartMapper;
import com.neuedu.pojo.Cart;
import com.neuedu.pojo.Product;
import com.neuedu.service.ICartService;
import com.neuedu.service.IProductService;
import com.neuedu.utils.BigDecimalUtils;
import com.neuedu.vo.CartProductVO;
import com.neuedu.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Service
public class CartServiceImpl implements ICartService {
    @Autowired
    IProductService productService;
    @Autowired
    CartMapper cartMapper;
    @Override
    public ServerResponse addCart(Integer userid, Integer productId,Integer count) {
        //参数非空判断
        if(productId==null){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.PARAM_NOT_NULL,"参数不能为空");
        }
        if(count==null){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.PARAM_NOT_NULL,"商品数量不能为0");
        }

        //判断商品是否存在
        ServerResponse serverResponse=productService.findProductById(productId);
        Product product=(Product) serverResponse.getDate();
        if(product==null){

            return ServerResponse.ServerResponsecreateByFail(ResponseCode.PARAM_NOT_NULL,"商品不存在");
        }
        if(product.getStock()<=0){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.PRODUCT_SELL_OUT,"该商品已售罄");
        }



        //判断商品是否在购物车中
       Cart cart= cartMapper.selectProductByUserIdAndProductId(userid,productId);
        if(cart==null){//添加
            Cart newCart=new Cart();
            newCart.setUserId(userid);
            newCart.setProductId(productId);
            newCart.setQuantity(count);
            newCart.setChecked(CheckEnum.CART_PRODUCT_CHACK.getCheck());
            int result=cartMapper.insert(newCart);
            if(result==0){
                return ServerResponse.ServerResponsecreateByFail(ResponseCode.PRODUCT_ADD_CART_ERROR,"添加失败");
            }
        }else{//更新
            cart.setQuantity(cart.getQuantity()+count);
            int result=cartMapper.updateByPrimaryKey(cart);
            if(result==0){
                return ServerResponse.ServerResponsecreateByFail(ResponseCode.PRODUCT_ADD_CART_ERROR,"更新失败");
            }

        }


        //封装购物车对象CartVO
        CartVO cartVO= getcartvo(userid);

        //返回CartVO
        return ServerResponse.ServerResponsecreateBySucess("1",cartVO);


    }

    @Override
    public ServerResponse list(Integer userid) {

        CartVO cartVO=getcartvo(userid);
        return ServerResponse.ServerResponsecreateBySucess("1",cartVO);
    }

    @Override
    public ServerResponse update(Integer userid, Integer productId, Integer count) {
        if(productId==null){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.PARAM_NOT_NULL,"参数不能为空");
        }
        if(count==null){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.PARAM_NOT_NULL,"商品数量不能为0");
        }
        Cart cart=cartMapper.selectProductByUserIdAndProductId(userid,productId);
        if(cart!=null){
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKey(cart);

        }

        CartVO cartVO=getcartvo(userid);
        return ServerResponse.ServerResponsecreateBySucess("1",cartVO);
    }

    @Override
    public ServerResponse deleteproduct(Integer userid, String productIds) {
        if(productIds==null||productIds.equals("")){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.PARAM_NOT_NULL,"参数不能为空");
        }

        List<Integer> productList=Lists.newArrayList();
        String[] productArr=productIds.split(",");
        if(productArr!=null&&productArr.length>0){
            for(String productIdstr:productArr){
                Integer productId=Integer.parseInt(productIdstr);
                productList.add(productId);
            }
        }
        cartMapper.deleteProductByUserIdAndProductIds(userid,productList);

        CartVO cartVO=getcartvo(userid);
        return ServerResponse.ServerResponsecreateBySucess("1",cartVO);
    }

    @Override
    public ServerResponse choiceproduct(Integer userid, Integer productId,Integer check) {

        cartMapper.choiceProductByUserId(userid,productId,check);
        CartVO cartVO=getcartvo(userid);
        return ServerResponse.ServerResponsecreateBySucess("1",cartVO);
    }

    @Override
    public ServerResponse get_product_count(Integer userid) {
        int result=cartMapper.countProductNumber(userid);
        return ServerResponse.ServerResponsecreateBySucess("1",result);
    }

    @Override
    public ServerResponse selectcheckedproduct(Integer userId) {
        List<Cart> list=cartMapper.selectcheckedproduct(userId);
        if(list==null||list.size()==0){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.ERROR,"未选择");
        }
        return ServerResponse.ServerResponsecreateBySucess(list);
    }

    @Override
    public ServerResponse batchDelete(List<Cart> cartList) {
        cartMapper.batchDelete(cartList);
        return ServerResponse.ServerResponsecreateBySucess();
    }


    private CartVO getcartvo(Integer userid){

        CartVO cartVO=new CartVO();

        //根据用户id查询用户购物车信息
        List<Cart> list=cartMapper.selectCartByUserId(userid);
        if(list==null||list.size()==0){
            return cartVO;
        }
        List<CartProductVO> cartProductVOList= Lists.newArrayList();
        int limit_quantity=0;
        String limitQuantity=null;
        BigDecimal caetTotalPrice=new BigDecimal("0");
        for (Cart cart:list){
            CartProductVO cartProductVO = new CartProductVO();
            cartProductVO.setId(cart.getId());
            cartProductVO.setUserId(userid);
            cartProductVO.setProductId(cart.getProductId());

            ServerResponse serverResponse=productService.findProductById(cart.getProductId());
            if(serverResponse.isSucess()){
                Product product=(Product) serverResponse.getDate();
                if(product.getStock()>=cart.getQuantity()){
                    limit_quantity=cart.getQuantity();
                    limitQuantity="LIMIT_NUM_SUCCESS";
                }else{
                    limit_quantity=product.getStock();
                    limitQuantity="LIMIT_NUMFAIL";
                }
                cartProductVO.setQuantity(limit_quantity);
                cartProductVO.setLimitQuantity(limitQuantity);
                cartProductVO.setQuantity(cart.getQuantity());
                cartProductVO.setProductName(product.getName());
                cartProductVO.setProductMainImage(product.getMainImage());
                cartProductVO.setProductSubtitle(product.getSubtitle());
                cartProductVO.setProductPrice(product.getPrice());
                cartProductVO.setProductStatus(product.getStatus());
                cartProductVO.setProductTotalPrice(BigDecimalUtils.mul(product.getPrice().doubleValue(),cart.getQuantity()*1.0));
                cartProductVO.setProductStock(product.getStock());
                cartProductVO.setProductChecked(cart.getChecked());
                cartProductVOList.add(cartProductVO);

                if(cart.getChecked()==CheckEnum.CART_PRODUCT_CHACK.getCheck()){

                    caetTotalPrice=  BigDecimalUtils.add(caetTotalPrice.doubleValue(),cartProductVO.getProductTotalPrice().doubleValue());
                }
            }
            //计算购物车总价格
            cartVO.setCarttotalprice(caetTotalPrice);
            cartVO.setCartProductVOList(cartProductVOList);



            //判断是否全选
           int result= cartMapper.selectNoCheckProduct(userid);
           if(result==0){
               cartVO.setIsallchecked(true);
           }else{
               cartVO.setIsallchecked(false);
           }


            //构建cartVO



        }


        return cartVO;
    }
}
