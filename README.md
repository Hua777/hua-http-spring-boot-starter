# SpringBoot 接口式註解，快速開發 HTTP 請求函數

GitHub: [hua-http-spring-boot-starter](https://github.com/Hua777/hua-http-spring-boot-starter)

在 SpringBoot 中用註解的方式請求 Http

## :pencil2: 嗨

SpringBoot 小白的我，歡迎大家 Issues、Fork、Pull Requests :smile:。

## POM 引入

```xml
<dependency>
    <groupId>com.github.hua777</groupId>
    <artifactId>hua-http-spring-boot-starter</artifactId>
    <version>2.0.0</version>
</dependency>
```

## 配置文件設置掃描路徑（可選，默認掃描啟動類下）

```yaml
com:
  github:
    hua777:
      hua-http-spring-boot-starter:
        scan-packages: xxx.xxx.xxx1,xxx.xxx.xxx2
        http-timeout-seconds: 60 # 默認 60
        http-redirects: true # 默認 true
```

## 教學

### 使用下面教程定義好的接口

```java
@Service
public class MyService {
    
    @Autowired
    TestHttp testhttp;

    public void run() {
        testhttp.get();
    }
}
```

### 發送 Get、Delete 請求

```java
@HuaHttp("http://hello-world.com")
public interface TestHttp {

    /*
     * http get http://hello-world.com/get/hello/world?hello=xxx
     */
    @HuaMethod(method = Method.GET, url = "/get/hello/world")
    String getHelloWorld(String hello);

    /*
     * http delete http://hello-world.com/delete/hello/world?hello=xxx
     */
    @HuaMethod(method = Method.DELETE, url = "/delete/hello/world")
    String deleteHelloWorld(String hello);
}
```

### 發送 Post、Put 請求

```java
@HuaHttp("http://hello-world.com")
public interface TestHttp {
    
    /*
     * http post http://hello-world.com/post/hello/world
     * body = {
     *     "hello": "xxx"
     * }
     */
    @HuaMethod(method = Method.POST, url = "/post/hello/world")
    String postHelloWorld(String hello); // default is body

    /*
     * http put http://hello-world.com/put/hello/world
     * body = {
     *     "hello": "xxx"
     * }
     */
    @HuaMethod(method = Method.PUT, url = "/put/hello/world")
    String putHelloWorld(String hello);
}
```

### 使用配置地址

```java
@HuaHttp("${hello.world.url}")
public interface TestHttp {

    /*
     * http get http://hello-world.com/get/hello/world?hello=xxx
     */
    @HuaMethod(method = Method.GET, url = "/get/hello/world")
    String getHelloWorld(String hello);
}
```

### Post、Put 請求中加上 Query

```java
@HuaHttp("http://hello-world.com")
public interface TestHttp {
    
    /*
     * http post http://hello-world.com/post/hello/world?param=xxx
     * body = {
     *     "hello": "xxx"
     * }
     */
    @HuaMethod(method = Method.POST, url = "/post/hello/world")
    String postHelloWorld(String hello, @HuaParam String param);
}
```

### 使用 Form 表單發送 Post、Put 請求

```java
@HuaHttp("http://hello-world.com")
public interface TestHttp {
    
    /*
     * http post http://hello-world.com/post/hello/world
     * body = hello=xxx&world=xxx
     */
    @HuaMethod(method = Method.POST, url = "/post/hello/world", form = true)
    String postHelloWorld(String hello, String world);
}
```

### 使用對象發送 Get、Post、Put、Delete 請求

```java
@Data
public class YourBean {
    String hello;
    String world;
}

@HuaHttp("http://hello-world.com")
public interface TestHttp {
    
    /*
     * http post http://hello-world.com/post/hello/world?hello=xxx&world=xxx
     * body = {
     *     "hello": "xxx",
     *     "world": "xxx"
     * }
     */
    @HuaMethod(method = Method.POST, url = "/post/hello/world")
    String postHelloWorld(@HuaParam(type = ParamType.QUERY, full = true) YourBean paramBean, @HuaParam(type = ParamType.BODY, full = true) YourBean bodyBean);
}
```

### 請求地址帶變量

```java
@HuaHttp("http://hello-world.com")
public interface TestHttp {
    
    /*
     * http post http://hello-world.com/post/hello/world/xxx
     */
    @HuaMethod(method = Method.POST, url = "/post/hello/world/{pathValue}")
    String postHelloWorld(@HuaParam(type = ParamType.PATH) String pathValue);
}
```

### 动态 Header

```java
@HuaParam(type = ParamType.HEADER, create = Creator.class) // 全局添加，優先級低
@HuaHttp("http://hello-world.com")
public interface TestHttp {

    /*
     * http get http://hello-world.com/get/hello/world
     * headers {
     *     "big_token1": "big_value1",
     *     "token1": "value1",
     *     "InputToken": "xxx"
     * }
     */
    @HuaMethod(method = Method.GET, url = "/get/hello/world")
    @HuaParam(type = ParamType.HEADER, create = Creator.class) // 此方法添加，優先級高
    String testHeader(@HuaParam(type = ParamType.HEADER, name = "InputToken") String token); // 變量添加
}
```

### 自定義請求上下文

```java
@Configuration
public class MyHttpHandlerConfig implements HttpHandlerConfig {
    @Override
    public HttpHandlerSetting getSetting() {
        HttpHandlerSetting setting = new HttpHandlerSetting();
        setting.addMethod("please_tag_me", new HttpHandlerMethod() {

            final ThreadLocal<String> tlUrl = new ThreadLocal<>();

            @Override
            public void beforeHttpMethod(HttpRequest request) {
                // 请求前操作
                tlUrl.set(request.getUrl());
            }

            @Override
            public void afterHttpMethod(HttpResponse response) {
                // 请求后操作
                if (!response.isOk()) {
                    log.error("请求地址 {} 不成功，返回内容为 {}！", tlUrl.get(), response.body());
                }
            }

            @Override
            public String preHandleResponse(String originString) {
                // 请求结果预处理
                // 假如函数接口返回值写的 Happy
                // 真实返回值是 Response<Happy>
                // 则透过此方法转换为 Happy
                Response<String> response = JsonMan.fromJson(originString, new TypeReference<Response<String>>() {
                }.getType());
                if (response.getCode().equals(Response.ERROR)) {
                    throw new RequestException(response.getMsg());
                }
                return response.getContent();
            }

        });
        setting.addMoreScanPackage("代码内写死额外的扫描包");
        return setting;
    }
}
```

```java
@HuaAop("please_tag_me")
@HuaHttp("http://hello-world.com")
public interface TestHttp {

    /*
     * http get http://hello-world.com/get/hello/world?hello=xxx
     */
    @HuaAop("please_tag_me")
    @HuaParam(type = ParamType.GET, url = "/get/hello/world")
    Happy getHelloWorld(String hello);
}
```

### 返回流（InputStream）

```java
@HuaHttp("http://hello-world.com")
public interface TestHttp {

    /*
     * http get http://hello-world.com/download
     */
    @HuaGet(url = "/download")
    InputStream download();

    /*
     * http get http://hello-world.com/stream
     */
    @HuaStream(
        limit = DefaultStreamLimit.class // 重要：请务必自定义获取数据总量的方法
        // 预设使用 response headers 内的 USER-DEFINED-DATA-COUNT 字段
    )
    @HuaGet(url = "/stream")
    Stream<XXX> stream();

}
```

## 這個項目使用的依賴包

### hutool-http

用於請求 Http [Hutool](https://github.com/looly/hutool)

```xml
<dependency>
    <groupId>cn.hutool</groupId>
    <artifactId>hutool-http</artifactId>
    <version>5.5.2</version>
</dependency>
```

### fast-json

用於解析請求返回值 Json [FastJson](https://github.com/alibaba/fastjson)

```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>1.2.73</version>
</dependency>
```
