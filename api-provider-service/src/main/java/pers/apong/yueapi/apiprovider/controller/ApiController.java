package pers.apong.yueapi.apiprovider.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pers.apong.yueapi.apiprovider.common.BaseResponse;
import pers.apong.yueapi.apiprovider.common.ErrorCode;
import pers.apong.yueapi.apiprovider.common.Result;
import pers.apong.yueapi.apiprovider.domain.vo.NeteaseHotComment;
import pers.apong.yueapi.apiprovider.exception.BusinessException;
import pers.apong.yueapi.apiprovider.utils.ThrowUtils;

import java.util.*;

/**
 * API 接口
 */
@RestController
public class ApiController {

    /**
     * 随机一句毒鸡汤
     * 来源：https://api.btstu.cn/doc/yan.php
     *
     * @return
     */
    @GetMapping("/weird_sayings")
    public BaseResponse<String> getWeirdSayings() {
        String data = HttpUtil.get("https://api.btstu.cn/yan/api.php?charset=utf-8");
        ThrowUtils.throwIf(StrUtil.isBlank(data), ErrorCode.SYSTEM_ERROR, "接口维护中，暂不可用");
        return Result.ok(data);
    }


    /**
     * 随机一句土味情话
     * 来源：https://api.uomg.com/doc-rand.qinghua.html
     *
     * @return
     */
    @GetMapping("/love_words")
    public BaseResponse<String> getLoveWords() {
        String data = HttpUtil.get("https://api.uomg.com/api/rand.qinghua?format=text");
        ThrowUtils.throwIf(StrUtil.isBlank(data), ErrorCode.SYSTEM_ERROR, "接口维护中，暂不可用");
        return Result.ok(data);
    }

    /**
     * 随机一句网易云热门评论
     * 来源：https://api.uomg.com/doc-comments.163.html
     *
     * @return
     */
    @GetMapping("/netease_hot_comments")
    public BaseResponse<NeteaseHotComment> getNeteaseHotComments() {
        String jsonData = HttpUtil.get("https://api.uomg.com/api/comments.163?format=json");
        ThrowUtils.throwIf(StrUtil.isBlank(jsonData), ErrorCode.SYSTEM_ERROR, "接口维护中，暂不可用");
        Map<String, Object> uomgResult = JSONUtil.toBean(jsonData, Map.class);
        // 取出结果
        NeteaseHotComment neteaseHotComment = NeteaseHotComment.of((Map<String, String>) uomgResult.get("data"));
        return Result.ok(neteaseHotComment);
    }

    /**
     * 查询城市天气
     * 来源：https://api.52vmy.cn/doc/query/tian/three.html
     *
     * @return
     */
    @GetMapping("/city_weather")
    public BaseResponse<Object> getCityWeather(@RequestParam(required = false) String cityName) {
        if (StrUtil.isBlank(cityName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请输入要查询的城市名称！");
        }
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("city", cityName);
        String jsonData = HttpUtil.get("https://api.52vmy.cn/api/query/tian/three", paramsMap);
        ThrowUtils.throwIf(StrUtil.isBlank(jsonData), ErrorCode.SYSTEM_ERROR, "接口维护中，暂不可用");
        // 解析响应结果
        try {
            Map vmyResult = JSONUtil.toBean(jsonData, Map.class);
            Object data = vmyResult.get("data");
            if (data == null) {
                return Result.error(ErrorCode.PARAMS_ERROR, (String) vmyResult.get("msg"));
            }
            Map innerData = JSONUtil.toBean((JSONObject) data, Map.class);
            JSONArray jsonWeathers = (JSONArray) innerData.get("data");
            List weathers = JSONUtil.toList(jsonWeathers, Object.class);
            return Result.ok(weathers.size() < 3 ? null : weathers.get(1));
        } catch (Exception e) {
            return Result.error(ErrorCode.SYSTEM_ERROR, "接口维护中，暂不可用");
        }
    }

    /**
     * 随机一句名人名言
     * 来源：https://api.52vmy.cn/doc/wl/yan/ming.html
     *
     * @return
     */
    @GetMapping("/famous_sayings")
    public BaseResponse<String> getFamousSayings() {
        String data = HttpUtil.get("https://api.52vmy.cn/api/wl/yan/ming?type=text");
        ThrowUtils.throwIf(StrUtil.isBlank(data), ErrorCode.SYSTEM_ERROR, "接口维护中，暂不可用");
        return Result.ok(data);
    }

    /**
     * 给你一个答案
     * 来源：https://api.52vmy.cn/doc/wl/yan/bay.html
     *
     * @return
     */
    @GetMapping("/an_answer")
    public BaseResponse<String> getAnAnswer() {
        String data = HttpUtil.get("https://api.52vmy.cn/api/wl/yan/bay?type=text");
        ThrowUtils.throwIf(StrUtil.isBlank(data), ErrorCode.SYSTEM_ERROR, "接口维护中，暂不可用");
        return Result.ok(data);
    }

}
