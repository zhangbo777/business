package com.neuedu.common;

public enum CheckEnum {

    CART_PRODUCT_CHACK(1,"已选中"),
    CART_PRODUCT_UNCHACK(0,"未选中")
    ;
    private int check;
    private String desc;
    CheckEnum(int check,String desc){
        this.desc=desc;
        this.check=check;
    }

    public int getCheck() {
        return check;
    }

    public void setCheck(int check) {
        this.check = check;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
