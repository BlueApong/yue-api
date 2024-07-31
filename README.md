## 简介

欢迎使用 Yue API 接口开放平台 🎉

> Yue API 接口开放平台是一个为用户和开发者提供丰富 API 接口调用服务的平台 🛠

作为『开发者』，可以在线选择所需接口并通过导入 api-client-sdk 快速在项目中集成调用接口的客户端，通过配置密钥可以快速调用接口，降低开发成本。

作为『用户』，可以查看接口列表找到感兴趣的接口，在线调用接口，查看结果。

作为『管理员』，可以对已接入的接口进行发布、下线等功能。

:one: 多样化的接口选择

平台提供了丰富多样的接口用您选择，满足您的不同需求。

:two: 在线调用功能

您可以在平台上进行接口在线调试，快速验证接口的功能和效果，节省了开发调试的时间和工作量。

:three: 开发者 SDK 支持

为了方便开发者集成接口到自己的代码中，平台提供了客户端SDK，使调用接口变得更加简单和便捷。

## 项目技术栈

- Spring Boot 2.7 方便整合其他依赖
- Mybatis Plus 快速编写条件 sql
- Redis 缓存
- Spring Cloud Gateway 鉴权和业务处理
- Dubbo 服务通信
- Nacos 注册中心

## 快速开始

### 导入

``` xml
<dependency>
    <groupId>pers.apong</groupId>
    <artifactId>api-client-sdk</artifactId>
    <version>0.0.1</version>
</dependency>
```

### 配置密钥

``` yaml
api:
  client:
    access-key: 你的accessKey
    secret-key: 你的secretKey
```

### 调用

``` java
public class Demo {
    @Resource
    private ApiClient apiClient;

    @Test
    public static void main(String[] args) {
        // 发出请求
        String loveWords = apiClient.getLoveWords();
        // 打印响应结果  
        System.out.println(loveWords);
    }
}
```

## 接口-方法对照表

| 接口名称           | 调用方法名            | 说明 |
| ------------------ | --------------------- | ---- |
| 一个答案           | getAnAnswer           |      |
| 查询城市天气       | getCityWeather        |      |
| 名人名言           | getFamousSayings      |      |
| 土味情话           | getLoveWords          |      |
| 网易云音乐热门评论 | getNeteaseHotComments |      |
| 毒鸡汤             | getWeirdSayings       |      |

## 响应状态码

| 响应码 |    参数名称     |    参数描述    |
| :----: | :-------------: | :------------: |
|   0    |     SUCCESS     |       ok       |
| 40000  |  PARAMS_ERROR   |  请求参数错误  |
| 40101  |  NO_AUTH_ERROR  |     无权限     |
| 40300  | FORBIDDEN_ERROR |    禁止访问    |
| 40400  | NOT_FOUND_ERROR | 请求数据不存在 |
| 50000  |  SYSTEM_ERROR   |  系统内部异常  |
| 50001  | OPERATION_ERROR |    操作失败    |

## 页面截图

**登录页面**

![image-20240731123208247](https://gcore.jsdelivr.net/gh/BlueApong/images@main/img/202407311232524.png)

**首页**

![image-20240731123310017](https://gcore.jsdelivr.net/gh/BlueApong/images@main/img/202407311233129.png)

**接口广场**

可以浏览各种接口，在线申请调用。

![image-20240731123322643](https://gcore.jsdelivr.net/gh/BlueApong/images@main/img/202407311233753.png)

**调用页面**

![image-20240731123410691](https://gcore.jsdelivr.net/gh/BlueApong/images@main/img/202407311234757.png)

申请后点击调用返回结果

![image-20240731123422132](https://gcore.jsdelivr.net/gh/BlueApong/images@main/img/202407311234199.png)

**个人中心**

支持查看已开通接口的调用情况。

![image-20240731123434338](https://gcore.jsdelivr.net/gh/BlueApong/images@main/img/202407311234430.png)

也可以获取开发者密钥，然后通过 sdk 调用接口。

![image-20240731123450119](https://gcore.jsdelivr.net/gh/BlueApong/images@main/img/202407311234209.png)

**后台管理页**

可以对接口进行管理，添加、发布、删除等功能。

![image-20240731123507829](https://gcore.jsdelivr.net/gh/BlueApong/images@main/img/202407311235926.png)

![image-20240731123601152](https://gcore.jsdelivr.net/gh/BlueApong/images@main/img/202407311236258.png)

还可以统计所有接口的调用情况，对网站数据有一个大概的把控。

![image-20240731123618080](https://gcore.jsdelivr.net/gh/BlueApong/images@main/img/202407311236166.png)

