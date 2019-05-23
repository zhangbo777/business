package com.neuedu.common;

public enum Role {
    ROLE_ADMIN(0,"管理员"),
    ROLE_USER(1,"普通用户")
    ;
    private int role;
    private String desc;
     Role(int role,String desc){
        this.desc=desc;
        this.role=role;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
