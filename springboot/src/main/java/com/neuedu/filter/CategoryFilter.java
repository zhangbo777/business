package com.neuedu.filter;

import com.neuedu.common.Const;
import com.neuedu.common.ResponseCode;
import com.neuedu.common.Role;
import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.User;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


@WebFilter(urlPatterns = {"/manage/*"})
public class CategoryFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("11111111111111");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession();
        User user = (User)session.getAttribute(Const.CURRENTUSER);
        String uri = request.getRequestURI();

        System.out.println("filter url:"+uri);
        if(user==null){
            String result=ServerResponse.ServerResponsecreateByFail(ResponseCode.USER_NOT_LOGIN,"请登录").objtostr();
            response.getOutputStream().write(result.getBytes());
            return;
        }
        if(user.getRole()== Role.ROLE_USER.getRole()){
            String result=ServerResponse.ServerResponsecreateByFail(ResponseCode.USER_NOT_VIERS,"权限不足").objtostr();
            response.getOutputStream().write(result.getBytes());
            return;

        }
        filterChain.doFilter(request,response);


        System.out.println("我已经执行了。");


    }

    @Override
    public void destroy() {

    }
}
