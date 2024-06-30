package pers.apong.yueapi.common.utils;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;

/**
 * 签名加密工具类
 * @author apong
 */
public class SignUtils {
    public static String genSign(String body, String secretKey) {
        Digester digester = new Digester(DigestAlgorithm.SHA256);
        String data = body + "." + secretKey;
        return digester.digestHex(data);
    }
}
