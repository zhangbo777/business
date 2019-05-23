package com.neuedu.controller.front;



import com.neuedu.common.Const;
import com.neuedu.common.ResponseCode;
import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.User;
import com.neuedu.service.IUserService;
import com.sun.org.apache.xpath.internal.SourceTree;
import org.apache.catalina.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value= "/user")
public class UserController {

    @Autowired
    IUserService userService;
    /*
    * 注册
    * */
    @RequestMapping("/register.do")
    public ServerResponse register(User user){


        return userService.register(user);
    }
    /*
    * 登录
    * */
    @RequestMapping("/login.do")
    public ServerResponse login(HttpSession session, String username, String password){
        ServerResponse serverResponse=userService.login(username,password,1);
        if(serverResponse.isSucess()){
            User user=(User)serverResponse.getDate();
            session.setAttribute(Const.CURRENTUSER,user);

        }
        return serverResponse;
    }
   /*
   * 登录
   * */
    @RequestMapping("/login1.do")
    public ServerResponse login1(User user){
        return userService.login1(user);
    }
    /*
    * 退出
    * */
    @RequestMapping("/logout.do")
    public ServerResponse logout(HttpSession session){
        session.removeAttribute(Const.CURRENTUSER);
        User user=(User)session.getAttribute(Const.CURRENTUSER);
        if(user==null){
            return ServerResponse.ServerResponsecreateBySucess("退出成功");
        }
        return ServerResponse.ServerResponsecreateByFail("服务器异常");
    }
    /*
    * 登录状态下修改密码
    * */
    @RequestMapping("/chagepassword.do")
    public ServerResponse chagepassword(HttpSession session,String oldpassword,String newpassword){
        User user=(User)session.getAttribute(Const.CURRENTUSER);

        if(user==null){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.USER_NOT_LOGIN,"当前未登录");
        }
        return userService.chagepassword(user,oldpassword,newpassword);
    }
    /*
    * 校验邮箱、用户名是否存在
    * */
    @RequestMapping("/check_valid.do")
    public ServerResponse check_valid(String str,String type){
        return userService.check_valid(str,type);
    }
    /*
    * 登录状态下更新个人信息
    * */
    @RequestMapping("/update_information.do")
    public ServerResponse update_information(HttpSession session){
        User user =(User)session.getAttribute(Const.CURRENTUSER);
        System.out.println(user.getUsername());
        if(user==null){
            return ServerResponse.ServerResponsecreateByFail("用户未登录");
        }
        return userService.update_information(user);
    }
    /*
    * 获取当前登录用户的信息
    * */
    @RequestMapping("/get_information.do")
    public ServerResponse get_ingotmation(HttpSession session){
        User user =(User)session.getAttribute(Const.CURRENTUSER);
        if(user==null){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.USER_NOT_LOGIN,"当前未登录");
        }
        return ServerResponse.ServerResponsecreateBySucess("1",user);
    }
    /*
    * 根据用户名得到密保问题
    * */
    @RequestMapping("/findquestionByUsername")
    public ServerResponse findquestionByUsername(String username){
        return userService.findquestionByUsername(username);
    }
    /*
    * 校验密保问题
    * */
    @RequestMapping("/check_question")
    public ServerResponse check_question(String username,String question,String answer){
        return  userService.check_question(username,question,answer);
    }
    /*
    * 忘记密码状态下修改密码
    * */
    @RequestMapping("/changePasswordForget")
    public ServerResponse changePasswordForget(String username,String newpassword,String forgettoken){
        return userService.change_password_forget(username,newpassword,forgettoken);
    }



}
