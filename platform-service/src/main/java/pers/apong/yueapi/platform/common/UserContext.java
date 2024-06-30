package pers.apong.yueapi.platform.common;

import pers.apong.yueapi.platform.exception.BusinessException;
import pers.apong.yueapi.platform.model.domain.User;

public class UserContext {
    private static final ThreadLocal<User> tl = new ThreadLocal<>();
    public static void setUser(User user) {
        tl.set(user);
    }

    public static User getLoginUser() {
        User user = tl.get();
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return user;
    }

    public static User getUserPermitNull() {
        return tl.get();
    }

    public static void removeUser() {
        tl.remove();
    }
}
