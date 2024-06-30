package pers.apong.yueapi.platform.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import pers.apong.yueapi.platform.common.UserContext;
import pers.apong.yueapi.platform.constant.UserConstant;
import pers.apong.yueapi.platform.model.domain.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author apong
 */
public class ProcessSessionInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        if (session == null) {
            return true;
        }
        // 提取用户信息
        User loginUser = (User) session.getAttribute(UserConstant.USER_LOGIN_STATE);
        if (loginUser == null) {
            return true;
        }
        UserContext.setUser(loginUser);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.removeUser();
    }
}

