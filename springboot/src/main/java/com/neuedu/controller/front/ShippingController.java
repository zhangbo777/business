package com.neuedu.controller.front;


import com.neuedu.common.Const;
import com.neuedu.common.ResponseCode;
import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.Shipping;
import com.neuedu.pojo.User;
import com.neuedu.service.IShippingService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;


@RestController
@RequestMapping("/cart/shipping")
public class ShippingController {
    @Autowired
    IShippingService shippingService;
    @RequestMapping("/addshipping.do")
    public ServerResponse addshipping(HttpSession session,Shipping shipping){
        User user = (User) session.getAttribute(Const.CURRENTUSER);
        if(user==null){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.ERROR,"请登录");
        }
        return shippingService.addshipping(user.getId(),shipping);
    }
    @RequestMapping("/delshipp.do")
    public ServerResponse deleteshipp(HttpSession session,Integer id){
        User user = (User) session.getAttribute(Const.CURRENTUSER);
        if(user==null){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.ERROR,"请登录");
        }
        return shippingService.deleteshipp(id);
    }
    @RequestMapping("/selectshipp.do")
    public ServerResponse seleteshipp (HttpSession session,Integer id){
        User user = (User) session.getAttribute(Const.CURRENTUSER);
        if(user==null){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.ERROR,"请登录");
        }
        return shippingService.seleteeshipp(id);
    }
    @RequestMapping("/list.do")
    public ServerResponse list(@RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                               @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize,
                               HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENTUSER);
        if(user==null){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.ERROR,"请登录");
        }

        return shippingService.list(user.getId(),pageNum,pageSize);

    }


}
