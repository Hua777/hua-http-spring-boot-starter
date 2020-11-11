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
    <version>1.1.9-RELEASE</version>
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
    @HuaForm
    @HuaPost(url = "/post/hello/world")
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
    String postHelloWorld(@HuaParam(full=true) YourBean paramBean, @HuaBody(full=true) YourBean bodyBean);
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

### 請求時帶上 Header

```java
@HuaHeader(names = {"big_token1"}, values = {"big_value1"}) // 全局添加，優先級低
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
    @HuaHeader(names = {"token1"}, values = {"value1"}) // 此方法添加，優先級高
    String testHeader(@HuaHeader(name = "InputToken") String token); // 變量添加
}
```

### 請求時使用 Token

```java
@HuaToken(name = "name1", key = "key1", iss = "iss1", sub = "sub1") // 全局添加，優先級低
@HuaHttp("http://hello-world.com")
public interface TestHttp {

    /*
     * http get http://hello-world.com/get/hello/world
     * headers {
     *     "name2": "token2"
     * }
     */
    @HuaGet(url = "/get/hello/world")
    @HuaToken(name = "name2", key = "key2", iss = "iss2", sub = "sub2") // 此方法添加，優先級高
    String testToken2();
}
```

### 自定義請求上下文（預設）

```java
@Configuration
public class MyHttpHandlerConfig implements HttpHandlerConfig {
    @Override
    public HttpHandlerSetting getSetting() {
        HttpHandlerSetting setting = new HttpHandlerSetting();
        setting.defaultMethod(new HttpHandlerMethod<YourBean>() {
            @Override
            public void beforeHttpMethod(HttpRequest request) {

            }

            @Override
            public void afterHttpMethod(HttpResponse response) {

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

### 自定義請求上下文（指定）

```java
@Configuration
public class MyHttpHandlerConfig implements HttpHandlerConfig {
    @Override
    public HttpHandlerSetting getSetting() {
        HttpHandlerSetting setting = new HttpHandlerSetting();
        setting.addMethod("pleaseTagMe", new HttpHandlerMethod<YourBean>() {
            @Override
            public void beforeHttpMethod(HttpRequest request) {

            }

            @Override
            public void afterHttpMethod(HttpResponse response) {

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

### 使用轉換器

有個接口定義如下，返回復數

```java
@PostMapping("/test")
public Response<Complex> testPost(@RequestBody Complex complex) {
    return new Response<>(complex);
}
```

妳想把請求這個接口返回的復數轉成可愛的復數，並且不含 Response

先定義請求接口

```java
@HuaHttp("xxx")
public interface TestHttp {

    @HuaConvert(CuteConverter.class) // 定義轉換器類
    @HuaPost(url = "/test")
    CuteComplex getHelloWorld(Complex complex);
}
```

在定義轉換器

```java
@Configuration
public class CuteConverter implements Converter<CuteComplex> {
    @Override
    public CuteComplex convert(String responseBody, Type returnType, JsonMan jsonMan) {
        Response<Complex> resp = jsonMan.fromJson(responseBody, new TypeReference<Response<Complex>>(){}.getType());
        CuteComplex complex = new CuteComplex(resp.getData().getReal(), resp.getData().getImage());
        return complex;
    }
}
```

完成

### 動態 Header

```java
@HuaHeader(creator = MyHeaderCreator.class)
@HuaHttp("http://hello-world.com")
public interface TestHttp {

    /*
     * http get http://hello-world.com/get/hello/world?hello=xxx
     */
    @HuaHeader(creator = MyHeaderCreator.class)
    @HuaGet(url = "/get/hello/world")
    String getHelloWorld(String hello);

}
```

```java
@Configuration
public class MyHeaderCreator implements HeadersCreator {

    @Override
    public HashMap<String, String> headers() {
        return new HashMap<String, String>() {{
            put("key", "value");
        }};
    }

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
        bufferSize = 1024, // 依次读取大小
        endCharacter = "\n", // 获取数据时根据此字符串拆分成 XXX 给您
        limit = DefaultStreamLimit.class // 重要：请务必自定义获取数据总量的方法
        // 预设使用 response headers 内的 USER-DEFINED-DATA-COUNT 字段
    )
    @HuaGet(url = "/stream")
    Stream<XXX> stream();

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
    <version>1.2.73</version>
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

