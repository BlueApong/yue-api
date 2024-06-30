package pers.apong.yueapi.platform.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.apong.yueapi.platform.annotation.AuthCheck;
import pers.apong.yueapi.platform.common.BaseResponse;
import pers.apong.yueapi.platform.common.ResultUtils;
import pers.apong.yueapi.platform.mapper.ApiInfoMapper;
import pers.apong.yueapi.platform.mapper.UserApiInvokeMapper;
import pers.apong.yueapi.platform.model.vo.ApiInvokeAnalysisVO;
import pers.apong.yueapi.platform.service.ApiInfoService;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/analysis")
public class AnalysisController {

    @Resource
    private UserApiInvokeMapper userApiInvokeMapper;

    /**
     * 查询 topN 调用次数最多的接口信息
     *
     * @return
     */
    @GetMapping("/api/invoke/top")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<List<ApiInvokeAnalysisVO>> listTopInvokedApiInfo() {
        List<ApiInvokeAnalysisVO> list = userApiInvokeMapper.listTopInvokedApiInfo(3);
        return ResultUtils.success(list);
    }
}
