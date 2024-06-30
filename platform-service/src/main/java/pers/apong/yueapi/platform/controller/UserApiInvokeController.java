package pers.apong.yueapi.platform.controller;

import pers.apong.yueapi.platform.common.BaseResponse;
import pers.apong.yueapi.platform.common.DeleteRequest;
import pers.apong.yueapi.platform.common.ErrorCode;
import pers.apong.yueapi.platform.common.ResultUtils;
import pers.apong.yueapi.platform.exception.BusinessException;
import pers.apong.yueapi.platform.model.domain.User;
import pers.apong.yueapi.platform.model.domain.UserApiInvoke;
import pers.apong.yueapi.platform.model.dto.user_api_invoke.UserApiInvokeAddRequest;
import pers.apong.yueapi.platform.model.dto.user_api_invoke.UserApiInvokeQueryRequest;
import pers.apong.yueapi.platform.model.dto.user_api_invoke.UserApiInvokeUpdateRequest;
import pers.apong.yueapi.platform.service.UserApiInvokeService;
import pers.apong.yueapi.platform.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户接口调用关系接口
 *
 * @author apong
 */
@RestController
@RequestMapping("/user_api_invoke")
public class UserApiInvokeController {

    @Resource
    private UserApiInvokeService userApiInvokeService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建接口信息
     *
     * @param userApiInvokeAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addUserApiInvoke(@RequestBody UserApiInvokeAddRequest userApiInvokeAddRequest, HttpServletRequest request) {
        if (userApiInvokeAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserApiInvoke userApiInvoke = new UserApiInvoke();
        BeanUtils.copyProperties(userApiInvokeAddRequest, userApiInvoke);
        User loginUser = userService.getLoginUser(request);
        userApiInvoke.setUserId(loginUser.getId());
        // 校验参数
        boolean isAdd = true;
        userApiInvokeService.validate(userApiInvoke, isAdd);
        boolean result = userApiInvokeService.save(userApiInvoke);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return ResultUtils.success(userApiInvoke.getId());
    }

    /**
     * 删除接口信息
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUserApiInvoke(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userApiInvokeService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 更新接口信息
     *
     * @param userApiInvokeUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateUserApiInvoke(@RequestBody UserApiInvokeUpdateRequest userApiInvokeUpdateRequest, HttpServletRequest request) {
        if (userApiInvokeUpdateRequest == null || userApiInvokeUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserApiInvoke userApiInvoke = new UserApiInvoke();
        BeanUtils.copyProperties(userApiInvokeUpdateRequest, userApiInvoke);
        // 校验参数
        userApiInvokeService.validate(userApiInvoke, false);
        boolean result = userApiInvokeService.updateById(userApiInvoke);
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
    public BaseResponse<UserApiInvoke> getUserApiInvokeById(int id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserApiInvoke userApiInvoke = userApiInvokeService.getById(id);
        return ResultUtils.success(userApiInvoke);
    }

    /**
     * 获取接口信息列表
     *
     * @param userApiInvokeQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list")
    public BaseResponse<List<UserApiInvoke>> listUserApiInvoke(UserApiInvokeQueryRequest userApiInvokeQueryRequest, HttpServletRequest request) {
        UserApiInvoke userApiInvokeQuery = new UserApiInvoke();
        if (userApiInvokeQueryRequest != null) {
            BeanUtils.copyProperties(userApiInvokeQueryRequest, userApiInvokeQuery);
        }
        QueryWrapper<UserApiInvoke> queryWrapper = new QueryWrapper<>(userApiInvokeQuery);
        List<UserApiInvoke> userApiInvokeList = userApiInvokeService.list(queryWrapper);
        return ResultUtils.success(userApiInvokeList);
    }

    /**
     * 分页获取接口信息列表
     *
     * @param userApiInvokeQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<UserApiInvoke>> listUserApiInvokeByPage(UserApiInvokeQueryRequest userApiInvokeQueryRequest, HttpServletRequest request) {
        long current = 1;
        long size = 10;
        UserApiInvoke userApiInvokeQuery = new UserApiInvoke();
        if (userApiInvokeQueryRequest != null) {
            BeanUtils.copyProperties(userApiInvokeQueryRequest, userApiInvokeQuery);
            current = userApiInvokeQueryRequest.getCurrent();
            size = userApiInvokeQueryRequest.getPageSize();
        }
        QueryWrapper<UserApiInvoke> queryWrapper = new QueryWrapper<>(userApiInvokeQuery);
        Page<UserApiInvoke> userApiInvokePage = userApiInvokeService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(userApiInvokePage);
    }
    // endregion

}
