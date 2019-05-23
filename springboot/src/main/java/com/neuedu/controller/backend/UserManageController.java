package com.neuedu.controller.backend;


import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.User;
import com.neuedu.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "/admin")
public class UserManageController {
    @Autowired
    IUserService userService;
    @RequestMapping("/login.do")
    public ServerResponse login(HttpSession session, String username, String password){
        ServerResponse serverResponse=userService.login(username,password,0);
        if(serverResponse.isSucess()){
            User user=(User)serverResponse.getDate();
            session.setAttribute(Const.CURRENTUSER,user);

        }
        return serverResponse;
    }
}
