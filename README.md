# SpringBoot 接口式註解，快速開發 HTTP 請求函數

hua-http-spring-boot-starter

## :pencil2: 嗨

SpringBoot 小白的我，歡迎大家 Issues、Fork、Pull Requests :smile:。

## POM 引入

```xml
<dependency>
    <groupId>com.github.hua777</groupId>
    <artifactId>hua-http-spring-boot-starter</artifactId>
    <version>1.0.6-RELEASE</version>
</dependency>
```

## 註解

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

## 配置文件設置掃描路徑（可選，默認掃描啟動類下所有包）

```yaml
com:
  github:
    hua777:
      hua-http-spring-boot-starter:
        scan-packages: xxx.xxx.xxx1,xxx.xxx.xxx2
```

## 教學

### 基礎使用

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

### 請求時帶上 Header

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

### 請求時使用 Token

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

### 自定義請求上下文

註冊預設配置

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

註冊自定義名稱配置

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

使用自定義名稱配置

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

## 這個項目使用的依賴包

### hutool-all

用於請求 Http [Hutool](https://github.com/looly/hutool)

```xml
<dependency>
    <groupId>cn.hutool</groupId>
    <artifactId>hutool-all</artifactId>
    <version>5.3.8</version>
</dependency>
```

### gson

用於解析請求返回值 Json [Gson](https://github.com/google/gson)

```xml
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.8.6</version>
</dependency>
```

### fast-json

用於解析請求返回值 Json [FastJson](https://github.com/alibaba/fastjson)

```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>1.2.66</version>
</dependency>
```

### java-jwt

用於請求頭加上 Token [java-jwt](https://github.com/auth0/java-jwt)

```xml
<dependency>
    <groupId>com.auth0</groupId>
    <artifactId>java-jwt</artifactId>
    <version>3.10.3</version>
</dependency>
```

