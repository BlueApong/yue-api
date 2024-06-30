package pers.apong.yueapi.platform.mapper;

import pers.apong.yueapi.platform.model.domain.UserApiInvoke;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import pers.apong.yueapi.platform.model.vo.ApiInvokeAnalysisVO;

import java.util.List;

/**
* @author apong
*/
public interface UserApiInvokeMapper extends BaseMapper<UserApiInvoke> {
    /**
     * 查询 topN 调用次数最多的接口信息
     *
     * @return limitNum
     */
    List<ApiInvokeAnalysisVO> listTopInvokedApiInfo(int limitNum);
}




