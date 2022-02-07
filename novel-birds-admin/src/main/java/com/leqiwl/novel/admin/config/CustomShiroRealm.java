package com.leqiwl.novel.admin.config;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;


public class CustomShiroRealm extends AuthorizingRealm {

    @Autowired
    private CustomLoginConfig customLoginConfig;

    /**
     * 授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        return null;
    }

    /**
     * 登录认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
        //获取用户账号
        String username = token.getUsername();

        String password = String.valueOf(token.getPassword());
        //账号不存在、密码错误
        if (!customLoginConfig.getUserName().equals(username) || !customLoginConfig.getPassword().equals(password)) {
            throw new IncorrectCredentialsException("用户名或密码不正确");
        }

        //清除该用户以前登录时保存的session，强制退出
        removeOldSession(username);
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(username, password, getName());
        return info;
    }

    private void removeOldSession(String username) {
        DefaultWebSecurityManager securityManager = (DefaultWebSecurityManager) SecurityUtils.getSecurityManager();
        DefaultWebSessionManager sessionManager = (DefaultWebSessionManager) securityManager.getSessionManager();
//        获取当前已登录的用户session列表
        Collection<Session> sessions = sessionManager.getSessionDAO().getActiveSessions();
        for (Session session : sessions) {

            Object attribute = session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
            if (attribute == null) {
                continue;
            }

            String userName = ((SimplePrincipalCollection) attribute).getPrimaryPrincipal().toString();
            if (username.equals(userName)) {
                sessionManager.getSessionDAO().delete(session);
            }
        }
    }
}
