package pers.apong.yueapi.platform.service;

import cn.hutool.http.HttpResponse;
import pers.apong.yueapi.platform.model.domain.ApiInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import pers.apong.yueapi.platform.model.domain.User;

/**
* @author apong
*/
public interface ApiInfoService extends IService<ApiInfo> {

    /**
     * 校验创建、更新的参数
     *
     * @param apiInfo
     * @param isAdd
     * @return
     */
    ApiInfo validate(ApiInfo apiInfo, boolean isAdd);

    /**
     * 验证接口是否能够正常响应（外部）
     *
     * @param url
     * @param method
     * @param requestParams
     * @return
     */
    boolean validateApiWorking(String url, String method, String requestParams);

    /**
     * 调用（测试）接口，返回json结果
     *
     * @param url
     * @param method
     * @param requestParams
     * @return
     */
    HttpResponse invokeApi(String url, String method, String requestParams, boolean isTest);

    /**
     * 确认接口可以使用（内部）
     *
     * @param url
     * @param method
     * @return
     */
    ApiInfo verifyApiAvailable(String url, String method);

    /**
     * 申请接口调用
     *
     * @param apiId
     * @return
     */
    boolean applyApiInfo(Long apiId);

    /**
     * 为用户分配密钥
     *
     * @param user
     * @return
     */
    User assignAkSk(User user);

    /**
     * 网站在线调用接口
     *
     * @param id
     * @param userRequestParams
     * @return
     */
    String invokeOnline(Long id, String userRequestParams);
}
