package pers.apong.yueapi.platform;

import cn.hutool.http.Method;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
public class PlatformApplicationTest {

    @Test
    public void test() {
        String regex = "https?://([^/]+)/(.*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher("http://localhost:8123/api/name/user");
        if (matcher.find()) {
            String host = matcher.group(1);
            String path = matcher.group(2);
            System.out.println(String.format("host: %s, path: %s", host, path));
        } else {
            System.out.println("匹配失败！");
        }
    }

    @Test
    public void testMethodValueOf() {
        Method aaa = Method.valueOf("aaa");
        System.out.println(aaa);
    }

}
