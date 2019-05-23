package com.neuedu.controller.front;


import com.neuedu.common.ServerResponse;
import com.neuedu.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
public class ProductFrontController {
    @Autowired
    IProductService productService;
    /*
    * 查看商品详情
    * */
    @RequestMapping(value = "/detail.do")
    public ServerResponse detail(Integer productId){

        return  productService.detail_portal(productId);
    }
    /*
    * 搜索商品并排序
    * */
    @RequestMapping(value = "/list.do")
    public ServerResponse list(@RequestParam(required = false) Integer categoryId,
                               @RequestParam(required = false)     String keyword,
                               @RequestParam(required = false,defaultValue = "1")Integer pageNum,
                               @RequestParam(required = false,defaultValue = "10")Integer pageSize,
                               @RequestParam(required = false,defaultValue = "")String orderBy){


        return  productService.list_portal(categoryId,keyword,pageNum,pageSize,orderBy);
    }
}
