package com.neuedu.service.impl;

import com.neuedu.common.Const;
import com.neuedu.common.ResponseCode;
import com.neuedu.common.Role;
import com.neuedu.common.ServerResponse;
import com.neuedu.dao.UserMapper;
import com.neuedu.pojo.User;
import com.neuedu.service.IUserService;
import com.neuedu.utils.MD5Utils;
import com.neuedu.utils.TokenCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.soap.SOAPBinding;
import javax.rmi.CORBA.Util;
import javax.servlet.http.HttpSession;
import java.util.UUID;


@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    /*
    * 注册
    * */
    @Override
    public ServerResponse register(User user) {

       if(user==null){
           return ServerResponse.ServerResponsecreateByFail(ResponseCode.PARAM_NOT_NULL,"参数不能为空");
       }

       int resultuser=userMapper.testuser(user.getUsername());
       if(resultuser>0){
           return ServerResponse.ServerResponsecreateByFail(ResponseCode.USERNAME_EXISTS,"用户名已存在");
       }
       int resultemail=userMapper.testemail(user.getEmail());
       if(resultemail>0){
           return ServerResponse.ServerResponsecreateByFail(ResponseCode.EMAIL_EXISTS,"邮箱已存在");

       }
       /*
       * 密码加密
       * */
       user.setPassword(MD5Utils.getMD5Code(user.getPassword()));
       /*
       * 设置角色类型
       * */
       user.setRole(Role.ROLE_USER.getRole());
        int userregister=userMapper.insert(user);
        if(userregister<=0){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.ERROR,"注册失败");
        }
        return ServerResponse.ServerResponsecreateBySucess();
    }

    /*
    * 登录
    * */
    @Override
    public ServerResponse login(String username, String password,int type) {
        if(username==null||username.equals("")){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.USER_NOT_NULL,"用户名不可为空");

        }
        if(password==null||password.equals("")){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.PASSWORD_NOT_NULL,"密码不能为空");

        }
        int resultuser=userMapper.testuser(username);
        if(resultuser==0){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.USER_NOT_EXISTS,"该用户不存在");
        }
        String _password = MD5Utils.getMD5Code(password);
        User resulUP=userMapper.testUP(username, _password);

        if(resulUP==null){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.PASSWORD_INCORRECT,"密码错误");

        }
        if(type==0){
            if(resulUP.getRole()==Role.ROLE_USER.getRole()){
                return ServerResponse.ServerResponsecreateByFail(ResponseCode.LOGIG_ULTRA_VIRES,"权限不足");
            }
        }
        return ServerResponse.ServerResponsecreateBySucess("登录成功",resulUP);

    }


    @Override
    public ServerResponse login1(User user) {
        if(user.getUsername()==null||user.getUsername().equals("")){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.USER_NOT_NULL,"用户名不可为空");

        }
        if(user.getPassword()==null||user.getPassword().equals("")){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.PASSWORD_NOT_NULL,"密码不能为空");

        }
        User num=userMapper.test1(user.getUsername(),MD5Utils.getMD5Code(user.getPassword()));
        if(num!=null){
            return ServerResponse.ServerResponsecreateBySucess("登录成功",user);
        }

        return ServerResponse.ServerResponsecreateByFail(ResponseCode.PASSWORD_INCORRECT,"密码错误");
    }

    /*
    * 修改密码
    * */
    @Override
    public ServerResponse chagepassword(User user,String oldpassword,String newpassword) {
        if(newpassword==null||newpassword.equals("")){
            return ServerResponse.ServerResponsecreateByFail("新密码不可为空");

        }
        if(oldpassword==null||oldpassword.equals("")){
            return ServerResponse.ServerResponsecreateByFail("旧密码不可为空");

        }
        System.out.println(MD5Utils.getMD5Code(oldpassword));
        System.out.println(user.getPassword());
        if(MD5Utils.getMD5Code(oldpassword).equals(user.getPassword())){
            String _password=MD5Utils.getMD5Code(newpassword);
            int result=userMapper.ModifyPassword(user.getUsername(),_password);
            if(result!=0){
                return ServerResponse.ServerResponsecreateBySucess("修改成功");
            }

        }
        return ServerResponse.ServerResponsecreateByFail(ResponseCode.PASSWORD_INCORRECT,"旧密码输入错误");
    }


    /*
    * 检验账号是否存在
    * */
    @Override
    public ServerResponse check_valid(String str, String type) {
        if(str==null||str.equals("")){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.PARAM_NOT_NULL,"用户名或者邮箱不能为空");

        }
        if(type==null||type.equals("")){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.PARAM_NOT_NULL,"检验的类型参数不能为空");

        }
        if(type.equals("username")){
            int result=userMapper.testuser(str);
            if(result>0){
                return ServerResponse.ServerResponsecreateByFail(ResponseCode.USERNAME_EXISTS,"用户名已存在");
            }else{
                return ServerResponse.ServerResponsecreateBySucess("检验成功");
            }
        }
        if(type.equals("email")){
            int result=userMapper.testemail(str);
            if(result>0){
                return ServerResponse.ServerResponsecreateByFail(ResponseCode.EMAIL_EXISTS,"邮箱已存在");
            }else{
                return ServerResponse.ServerResponsecreateBySucess("检验成功");
            }
        }

        return null;
    }

    @Override
    public ServerResponse update_information(User user) {

        if(user==null){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.PARAM_NOT_NULL,"参数不能为空");
        }
        int result = userMapper.update_information(user);
        if(result!=0){
            return ServerResponse.ServerResponsecreateBySucess("修改成功");
        }
        return ServerResponse.ServerResponsecreateByFail("修改失败");
    }

    /*
    * 根据用户名查找问题
    * */
    @Override
    public ServerResponse findquestionByUsername(String username) {
        if(username==null||username.equals("")){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.PARAM_NOT_NULL,"参数不可为空");

        }
        String result=userMapper.findquestionByUsername(username);
        if(result==null){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.QUESTION_NOT_SEEK,"未查到");
        }

        return ServerResponse.ServerResponsecreateBySucess("密保问题",result);
    }
    /*
    * 验证问题答案
    * */
    @Override
    public ServerResponse check_question(String username, String question, String answer) {
        if(username==null||username.equals("")){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.PARAM_NOT_NULL,"用户名不可为空");

        }
        if(question==null||question.equals("")){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.PARAM_NOT_NULL,"问题不可为空");

        }
        if(answer==null||answer.equals("")){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.PARAM_NOT_NULL,"答案不可为空");

        }
        int result=userMapper.checkquestion(username,question,answer) ;
        if(result==0){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.ANSWER_ERROR,"答案错误");
        }
        String token= UUID.randomUUID().toString();
        TokenCache.set("username:"+username,token);
        return ServerResponse.ServerResponsecreateBySucess("获取token成功",token);
    }
    /*
     * 忘记密码修改密码
     * */
    @Override
    public ServerResponse change_password_forget(String username, String newpassword, String forgettoken) {
        if(username==null||username.equals("")){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.PARAM_NOT_NULL,"用户名不可为空");

        }
        if(newpassword==null||newpassword.equals("")){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.PARAM_NOT_NULL,"密码不可为空");

        }
        if(forgettoken==null||forgettoken.equals("")){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.PARAM_NOT_NULL,"token不可为空");

        }
        String token=TokenCache.get("username:"+username);
        if(token==null){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.TOKEN_OVERDUE,"token已过期");
        }
        if(!token.equals(forgettoken)){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.TOKEN_INVALID,"无效的token");
        }
        int result =userMapper.changePasswordFotget(username,MD5Utils.getMD5Code(newpassword));
        if(result==0){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.ERROR,"修改失败");
        }

        return ServerResponse.ServerResponsecreateBySucess();
    }

}
