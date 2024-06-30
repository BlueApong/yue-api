package pers.apong.yueapi.platform.service;

import pers.apong.yueapi.platform.model.domain.UserApiInvoke;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author apong
*/
public interface UserApiInvokeService extends IService<UserApiInvoke> {
    /**
     * 校验创建、更新的参数
     *
     * @param userApiInvoke
     * @param isAdd
     */
    void validate(UserApiInvoke userApiInvoke, boolean isAdd);

    /**
     * 统计用户调用接口次数
     *
     * @param apiInfoId
     * @param userId
     * @return
     */
    boolean countInvocation(long apiInfoId, long userId);
}
