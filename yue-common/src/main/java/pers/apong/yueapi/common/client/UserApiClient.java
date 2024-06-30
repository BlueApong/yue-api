package pers.apong.yueapi.common.client;

import pers.apong.yueapi.common.dto.UserApiInvocationDto;

/**
 * 用户接口调用关系服务接口
 */
public interface UserApiClient {
    /**
     * 验证用户是否可以使用该接口
     *
     * @param url
     * @param method
     * @param accessKey
     * @return
     */
    UserApiInvocationDto validate(String url, String method, String accessKey);

    /**
     * 统计用户调用接口次数
     *
     * @param apiInfoId
     * @param userId
     */
    boolean countInvacation(long apiInfoId, long userId);
}
