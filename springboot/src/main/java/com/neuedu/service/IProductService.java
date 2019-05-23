package com.neuedu.service;

import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.Product;
import org.springframework.web.bind.annotation.RequestParam;


public interface IProductService {

    public ServerResponse addOrUpdate(Product product);

    public ServerResponse search( String productName, Integer productId,Integer pageNum, Integer pageSize);

    public ServerResponse detail(Integer productId);
    ServerResponse list_portal(Integer categoryId,String keyword,Integer pageNum,Integer pageSize,String orderBy);
    ServerResponse detail_portal(Integer productId);

    /*
    * 根据商品ID查找商品的信息
    * */
    ServerResponse findProductById(Integer productId);

    ServerResponse updateStock(Product product);
    ServerResponse product_dateil(Integer productId);
}
