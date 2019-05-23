package com.neuedu.controller.backend;


import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.Category;
import com.neuedu.pojo.User;
import com.neuedu.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "/manage/category")
public class CategoryController {

    @Autowired
    ICategoryService categoryService;

    /*
    * 添加类别
    * */
    @RequestMapping("/add_category.do")
    public ServerResponse add_category(Category category){


        return categoryService.add_category(category);
    }
    /*
    *修改品类名字
    * */
    @RequestMapping("/set_category_name.do")
    public ServerResponse set_category_name(Category category){

        return categoryService.set_category_name(category);
    }
    /*
    * 获取平级品类子节点
    * */
    @RequestMapping("/get_category.do")
    public ServerResponse get_category(Integer categoryId){
        return categoryService.get_category(categoryId);
    }

    /*
    * 获取当前分类ID及递归子节点的categoryId
    * */
    @RequestMapping("/get_deep_category.do")
    public ServerResponse get_deep_category(Integer categoryId){

        return categoryService.get_deep_category(categoryId);
    }

}
