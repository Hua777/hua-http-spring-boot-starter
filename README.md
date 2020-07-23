# 快速的请求 HTTP

hua-http-spring-boot-starter

## :pencil2: 恳求

SpringBoot 小白的我，欢迎大家发 Issues、Fork、Pull Requests :smile:。

## 引用

```xml
<dependency>
    <groupId>com.github.hua777</groupId>
    <artifactId>hua-http-spring-boot-starter</artifactId>
    <version>1.0.2-RELEASE</version>
</dependency>
```

## 注解

```java
// TYPE
@HuaHttp
@HuaToken
@HuaHeader

// METHOD
@HuaGet
@HuaPost
@HuaPut
@HuaDelete
@HuaToken
@HuaHeader
@HuaForm

// PARAMETER
@HuaParam
@HuaBody
@HuaHeader
@HuaPath
```

## 配置扫描包

```yaml
hua777:
  spring-boot-starter:
    http:
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