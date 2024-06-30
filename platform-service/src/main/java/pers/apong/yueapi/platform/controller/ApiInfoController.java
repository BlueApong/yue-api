package pers.apong.yueapi.platform.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDto;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import pers.apong.yueapi.platform.annotation.AuthCheck;
import pers.apong.yueapi.platform.common.*;
import pers.apong.yueapi.platform.exception.BusinessException;
import pers.apong.yueapi.platform.mapper.ApiInfoMapper;
import pers.apong.yueapi.platform.model.domain.ApiInfo;
import pers.apong.yueapi.platform.model.domain.User;
import pers.apong.yueapi.platform.model.dto.api_info.ApiInfoAddRequest;
import pers.apong.yueapi.platform.model.dto.api_info.ApiInfoInvokeRequest;
import pers.apong.yueapi.platform.model.dto.api_info.ApiInfoQueryRequest;
import pers.apong.yueapi.platform.model.dto.api_info.ApiInfoUpdateRequest;
import pers.apong.yueapi.platform.model.enums.ApiInfoStatusEnum;
import pers.apong.yueapi.platform.model.vo.ApiInfoVO;
import pers.apong.yueapi.platform.model.vo.UserAppliedApiVO;
import pers.apong.yueapi.platform.service.ApiInfoService;
import pers.apong.yueapi.platform.service.UserApiInvokeService;
import pers.apong.yueapi.platform.service.UserService;
import pers.apong.yueapi.platform.utils.JsonParamsBuilder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 接口信息接口
 *
 * @author apong
 */
@RestController
@RequestMapping("/apiInfo")
public class ApiInfoController {

    @Resource
    private ApiInfoService apiInfoService;

    @Resource
    private UserService userService;

    @Resource
    private UserApiInvokeService userApiInvokeService;

    @Resource
    private ApiInfoMapper apiInfoMapper;

    // region 增删改查

    /**
     * 创建接口信息
     *
     * @param apiInfoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addApiInfo(@RequestBody ApiInfoAddRequest apiInfoAddRequest, HttpServletRequest request) {
        if (apiInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ApiInfo apiInfo = new ApiInfo();
        BeanUtils.copyProperties(apiInfoAddRequest, apiInfo);
        User loginUser = userService.getLoginUser(request);
        apiInfo.setUserId(loginUser.getId());
        // 校验参数
        boolean isAdd = true;
        apiInfoService.validate(apiInfo, isAdd);
        boolean result = apiInfoService.save(apiInfo);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return ResultUtils.success(apiInfo.getId());
    }

    /**
     * 删除接口信息
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteApiInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = apiInfoService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 批量删除接口信息
     *
     * @param deleteRequest
     * @return
     */
    @PostMapping("/delete/list")
    public BaseResponse<Boolean> deleteApiInfo(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || CollUtil.isEmpty(deleteRequest.getIds())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<Long> ids = deleteRequest.getIds();
        boolean b = apiInfoService.removeByIds(ids);
        return ResultUtils.success(b);
    }

    /**
     * 更新接口信息
     *
     * @param apiInfoUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateApiInfo(@RequestBody ApiInfoUpdateRequest apiInfoUpdateRequest, HttpServletRequest request) {
        if (apiInfoUpdateRequest == null || apiInfoUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ApiInfo apiInfo = new ApiInfo();
        BeanUtils.copyProperties(apiInfoUpdateRequest, apiInfo);
        // 校验参数
        apiInfoService.validate(apiInfo, false);
        boolean result = apiInfoService.updateById(apiInfo);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取接口信息
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<ApiInfoVO> getApiInfoById(int id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ApiInfo apiInfo = apiInfoService.getById(id);
        ApiInfoVO apiInfoVO = new ApiInfoVO();
        BeanUtils.copyProperties(apiInfo, apiInfoVO);
        JsonParamsBuilder jsonParamsBuilder = new JsonParamsBuilder();
        Map<String, Object> defaultMap = jsonParamsBuilder.getDefaultMap(apiInfoVO.getRequestParams());
        apiInfoVO.setRequestExample(JSONUtil.toJsonStr(defaultMap));
        return ResultUtils.success(apiInfoVO);
    }

    /**
     * 获取接口信息列表
     *
     * @param apiInfoQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list")
    public BaseResponse<List<ApiInfoVO>> listApiInfo(ApiInfoQueryRequest apiInfoQueryRequest, HttpServletRequest request) {
        ApiInfo apiInfoQuery = new ApiInfo();
        if (apiInfoQueryRequest != null) {
            BeanUtils.copyProperties(apiInfoQueryRequest, apiInfoQuery);
        }
        QueryWrapper<ApiInfo> queryWrapper = new QueryWrapper<>(apiInfoQuery);
        List<ApiInfo> apiInfoList = apiInfoService.list(queryWrapper);
        List<ApiInfoVO> apiInfoVOList = apiInfoList.stream().map(apiInfo -> {
            ApiInfoVO apiInfoVO = new ApiInfoVO();
            BeanUtils.copyProperties(apiInfo, apiInfoVO);
            JsonParamsBuilder jsonParamsBuilder = new JsonParamsBuilder();
            Map<String, Object> defaultMap = jsonParamsBuilder.getDefaultMap(apiInfoVO.getRequestParams());
            apiInfoVO.setRequestExample(JSONUtil.toJsonStr(defaultMap));
            return apiInfoVO;
        }).collect(Collectors.toList());
        return ResultUtils.success(apiInfoVOList);
    }

    /**
     * 分页获取接口信息列表
     *
     * @param apiInfoQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<ApiInfoVO>> listApiInfoByPage(ApiInfoQueryRequest apiInfoQueryRequest, HttpServletRequest request) {
        long current = 1;
        long size = 10;
        ApiInfo apiInfoQuery = new ApiInfo();
        if (apiInfoQueryRequest != null) {
            BeanUtils.copyProperties(apiInfoQueryRequest, apiInfoQuery);
            current = apiInfoQueryRequest.getCurrent();
            size = apiInfoQueryRequest.getPageSize();
        }
        QueryWrapper<ApiInfo> queryWrapper = new QueryWrapper<>(apiInfoQuery);
        Page<ApiInfo> apiInfoPage = apiInfoService.page(new Page<>(current, size), queryWrapper);
        Page<ApiInfoVO> apiInfoVOPage = new PageDto<>(apiInfoPage.getCurrent(), apiInfoPage.getSize(), apiInfoPage.getTotal());
        List<ApiInfoVO> apiInfoVOList = apiInfoPage.getRecords().stream().map(apiInfo -> {
            ApiInfoVO apiInfoVO = new ApiInfoVO();
            BeanUtils.copyProperties(apiInfo, apiInfoVO);
            JsonParamsBuilder jsonParamsBuilder = new JsonParamsBuilder();
            Map<String, Object> defaultMap = jsonParamsBuilder.getDefaultMap(apiInfoVO.getRequestParams());
            apiInfoVO.setRequestExample(JSONUtil.toJsonStr(defaultMap));
            return apiInfoVO;
        }).collect(Collectors.toList());
        apiInfoVOPage.setRecords(apiInfoVOList);
        return ResultUtils.success(apiInfoVOPage);
    }

    // endregion

    /**
     * 发布上线接口
     *
     * @param idRequest
     * @param request
     * @return
     */
    @AuthCheck(mustRole = "admin")
    @PostMapping("/online")
    public BaseResponse<Boolean> onlineApiInfo(@RequestBody IdRequest idRequest, HttpServletRequest request) {
        if (idRequest == null || idRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 接口是否存在
        Long id = idRequest.getId();
        ApiInfo apiInfo = apiInfoService.getById(id);
        if (apiInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "接口不存在");
        }
        // 验证接口能否使用
        boolean working = apiInfoService.validateApiWorking(apiInfo.getUrl(), apiInfo.getMethod(), apiInfo.getRequestParams());
        if(!working) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口不可用");
        }
        // 发布接口
        boolean update = apiInfoService.update()
                .set("status", ApiInfoStatusEnum.ONLINE.getValue())
                .eq("id", id).update();
        return ResultUtils.success(update);
    }

    /**
     * 批量发布上线接口
     *
     * @param idRequest
     * @param request
     * @return
     */
    @AuthCheck(mustRole = "admin")
    @PostMapping("/online/list")
    public BaseResponse<Boolean> listOnlineApiInfo(@RequestBody IdRequest idRequest, HttpServletRequest request) {
        if (idRequest == null || CollUtil.isEmpty(idRequest.getIds())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<Long> ids = idRequest.getIds();
        List<ApiInfo> apiInfoList = apiInfoService.listByIds(ids);
        List<Long> workingIds = apiInfoList.stream()
                .filter(apiInfo ->
                        apiInfoService.validateApiWorking(apiInfo.getUrl(), apiInfo.getMethod(), apiInfo.getRequestParams()))
                .map(ApiInfo::getId)
                .collect(Collectors.toList());
        // 发布接口
        boolean update = apiInfoService.update()
                .set("status", ApiInfoStatusEnum.ONLINE.getValue())
                .in("id", workingIds)
                .update();
        if (workingIds.size() == ids.size()) {
            return ResultUtils.success(update);
        } else {
            return ResultUtils.nearlySuccess(update, "成功开启 " + workingIds.size() + " 个接口");
        }
    }

    /**
     * 关闭下线接口
     *
     * @param idRequest
     * @param request
     * @return
     */
    @AuthCheck(mustRole = "admin")
    @PostMapping("/offline")
    public BaseResponse<Boolean> offlineApiInfo(@RequestBody IdRequest idRequest, HttpServletRequest request) {
        if (idRequest == null || idRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 接口是否存在
        Long id = idRequest.getId();
        ApiInfo apiInfo = apiInfoService.getById(id);
        if (apiInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "接口不存在");
        }
        // 下线接口
        boolean update = apiInfoService.update()
                .set("status", ApiInfoStatusEnum.OFFLINE.getValue())
                .eq("id", id).update();
        return ResultUtils.success(update);
    }

    /**
     * 关闭下线接口
     *
     * @param idRequest
     * @param request
     * @return
     */
    @AuthCheck(mustRole = "admin")
    @PostMapping("/offline/list")
    public BaseResponse<Boolean> listOfflineApiInfo(@RequestBody IdRequest idRequest, HttpServletRequest request) {
        if (idRequest == null || CollUtil.isEmpty(idRequest.getIds())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 下线接口
        List<Long> ids = idRequest.getIds();
        boolean update = apiInfoService.update()
                .set("status", ApiInfoStatusEnum.OFFLINE.getValue())
                .in("id", ids).update();
        return ResultUtils.success(update);
    }

    /**
     * 申请接口使用次数
     *
     * @param idRequest
     * @return
     */
    @PostMapping("/apply")
    public BaseResponse<Boolean> applyApiInfo(@RequestBody IdRequest idRequest) {
        if (idRequest == null || idRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 接口是否存在
        Long apiId = idRequest.getId();
        boolean b = apiInfoService.applyApiInfo(apiId);
        if (!b) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口申请失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 在线测试接口
     *
     * @param apiInfoInvokeRequest
     * @param request
     * @return
     */
    @PostMapping("/invoke")
    public BaseResponse<Object> invokeApiInfo(@RequestBody ApiInfoInvokeRequest apiInfoInvokeRequest, HttpServletRequest request) {
        if (apiInfoInvokeRequest == null || apiInfoInvokeRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userRequestParams = apiInfoInvokeRequest.getUserRequestParams();
        Long id = apiInfoInvokeRequest.getId();
        String data = apiInfoService.invokeOnline(id, userRequestParams);
        return ResultUtils.success(data);
    }

    /**
     * 获取当前用户申请的接口信息
     *
     * @param apiInfoQueryRequest
     * @return
     */
    @GetMapping("/list/my/applied")
    public BaseResponse<List<UserAppliedApiVO>> listMyAppliedApiInfo(ApiInfoQueryRequest apiInfoQueryRequest) {
        if (apiInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = UserContext.getLoginUser();
        long current = apiInfoQueryRequest.getCurrent();
        long pageSize = apiInfoQueryRequest.getPageSize();
        current = (current - 1) * pageSize;
        ApiInfo apiInfo = BeanUtil.copyProperties(apiInfoQueryRequest, ApiInfo.class);
        // todo 封装条件，直接使用entity无效
        QueryWrapper<ApiInfo> queryWrapper = new QueryWrapper<>(apiInfo);
        //queryWrapper.like("description", apiInfo.getDescription());
        List<UserAppliedApiVO> userAppliedApiVOList = apiInfoMapper.listUserAppliedApi(loginUser.getId(), current, pageSize, queryWrapper);
        return ResultUtils.success(userAppliedApiVOList);
    }
}
