package pers.apong.yueapi.apiprovider.domain.vo;

import cn.hutool.core.collection.CollUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NeteaseHotComment {
    /**
     * 歌曲名
     */
    private String songName;
    /**
     * 歌手名
     */
    private String artistsName;
    /**
     * 评论用户昵称
     */
    private String nickname;
    /**
     * 评论用户头像
     */
    private String avatarUrl;
    /**
     * 评论内容
     */
    private String content;

    public static NeteaseHotComment of(Map<String, String> uomgData) {
        if (CollUtil.isEmpty(uomgData)) {
            return new NeteaseHotComment();
        }
        String songName = uomgData.getOrDefault("name", "未知");
        String artistsName = uomgData.getOrDefault("artistsname", "未知");
        String nickname = uomgData.getOrDefault("nickname", "未知");
        // todo 空头像地址
        String avatarUrl = uomgData.getOrDefault("avatarurl", "");
        String content = uomgData.getOrDefault("content", "空");
        return new NeteaseHotComment(songName, artistsName, nickname, avatarUrl, content);
    }
}
