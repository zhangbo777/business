package com.neuedu.controller.backend;

import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.Product;
import com.neuedu.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/manage/product")
public class ProductController {
    @Autowired
    IProductService productService;

    /*
    * 添加/更新
    * */
    @RequestMapping("/addorupdate.do")
    public ServerResponse addOrUpdate(Product product){

        return productService.addOrUpdate(product);
    }


    /*
    * 搜索
    * */
    @RequestMapping("/search.do")
    public ServerResponse search(@RequestParam(name = "productName",required = false) String productName,
                                 @RequestParam(name = "productId",required = false) Integer productId,
                                 @RequestParam(name = "pageNum",required = false,defaultValue = "1") Integer pageNum,
                                 @RequestParam(name = "pageSize",required = false,defaultValue = "10") Integer pageSize){



        return productService.search(productName, productId, pageNum, pageSize);

    }
    @RequestMapping("/detail.do")
    public ServerResponse detail(Integer productId){
        return productService.detail(productId);
    }
}

