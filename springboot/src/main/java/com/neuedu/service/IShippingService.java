package com.neuedu.service;

import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.Shipping;


public interface IShippingService {

   ServerResponse addshipping(Integer userId, Shipping shipping);
    ServerResponse deleteshipp(Integer id);
    ServerResponse seleteeshipp(Integer id);
    ServerResponse list(Integer userId,Integer pageNum,Integer pageSize);

}
