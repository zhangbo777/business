package com.neuedu.service;

import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.User;

import javax.servlet.http.HttpSession;

public interface IUserService {

    ServerResponse register(User user);
    ServerResponse login(String username,String password,int type);
    ServerResponse login1(User user);
    ServerResponse chagepassword(User user,String oldpassword,String newpassword);

    ServerResponse check_valid(String str,String type);
    ServerResponse update_information(User user);

    ServerResponse findquestionByUsername(String username);
    ServerResponse check_question(String username,String question,String answer);
    ServerResponse change_password_forget(String username,String newpassword,String forgettoken);

}
