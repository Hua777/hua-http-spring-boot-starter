# 快速的请求 HTTP

hua-http-spring-boot-starter

## :pencil2: 恳求

SpringBoot 小白的我，欢迎大家发 Issues、Fork、Pull Requests :smile:。

## 引用

```xml
<dependency>
    <groupId>com.github.hua777</groupId>
    <artifactId>hua-http-spring-boot-starter</artifactId>
    <version>1.0.4-RELEASE</version>
</dependency>
```

备注：还没上传

## 注解

```java
// TYPE
@HuaHttp
@HuaToken
@HuaHeader
@HuaAop

// METHOD
@HuaGet
@HuaPost
@HuaPut
@HuaDelete
@HuaToken
@HuaHeader
@HuaForm
@HuaAop

// PARAMETER
@HuaParam
@HuaBody
@HuaHeader
@HuaPath
```

## 配置扫描包

```yaml
com:
  github:
    hua777:
      hua-http-spring-boot-starter:
        scan-packages: xxx.xxx.xxx1,xxx.xxx.xxx2
```

## 教程

### 基础使用

```java
@HuaHttp("http://hello-world.com")
public interface TestHttp {

    /*
     * http get http://hello-world.com/get/hello/world?hello=xxx
     */
    @HuaGet(url = "/get/hello/world")
    String getHelloWorld(String hello);
    
    /*
     * http post http://hello-world.com/post/hello/world
     * body = {
     *     "hello": "xxx"
     * }
     */
    @HuaPost(url = "/post/hello/world")
    String postHelloWorld(String hello); // default is body

    /*
     * http post http://hello-world.com/post/hello/world?world=xxx
     * body = {
     *     "hello": "xxx"
     * }
     */
    @HuaPost(url = "/post/hello/world")
    String postHelloWorldButUseParam(String hello, @HuaParam String world);
    
    /*
     * http get http://hello-world.com/get/hello/world/xxx
     */
    @HuaGet(url = "/get/hello/world/{path}")
    String testPath(@HuaPath String path);
}
```

### Header 赋值

```java
@HuaHeader(names = {"big_token1"}, values = {"big_value1"})
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
    @HuaHeader(names = {"token1"}, values = {"value1"})
    String testHeader(@HuaHeader(name = "InputToken") String token);
}
```

### Token 使用

```java
@HuaToken(name = "name1", key = "key1", iss = "iss1", sub = "sub1")
@HuaHttp("http://hello-world.com")
public interface TestHttp {

    /*
     * http get http://hello-world.com/get/hello/world
     * headers {
     *     "name1": "token1"
     * }
     */
    @HuaGet(url = "/get/hello/world")
    String testToken1();

    /*
     * http get http://hello-world.com/get/hello/world
     * headers {
     *     "name2": "token2"
     * }
     */
    @HuaGet(url = "/get/hello/world")
    @HuaToken(name = "name2", key = "key2", iss = "iss2", sub = "sub2")
    String testToken2();
}
```

### AOP 使用

注册预设方法

```java
@Configuration
public class MyHttpHandlerConfig implements HttpHandlerConfig {
    @Override
    public HttpHandlerSetting getSetting() {
        HttpHandlerSetting setting = new HttpHandlerSetting();
        setting.defaultMethod(new HttpHandlerMethod<YourBean>() {
            @Override
            public Object[] start(Method method, Object[] args) {
                return args;
            }

            @Override
            public void beforeHttpMethod(String fullUrl, HttpMethod httpMethod, Map<String, Object> bodies, Map<String, String> headers) {

            }

            @Override
            public void afterHttpMethod(HttpResponse result) {

            }

            @Override
            public YourBean end(Method method, YourBean result) {
                return result;
            }
        });
        return setting;
    }
}
```

```java
@HuaAop
@HuaHttp("http://hello-world.com")
public interface TestHttp {

    /*
     * http get http://hello-world.com/get/hello/world?hello=xxx
     */
    @HuaAop
    @HuaGet(url = "/get/hello/world")
    String getHelloWorld(String hello);
}
```

注册变量方法

```java
@Configuration
public class MyHttpHandlerConfig implements HttpHandlerConfig {
    @Override
    public HttpHandlerSetting getSetting() {
        HttpHandlerSetting setting = new HttpHandlerSetting();
        setting.addMethod("pleaseTagMe", new HttpHandlerMethod<YourBean>() {
            @Override
            public Object[] start(Method method, Object[] args) {
                return args;
            }

            @Override
            public void beforeHttpMethod(String fullUrl, HttpMethod httpMethod, Map<String, Object> bodies, Map<String, String> headers) {

            }

            @Override
            public void afterHttpMethod(HttpResponse result) {

            }

            @Override
            public YourBean end(Method method, YourBean result) {
                return result;
            }
        });
        return setting;
    }
}
```

```java
@HuaAop("pleaseTagMe")
@HuaHttp("http://hello-world.com")
public interface TestHttp {

    /*
     * http get http://hello-world.com/get/hello/world?hello=xxx
     */
    @HuaAop("pleaseTagMe")
    @HuaGet(url = "/get/hello/world")
    String getHelloWorld(String hello);
}
```

## 这里使用到的依赖

### hutool-all

用于请求 Http [Hutool](https://github.com/looly/hutool)

```xml
<dependency>
    <groupId>cn.hutool</groupId>
    <artifactId>hutool-all</artifactId>
    <version>5.3.8</version>
</dependency>
```

### gson

用于解析请求返回 Json [Gson](https://github.com/google/gson)

```xml
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.8.6</version>
</dependency>
```

### java-jwt

用于请求头加上 Token [java-jwt](https://github.com/auth0/java-jwt)

```xml
<dependency>
    <groupId>com.auth0</groupId>
    <artifactId>java-jwt</artifactId>
    <version>3.10.3</version>
</dependency>
```
