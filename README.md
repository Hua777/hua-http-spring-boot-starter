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
    <version>2.0.1</version>
</dependency>
```

## 配置文件

```yaml
com:
  github:
    hua777:
      hua-http-spring-boot-starter:
        scan-packages: xxx.xxx.xxx1,xxx.xxx.xxx2 # 自定义额外扫描类
        http-timeout-seconds: 60 # 请求超时时间（秒）
        http-redirects: true # 是否自行重导向
```

## 教學

### 發送 Get、Delete 請求

```java
@HuaHttp("http://hello-world.com")
public interface TestHttp {

    /*
     * http get http://hello-world.com/get/hello/world?hello=xxx
     */
    @HuaGet(url = "/get/hello/world")
    String getHelloWorld(String hello);

    /*
     * http delete http://hello-world.com/delete/hello/world?hello=xxx
     */
    @HuaDelete(url = "/delete/hello/world")
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
    @HuaPost(url = "/post/hello/world")
    String postHelloWorld(String hello); // default is body

    /*
     * http put http://hello-world.com/put/hello/world
     * body = {
     *     "hello": "xxx"
     * }
     */
    @HuaPut(url = "/put/hello/world")
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
    @HuaGet(url = "/get/hello/world")
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
    @HuaPost(url = "/post/hello/world")
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
    @HuaPost(url = "/post/hello/world", form = true)
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
    @HuaPost(url = "/post/hello/world")
    String postHelloWorld(@HuaQuery(full = true) YourBean paramBean, @HuaBody(full = true) YourBean bodyBean);
}
```

### 請求地址帶變量

```java
@HuaHttp("http://hello-world.com")
public interface TestHttp {
    
    /*
     * http post http://hello-world.com/post/hello/world/xxx
     */
    @HuaPost(url = "/post/hello/world/{pathValue}")
    String postHelloWorld(@HuaPath String pathValue);
}
```

### 动态 Header

```java
@HuaHeader(create = Creator.class) // 全局添加，優先級低
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
    @HuaGet(url = "/get/hello/world")
    @HuaHeader(create = Creator.class) // 此方法添加，優先級高
    String testHeader(@HuaHeader(name = "InputToken") String token); // 變量添加
}
```

### 自定義請求上下文

```java
@Configuration
public class MyHandler implements HttpHandler {
    @Override
    public void beforeHttpMethod(HttpRequest request) {
        log.info("请求：" + request.toString());
    }

    @Override
    public void afterHttpMethod(HttpResponse response) {
        log.info("返回：" + response.toString());
    }

    @Override
    public String preHandleResponse(String originString) {
        // 预处理返回值
        return originString;
    }
}
```

```java
@HuaAop(MyHandler.class)
@HuaHttp("http://hello-world.com")
public interface TestHttp {

    /*
     * http get http://hello-world.com/get/hello/world?hello=xxx
     */
    @HuaAop(MyHandler.class)
    @HuaGet(url = "/get/hello/world")
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
    @HuaGet(
        url = "/stream", 
        streamLimit = DefaultStreamLimiter.class // 重要：请务必自定义获取数据总量的方法
        // 预设使用 response headers 内的 USER-DEFINED-DATA-COUNT 字段
    )
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
