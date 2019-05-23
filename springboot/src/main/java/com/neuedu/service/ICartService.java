package com.neuedu.service;

import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.Cart;

import java.util.List;


public interface ICartService {

     ServerResponse addCart(Integer userid, Integer productId,Integer count);
     ServerResponse list(Integer userid);
     ServerResponse update(Integer userid, Integer productId,Integer count);
     ServerResponse deleteproduct(Integer userid, String productIds);
     ServerResponse choiceproduct(Integer userid,Integer productId,Integer check);
     ServerResponse get_product_count(Integer userid);
     ServerResponse selectcheckedproduct(Integer userId);
     ServerResponse batchDelete(List<Cart> cartList);

}
