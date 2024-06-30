package pers.apong.yueapi.platform.remote;

import org.apache.dubbo.config.annotation.DubboService;
import pers.apong.yueapi.common.client.UserApiClient;
import pers.apong.yueapi.common.dto.UserApiInvocationDto;
import pers.apong.yueapi.platform.model.domain.ApiInfo;
import pers.apong.yueapi.platform.model.domain.User;
import pers.apong.yueapi.platform.model.domain.UserApiInvoke;
import pers.apong.yueapi.platform.model.enums.ApiInfoStatusEnum;
import pers.apong.yueapi.platform.service.ApiInfoService;
import pers.apong.yueapi.platform.service.UserApiInvokeService;
import pers.apong.yueapi.platform.service.UserService;

import javax.annotation.Resource;
import java.util.Optional;

@DubboService
public class UserApiInfoClientImpl implements UserApiClient {
    @Resource
    private ApiInfoService apiInfoService;

    @Resource
    private UserApiInvokeService userApiInvokeService;

    @Resource
    private UserService userService;

    @Override
    public UserApiInvocationDto validate(String url, String method, String accessKey) {
        // 1. 接口是否存在
        ApiInfo apiInfo = apiInfoService.query().eq("gatewayUrl", url).eq("method", method).one();
        if (apiInfo == null) {
            return UserApiInvocationDto.ofError("接口不存在");
        }
        // 2. 接口是否上线
        final int offlineValue = ApiInfoStatusEnum.OFFLINE.getValue();
        Integer status = Optional.ofNullable(apiInfo.getStatus()).orElse(offlineValue);
        if (!ApiInfoStatusEnum.ONLINE.equalsValue(status)) {
            return UserApiInvocationDto.ofError("接口不可用");
        }
        // 3. 用户是否存在
        User user = userService.query().eq("accessKey", accessKey).one();
        if (user == null) {
            return UserApiInvocationDto.ofError("身份验证失败");
        }
        // 4. 用户是否还有调用次数
        UserApiInvoke userApiInvoke = userApiInvokeService.query()
                .eq("apiInfoId", apiInfo.getId())
                .eq("userId", user.getId())
                .one();
        if (userApiInvoke == null || userApiInvoke.getLeftNum() <= 0) {
            return UserApiInvocationDto.ofError("无权限调用");
        }
        UserApiInvocationDto userApiInvocationDto = new UserApiInvocationDto();
        userApiInvocationDto.setApiInfoId(apiInfo.getId());
        userApiInvocationDto.setUserId(user.getId());
        userApiInvocationDto.setSecretKey(user.getSecretKey());
        return userApiInvocationDto;
    }

    @Override
    public boolean countInvacation(long apiInfoId, long userId) {
        return userApiInvokeService.countInvocation(apiInfoId, userId);
    }
}
