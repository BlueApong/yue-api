package pers.apong.yueapi.platform.service.impl;

import pers.apong.yueapi.platform.common.ErrorCode;
import pers.apong.yueapi.platform.exception.BusinessException;
import pers.apong.yueapi.platform.mapper.UserApiInvokeMapper;
import pers.apong.yueapi.platform.model.domain.UserApiInvoke;
import pers.apong.yueapi.platform.service.UserApiInvokeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
* @author apong
* @description 针对表【user_api_invoke(用户调用接口关系)】的数据库操作Service实现
* @createDate 2024-06-02 20:43:01
*/
@Service
public class UserApiInvokeServiceImpl extends ServiceImpl<UserApiInvokeMapper, UserApiInvoke>
    implements UserApiInvokeService{

    @Override
    public void validate(UserApiInvoke userApiInvoke, boolean isAdd) {
        if (userApiInvoke == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 创建时，所有参数必须非空
        if (isAdd) {
            if (userApiInvoke.getUserId() == null || userApiInvoke.getUserId() < 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        // 更新校验
        if (userApiInvoke.getLeftNum() < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "分配次数不能小于0");
        }
    }

    @Override
    public boolean countInvocation(long apiInfoId, long userId) {
        // 乐观锁，防止leftNum减少为负数
        return update().setSql("leftNum = leftNum - 1, totalNum = totalNum + 1")
                .eq("apiInfoId", apiInfoId)
                .eq("userId", userId)
                .gt("leftNum", 0)
                .update();
    }
}




