package pers.apong.yueapi.platform.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.ibatis.annotations.Param;
import pers.apong.yueapi.platform.model.domain.ApiInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import pers.apong.yueapi.platform.model.vo.UserAppliedApiVO;

import java.util.List;

/**
* @author apong
*/
public interface ApiInfoMapper extends BaseMapper<ApiInfo> {
    /**
     * 获取用户已开通的接口信息
     *
     * @param userId
     * @param current
     * @param pageSize
     * @param ew
     * @return
     */
    List<UserAppliedApiVO> listUserAppliedApi(@Param("userId") long userId, @Param("current") long current,
                                              @Param("pageSize") long pageSize, @Param("ew") QueryWrapper<ApiInfo> ew);
}




