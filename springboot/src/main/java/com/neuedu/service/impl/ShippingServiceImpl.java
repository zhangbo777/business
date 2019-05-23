package com.neuedu.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.neuedu.common.ResponseCode;
import com.neuedu.common.ServerResponse;
import com.neuedu.dao.ShippingMapper;
import com.neuedu.pojo.Shipping;
import com.neuedu.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.ws.Response;
import java.util.List;

@Service
public class ShippingServiceImpl implements IShippingService {
    @Autowired
    ShippingMapper shippingMapper;

    /*
    * 添加或更新 收货地址
    * */
    @Override
    public ServerResponse addshipping(Integer userId, Shipping shipping) {
        if(shipping==null){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.PARAM_NOT_NULL,"参数不能为空");
        }
        shipping.setUserId(userId);
        if(shipping.getId()==null){//添加
            int result=shippingMapper.insert(shipping);
            if(result!=0){
                return ServerResponse.ServerResponsecreateBySucess("新建地址成功",shipping.getId());
            }else{
                return ServerResponse.ServerResponsecreateByFail(ResponseCode.CREATE_SHIPPING_ERROR,"新建地址失败");
            }
        }else{ //更新

            int result=shippingMapper.updateByPrimaryKey(shipping);
            if(result!=0){
                return ServerResponse.ServerResponsecreateBySucess("更新地址成功");
            }else {
                return ServerResponse.ServerResponsecreateByFail(ResponseCode.UPDATE_SHIPPING_ERROR, "更新地址失败");
            }
        }


    }

    @Override
    public ServerResponse deleteshipp(Integer id) {
        if(id==null){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.PARAM_NOT_NULL,"参数不能为空");
        }
        int result=shippingMapper.deleteByPrimaryKey(id);
        if(result!=0){
            return ServerResponse.ServerResponsecreateBySucess("删除地址成功");
        }
        return ServerResponse.ServerResponsecreateByFail("删除地址失败");
    }

    @Override
    public ServerResponse seleteeshipp(Integer id) {
        if(id==null){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.PARAM_NOT_NULL,"参数不能为空");
        }
        Shipping shipping=shippingMapper.selectByPrimaryKey(id);
        if(shipping==null){
            return ServerResponse.ServerResponsecreateByFail("请登录后查询");
        }
        return ServerResponse.ServerResponsecreateBySucess(shipping);
    }

    @Override
    public ServerResponse list(Integer userId, Integer pageNum, Integer pageSize) {

        Page page=PageHelper.startPage(pageNum,pageSize);
        List<Shipping> list = shippingMapper.selectByUserId(userId);

        PageInfo pageInfo=new PageInfo(page);

        return ServerResponse.ServerResponsecreateBySucess(pageInfo);
    }
}
