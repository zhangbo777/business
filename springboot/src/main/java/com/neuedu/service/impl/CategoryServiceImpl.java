package com.neuedu.service.impl;

import com.google.common.collect.Sets;
import com.neuedu.common.ResponseCode;
import com.neuedu.common.ServerResponse;
import com.neuedu.dao.CategoryMapper;
import com.neuedu.pojo.Category;
import com.neuedu.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Service
public class CategoryServiceImpl implements ICategoryService {
    @Autowired
    CategoryMapper categoryMapper;


    @Override
    public ServerResponse add_category(Category category) {
        if(category==null){

            return ServerResponse.ServerResponsecreateByFail(ResponseCode.PARAM_NOT_NULL,"参数不能为空");
        }

        int result =categoryMapper.insert(category);
        if(result==0){

            return ServerResponse.ServerResponsecreateByFail("添加失败");
        }

        return ServerResponse.ServerResponsecreateBySucess();

    }

    @Override
    public ServerResponse set_category_name(Category category) {
        if(category==null){

            return ServerResponse.ServerResponsecreateByFail(ResponseCode.PARAM_NOT_NULL,"参数不能为空");
        }
        if(category.getId()==null){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.PARAM_NOT_NULL,"Idc参数不能为空");
        }
        int result=categoryMapper.updateByPrimaryKey(category);
        if(result==0){

            return ServerResponse.ServerResponsecreateByFail("更新失败");
        }

        return ServerResponse.ServerResponsecreateBySucess();
    }

    @Override
    public ServerResponse get_category(Integer categoryId) {

        if(categoryId==null){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.PARAM_NOT_NULL,"参数不能为空");
        }
        List<Category> list=categoryMapper.selectcategoryById(categoryId);

        return ServerResponse.ServerResponsecreateBySucess("已查到",list);
    }

    @Override
    public ServerResponse get_deep_category(Integer categoryId) {
        if(categoryId==null){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.PARAM_NOT_NULL,"参数不可为空");
        }
        Set<Category> categorySet= Sets.newHashSet();
        Set<Category> categorySet1=findAllChildCategory(categorySet,categoryId);
        Set<Integer> categoryIds=Sets.newHashSet();
        Iterator<Category> iterator=categorySet1.iterator();
        while(iterator.hasNext()){
            Category category=iterator.next();
            categoryIds.add(category.getId());
        }
        return ServerResponse.ServerResponsecreateBySucess("已查到",categoryIds);
    }

    @Override
    public ServerResponse selectCategory(Integer categoryId) {
        if(categoryId==null){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.PARAM_NOT_NULL,"参数不可为空");
        }
        Category category=categoryMapper.selectByPrimaryKey(categoryId);
        if(category==null){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.PRODUCT_NOT_FIND,"未找到");
        }
        return ServerResponse.ServerResponsecreateBySucess("已找到",category);
    }

    @Override
    public ServerResponse selectByPrymary(Integer categoryId) {
        Category category=  categoryMapper.selectByPrimaryKey(categoryId);
        return ServerResponse.ServerResponsecreateBySucess("已找到",category);
    }

    public Set<Category> findAllChildCategory(Set<Category> categorySet,Integer categoryId){

    Category category=categoryMapper.selectByPrimaryKey(categoryId);
    if(category!=null){
        categorySet.add(category);
    }
    List<Category> categoryList=categoryMapper.selectcategoryById(categoryId);
    if(categoryList!=null&&categoryList.size()>0){
        for(Category category1:categoryList){
                findAllChildCategory(categorySet,category1.getId());

        }
    }

        return categorySet;
    }
}
