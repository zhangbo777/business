package com.neuedu.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.neuedu.common.Const;
import com.neuedu.common.ResponseCode;
import com.neuedu.common.ServerResponse;
import com.neuedu.dao.CategoryMapper;
import com.neuedu.dao.ProductMapper;
import com.neuedu.pojo.Category;
import com.neuedu.pojo.Product;
import com.neuedu.service.ICategoryService;
import com.neuedu.service.IProductService;
import com.neuedu.vo.ProductDetailVO;
import com.neuedu.vo.ProductListVO;
import com.neuedu.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import javax.xml.ws.Response;
import java.util.List;
import java.util.Set;

@Service
public class ProductServiceImpl implements IProductService {
    @Autowired
    ICategoryService categoryService;
    @Autowired
    ProductMapper productMapper;

    @Value("${business.imageHost}")
    private String imageHost;

    @Override
    public ServerResponse addOrUpdate(Product product) {
        if(product==null){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.PARAM_NOT_NULL,"参数不能为空");

        }
        String subImages=product.getSubImages();
        if(subImages!=null&&!subImages.equals("")){
            String[] subImageArr=subImages.split(",");
            if(subImageArr.length>0){
                product.setMainImage(subImageArr[0]);
            }
        }




        if(product.getId()==null){
            int result=productMapper.insert(product);
            if(result==0){
                return ServerResponse.ServerResponsecreateByFail(ResponseCode.ADD_ERROR,"添加失败");
            }else{
                return ServerResponse.ServerResponsecreateBySucess();
            }
        }else{
            int result=productMapper.updateByPrimaryKey(product);
            if(result==0){

                return ServerResponse.ServerResponsecreateByFail(ResponseCode.UPDATE_ERROR,"更新失败");
            }else{
                return ServerResponse.ServerResponsecreateBySucess();
            }
        }

    }

    @Override
    public ServerResponse search(String productName, Integer productId, Integer pageNum, Integer pageSize) {
        System.out.println("111");
        if(productName!=null){
            productName="%"+productName+"%";
        }

       Page page= PageHelper.startPage(pageNum,pageSize); //向sql语句中加上limit


        List<Product> list= productMapper.findProductByIdOrName(productId, productName);
        List<ProductListVO> lit= Lists.newArrayList();
        System.out.println(list.size());
        if(list!=null&&list.size()>0){
            for(Product product:list){
                ProductListVO productListVO =productToVo(product);
                lit.add(productListVO);
            }
        }

        PageInfo pageInfo=new PageInfo(page);
        return ServerResponse.ServerResponsecreateBySucess("已查到",pageInfo);
    }

    @Override
    public ServerResponse detail(Integer productId) {

        Product product=productMapper.selectByPrimaryKey(productId);

        if(product==null){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.PRODUCT_NOT_FIND,"未找到该商品");
        }
        ProductDetailVO productDetailVO=productDetailToVo(product);
        return ServerResponse.ServerResponsecreateBySucess("已查到",productDetailVO);
    }


    /*
    * 产品搜索及动态排序
    * */
    @Override
    public ServerResponse list_portal(Integer categoryId, String keyword, Integer pageNum, Integer pageSize, String orderBy) {
        //step1:参数校验 categoryId和keyword不能同时为空
        //keyword 按照name 进行模糊查询
        if(categoryId==null&&(keyword==null||keyword.equals(""))){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.PARAM_NOT_NULL,"参数错误");
        }
        //step2:categoryId
        Set<Integer> integerSet= Sets.newHashSet();
        if(categoryId!=null){
            ServerResponse serverResponse1=categoryService.selectByPrymary(categoryId);
            Category category= (Category) serverResponse1 .getDate();
            if(category==null&&(keyword==null||keyword.equals(""))){
                //说明没有商品数据
               Page page= PageHelper.startPage(pageNum,pageSize);
                List<ProductListVO> productListVOList=Lists.newArrayList();
                PageInfo pageInfo=new PageInfo(page);
                return ServerResponse.ServerResponsecreateBySucess("已查到",pageInfo);
            }

            ServerResponse serverResponse= categoryService.get_deep_category(categoryId);

            if(serverResponse.isSucess()){
                integerSet=(Set<Integer>) serverResponse.getDate();
            }
        }
        //step3: keyword
        if(keyword!=null&&!keyword.equals("")){
            keyword="%"+keyword+"%";
        }
        Page page=null;
        if(orderBy.equals("")){
           page= PageHelper.startPage(pageNum,pageSize);
        }else{
            String[] orderByArr=   orderBy.split("_");
            if(orderByArr.length>1){
                page=PageHelper.startPage(pageNum,pageSize,orderByArr[0]+" "+orderByArr[1]);
            }else{
               page= PageHelper.startPage(pageNum,pageSize);
            }
        }
        //step4: List<Product>-->List<ProductListVO>
        List<Product> productList=productMapper.searchProduct(integerSet,keyword,orderBy);
        List<ProductListVO> productListVOList=Lists.newArrayList();
        if(productList!=null&&productList.size()>0){
            for(Product product:productList){
                ProductListVO productListVO=  productToVo(product);
                productListVOList.add(productListVO);
            }
        }

        //step5:分页

        PageInfo pageInfo=new PageInfo(page);
        pageInfo.setList(productListVOList);
        //step6:返回
        return ServerResponse.ServerResponsecreateBySucess("已查到",pageInfo);
    }




    /*
    * 用户查看商品详情
    * */
    @Override
    public ServerResponse detail_portal(Integer productId) {
        if(productId==null){
            return ServerResponse.ServerResponsecreateByFail("商品id参数不能为空");
        }
        //step2: 查询product
        Product product=  productMapper.selectByPrimaryKey(productId);
        if(product==null){
            return ServerResponse.ServerResponsecreateByFail("商品不存在");
        }

        //step3: 校验商品状态
        if(product.getStatus()!= Const.product_status.STATUS_UPPER.getStatus()){
            return ServerResponse.ServerResponsecreateByFail("商品已下架或删除");
        }

        ProductListVO productListVO=productToVo(product);

        //step5:返回结果
        return ServerResponse.ServerResponsecreateBySucess("已查到",productListVO);

    }
    /*
    * 根据商品ID查看商品信息
    * */
    @Override
    public ServerResponse findProductById(Integer productId) {
        if(productId==null){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.PARAM_NOT_NULL,"参数不能为空");
        }
        Product product=productMapper.selectByPrimaryKey(productId);

        return ServerResponse.ServerResponsecreateBySucess("已找到",product);
    }

    @Override
    public ServerResponse updateStock(Product product) {
        productMapper.updatestock(product);
        return ServerResponse.ServerResponsecreateBySucess();
    }

    @Override
    public ServerResponse product_dateil(Integer productId) {

        if(productId==null){
            return ServerResponse.ServerResponsecreateByFail("商品id参数不能为空");
        }
        //step2: 查询product
        Product product=  productMapper.selectByPrimaryKey(productId);
        if(product==null){
            return ServerResponse.ServerResponsecreateByFail("商品不存在");
        }

        //step3: 校验商品状态
        if(product.getStatus()!= Const.product_status.STATUS_UPPER.getStatus()){
            return ServerResponse.ServerResponsecreateByFail("商品已下架或删除");
        }
        return ServerResponse.ServerResponsecreateBySucess(product);
    }


    private ProductDetailVO productDetailToVo(Product product){
        ProductDetailVO productDetailVO=new ProductDetailVO();
        productDetailVO.setCategoryId(product.getCategoryId());
        productDetailVO.setCreateTime(DateUtils.dateToStr(product.getCreateTime()));
        productDetailVO.setDetail(product.getDetail());
        productDetailVO.setImageHost(imageHost);
        productDetailVO.setName(product.getName());
        productDetailVO.setMainImage(product.getMainImage());
        productDetailVO.setId(product.getId());
        productDetailVO.setPrice(product.getPrice());
        productDetailVO.setStatus(product.getStatus());
        productDetailVO.setStock(product.getStock());
        productDetailVO.setSubImages(product.getSubImages());
        productDetailVO.setSubtitle(product.getSubtitle());
        productDetailVO.setUpdateTime(DateUtils.dateToStr(product.getUpdateTime()));
        ServerResponse serverResponse= categoryService.selectCategory(product.getCategoryId());
        Category category=(Category)serverResponse.getDate();
        if(category!=null){
            productDetailVO.setId(category.getId());
        }

        return productDetailVO;
    }



    private ProductListVO productToVo(Product product){
        ProductListVO productListVO =new ProductListVO();
        productListVO.setId(product.getId());
        productListVO.setCategoryId(product.getCategoryId());
        productListVO.setName(product.getName());
        productListVO.setMainImage(product.getMainImage());
        productListVO.setStatus(product.getStatus());
        productListVO.setSubtitle(product.getSubtitle());
        productListVO.setPrice(product.getPrice());

        return productListVO;


    }
}
