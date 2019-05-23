package com.neuedu.controller.front;

import com.neuedu.common.CheckEnum;
import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.User;
import com.neuedu.service.ICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    ICartService carrtService;

    @RequestMapping("/addcart.do")
    public ServerResponse addCart(HttpSession session, Integer productId,Integer count){
        User user=(User) session.getAttribute(Const.CURRENTUSER);

        return carrtService.addCart(user.getId(),productId,count);
    }
    /*
    * 获取用户购物车列表
    *
    * */
    @RequestMapping("/list.do")
    public ServerResponse list(HttpSession session){
        User user=(User) session.getAttribute(Const.CURRENTUSER);

        return carrtService.list(user.getId());
    }
    /*
    * 更新购物车中商品的数量
    * */
    @RequestMapping("/update.do")
    public ServerResponse update(HttpSession session, Integer productId,Integer count){
        User user=(User) session.getAttribute(Const.CURRENTUSER);

        return carrtService.update(user.getId(),productId,count);
    }
    /*
    * 移除购物车中的商品
    * */
    @RequestMapping("/deleteproduct.do")
    public ServerResponse deleteproduct(HttpSession session,String productIds){

        User user=(User) session.getAttribute(Const.CURRENTUSER);

        return carrtService.deleteproduct(user.getId(),productIds);
    }
    /*
    * 选中购物车中某个商品
    * */
    @RequestMapping("/choiceproduct.do")
    public ServerResponse choiceproduct(HttpSession session,Integer productId){

        User user=(User) session.getAttribute(Const.CURRENTUSER);

        return carrtService.choiceproduct(user.getId(),productId, CheckEnum.CART_PRODUCT_CHACK.getCheck());
    }
    /*
     * 取消选中购物车中某个商品
     * */
    @RequestMapping("/cancelproduct.do")
    public ServerResponse cancelproduct(HttpSession session,Integer productId){

        User user=(User) session.getAttribute(Const.CURRENTUSER);

        return carrtService.choiceproduct(user.getId(),productId, CheckEnum.CART_PRODUCT_UNCHACK.getCheck());
    }
    /*
    * 全选
    * */
    @RequestMapping("/allcheck.do")
    public ServerResponse allcheck(HttpSession session){

        User user=(User) session.getAttribute(Const.CURRENTUSER);

        return carrtService.choiceproduct(user.getId(),null, CheckEnum.CART_PRODUCT_CHACK.getCheck());
    }
    /*
    * 取消全选
    * */
    @RequestMapping("/allnotcheck.do")
    public ServerResponse allnotcheck(HttpSession session){

        User user=(User) session.getAttribute(Const.CURRENTUSER);

        return carrtService.choiceproduct(user.getId(),null, CheckEnum.CART_PRODUCT_UNCHACK.getCheck());
    }
    /*
    * 获取购物车中产品的数量
    * */
    @RequestMapping("/getproductcount.do")
    public ServerResponse get_product_count(HttpSession session){
        User user=(User) session.getAttribute(Const.CURRENTUSER);

        return carrtService.get_product_count(user.getId());

    }

}
