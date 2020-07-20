# 快速的请求 HTTP

hua-http-spring-boot-starter

## 引用

```xml
<dependency>
    <groupId>com.hua.sss</groupId>
    <artifactId>hua-http-spring-boot-starter</artifactId>
    <version>1.0.0-RELEASE</version>
</dependency>
```

## Annotations

```java
// TYPE
@HuaHttp

// METHOD
@HuaGet
@HuaPost
@HuaPut
@HuaDelete

// PARAMETER
@HuaParam
@HuaBody
@HuaHeader
@HuaPath
```

## 范例

```yaml
hua777:
  spring-boot-starter:
    http:
      scan-packages: xxx.xxx.xxx
```

```java
pacakge xxx.xxx.xxx

@HuaHttp("${test.url}")
public interface TestHttp {
    @HuaHeader(
        names = {"key1", "key2"},
        values = {"value1", "value2"}
    )
    @HuaGet("/test/{hey}")
    String test(
        String arg0001, 
        @HuaBody(name = "arg0002_change", method = "toString") String arg0002, 
        @HuaHeader String arg0003, 
        @HuaPath String hey
    );
}
```

```java
@Autowired
TestHttp httpTest;

httpTest.test("", "", "", "");
```

## GitHub

https://github.com/Hua777/hua-http-spring-boot-starter