package com.neuedu.service;

import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.Category;

public interface ICategoryService {

    public ServerResponse add_category(Category category);
    /*
     *修改品类名字
     * */
    public ServerResponse set_category_name(Category category);
    /*
     * 获取平级品类子节点
     * */
    public ServerResponse get_category(Integer categoryId);

    /*
     * 获取当前分类ID及递归子节点的categoryId
     * */
    public ServerResponse get_deep_category(Integer categoryId);

    public ServerResponse selectCategory(Integer categoryId);

    ServerResponse selectByPrymary(Integer categoryId);
}
