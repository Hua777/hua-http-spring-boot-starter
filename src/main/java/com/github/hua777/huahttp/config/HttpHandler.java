package com.github.hua777.huahttp.config;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.github.hua777.huahttp.annotation.HuaAop;
import com.github.hua777.huahttp.annotation.HuaHttp;
import com.github.hua777.huahttp.annotation.enumrate.HttpMethod;
import com.github.hua777.huahttp.annotation.method.*;
import com.github.hua777.huahttp.annotation.param.HuaBody;
import com.github.hua777.huahttp.annotation.param.HuaHeader;
import com.github.hua777.huahttp.annotation.param.HuaParam;
import com.github.hua777.huahttp.annotation.param.HuaPath;
import com.github.hua777.huahttp.bean.HttpHandlerMethod;
import com.github.hua777.huahttp.property.HttpProperty;
import com.github.hua777.huahttp.tool.TokenTool;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

public class HttpHandler implements InvocationHandler {

    static Logger log = LoggerFactory.getLogger(HttpHandler.class);

    Class<?> interfaceClass;

    Environment environment;
    HttpProperty httpProperty;
    HttpHandlerConfig httpHandlerConfig;

    public HttpHandler() {
    }

    public HttpHandler setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
        return this;
    }

    public HttpHandler setEnvironment(Environment environment) {
        this.environment = environment;
        return this;
    }

    public HttpHandler setHttpProperty(HttpProperty httpProperty) {
        this.httpProperty = httpProperty;
        return this;
    }

    public HttpHandler setHttpHandlerConfig(HttpHandlerConfig httpHandlerConfig) {
        this.httpHandlerConfig = httpHandlerConfig;
        return this;
    }

    private String getValue(String key) {
        if (key.startsWith("${") && key.endsWith("}")) {
            key = key.substring(2);
            key = key.substring(0, key.length() - 1);
            return environment.getProperty(key);
        }
        return key;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {

        HttpMethod httpMethod = HttpMethod.Get;

        HashMap<String, String> headers = new HashMap<>();
        HashMap<String, Object> params = new HashMap<>();
        HashMap<String, Object> bodies = new HashMap<>();
        HashMap<String, String> paths = new HashMap<>();

        String baseUrl = "";
        String subUrl = "";
        String fullUrl;

        //region 获取切片方法
        HttpHandlerMethod<Object> aopMethod = null;
        if (httpHandlerConfig != null) {
            HuaAop huaAop = interfaceClass.getAnnotation(HuaAop.class);
            if (huaAop == null) {
                huaAop = method.getAnnotation(HuaAop.class);
            }
            if (huaAop != null) {
                String methodName = getValue(huaAop.value());
                aopMethod = httpHandlerConfig.getSetting().getMethod(methodName);
                if (aopMethod == null) {
                    log.debug("无法从配置文件中找到 {} 函数", methodName);
                }
            }
        }
        //endregion

        if (aopMethod != null) {
            args = aopMethod.start(method, args);
        }

        //region 检查是否为表单类型
        boolean isForm = false;
        HuaForm huaForm = method.getAnnotation(HuaForm.class);
        if (huaForm != null) {
            isForm = huaForm.value();
        }
        //endregion

        //region 处理地址与请求方法
        HuaHttp huaHttp = interfaceClass.getAnnotation(HuaHttp.class);
        if (huaHttp != null) {
            baseUrl = getValue(huaHttp.value());
        }
        HuaGet huaGet = method.getAnnotation(HuaGet.class);
        HuaPost huaPost = method.getAnnotation(HuaPost.class);
        HuaPut huaPut = method.getAnnotation(HuaPut.class);
        HuaDelete huaDelete = method.getAnnotation(HuaDelete.class);
        Annotation huaMethodChild = null;
        if (huaGet != null) {
            subUrl = getValue(huaGet.url());
            huaMethodChild = huaGet;
        } else if (huaPost != null) {
            subUrl = getValue(huaPost.url());
            huaMethodChild = huaPost;
        } else if (huaPut != null) {
            subUrl = getValue(huaPut.url());
            huaMethodChild = huaPut;
        } else if (huaDelete != null) {
            subUrl = getValue(huaDelete.url());
            huaMethodChild = huaDelete;
        }
        fullUrl = baseUrl + subUrl;
        if (fullUrl.equals("")) {
            throw new InvalidParameterException("请求地址为空！");
        }
        if (huaMethodChild != null) {
            HuaMethod huaMethod = huaMethodChild.annotationType().getAnnotation(HuaMethod.class);
            if (huaMethod != null) {
                httpMethod = huaMethod.method();
            }
        }
        //endregion

        //region 处理 Token
        HuaToken huaToken = interfaceClass.getAnnotation(HuaToken.class);
        if (huaToken == null) {
            huaToken = method.getAnnotation(HuaToken.class);
        }
        if (huaToken != null) {
            String key = getValue(huaToken.key());
            String iss = getValue(huaToken.iss());
            String sub = getValue(huaToken.sub());
            long iat = Long.parseLong(getValue(huaToken.issuedAtTimeThresholdMs()));
            long vp = Long.parseLong(getValue(huaToken.validityPeriodMs()));
            String token = TokenTool.createJWTByHMAC256(key, iss, sub, iat, vp);
            headers.put(getValue(huaToken.name()), token);
        }
        //endregion

        //region 处理 Headers
        HuaHeader typeHeader = interfaceClass.getAnnotation(HuaHeader.class);
        if (typeHeader != null) {
            String[] names = typeHeader.names();
            String[] values = typeHeader.values();
            if (names.length != values.length) {
                throw new InvalidParameterException("Header names 与 values 长度不匹配。");
            }
            for (int i = 0; i < names.length; ++i) {
                headers.put(getValue(names[i]), getValue(values[i]));
            }
        }

        HuaHeader methodHeader = method.getAnnotation(HuaHeader.class);
        if (methodHeader != null) {
            String[] names = methodHeader.names();
            String[] values = methodHeader.values();
            if (names.length != values.length) {
                throw new InvalidParameterException("Header names 与 values 长度不匹配。");
            }
            for (int i = 0; i < names.length; ++i) {
                headers.put(getValue(names[i]), getValue(values[i]));
            }
        }
        //endregion

        //region 处理 Params
        HuaParam methodParam = method.getAnnotation(HuaParam.class);
        if (methodParam != null) {
            String[] names = methodParam.names();
            String[] values = methodParam.values();
            if (names.length != values.length) {
                throw new InvalidParameterException("Param names 与 values 长度不匹配。");
            }
            for (int i = 0; i < names.length; ++i) {
                params.put(getValue(names[i]), getValue(values[i]));
            }
        }
        //endregion

        //region 处理 Bodies
        HuaBody methodBody = method.getAnnotation(HuaBody.class);
        if (methodBody != null) {
            String[] names = methodBody.names();
            String[] values = methodBody.values();
            if (names.length != values.length) {
                throw new InvalidParameterException("Body names 与 values 长度不匹配。");
            }
            for (int i = 0; i < names.length; ++i) {
                bodies.put(getValue(names[i]), getValue(values[i]));
            }
        }
        //endregion

        //region 处理 Paths
        HuaPath methodPath = method.getAnnotation(HuaPath.class);
        if (methodPath != null) {
            String[] names = methodPath.names();
            String[] values = methodPath.values();
            if (names.length != values.length) {
                throw new InvalidParameterException("Path names 与 values 长度不匹配。");
            }
            for (int i = 0; i < names.length; ++i) {
                paths.put(getValue(names[i]), getValue(values[i]));
            }
        }
        //endregion

        //region 处理参数
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; ++i) {
            Parameter parameter = parameters[i];
            Object arg = args[i];
            HuaParam huaParam = parameter.getAnnotation(HuaParam.class);
            HuaBody huaBody = parameter.getAnnotation(HuaBody.class);
            HuaPath huaPath = parameter.getAnnotation(HuaPath.class);
            HuaHeader huaHeader = parameter.getAnnotation(HuaHeader.class);
            //region 处理参数名与参数值转换
            String paramName = parameter.getName();
            String paramMethodName = "";
            if (huaParam != null) {
                if (!huaParam.name().equals("")) {
                    paramName = getValue(huaParam.name());
                }
                paramMethodName = huaParam.method();
            } else if (huaBody != null) {
                if (!huaBody.name().equals("")) {
                    paramName = getValue(huaBody.name());
                }
                paramMethodName = huaBody.method();
            } else if (huaPath != null) {
                if (!huaPath.name().equals("")) {
                    paramName = getValue(huaPath.name());
                }
                paramMethodName = huaPath.method();
            } else if (huaHeader != null) {
                if (!huaHeader.name().equals("")) {
                    paramName = getValue(huaHeader.name());
                }
                paramMethodName = huaHeader.method();
            }
            if (!paramMethodName.equals("")) {
                arg = parameter.getType().getMethod(paramMethodName).invoke(arg);
            }
            //endregion
            if (huaParam != null) {
                params.put(paramName, arg.toString());
            } else if (huaBody != null) {
                bodies.put(paramName, arg);
            } else if (huaPath != null) {
                paths.put(paramName, arg.toString());
            } else if (huaHeader != null) {
                headers.put(paramName, arg.toString());
            } else {
                if (huaPost != null || huaPut != null) {
                    bodies.put(paramName, arg);
                } else {
                    params.put(paramName, arg.toString());
                }
            }
        }
        //endregion

        //region 处理 Paths
        for (Map.Entry<String, String> entry : paths.entrySet()) {
            fullUrl = fullUrl.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        //endregion

        Gson gson = new Gson();

        log.debug("============ Hua-Http Invoke Debug Start ============");
        log.debug("Full Url: {}, Http Type: {}, Params: {}, Bodies: {}, Headers: {}",
                fullUrl,
                httpMethod.name(),
                gson.toJson(params),
                gson.toJson(bodies),
                gson.toJson(headers)
        );
        log.debug("======================================================");

        //region 处理返回值
        fullUrl = HttpUtil.urlWithForm(fullUrl, params, StandardCharsets.UTF_8, true);

        if (aopMethod != null) {
            aopMethod.beforeHttpMethod(fullUrl, httpMethod, bodies, headers);
        }

        HttpRequest req = null;
        switch (httpMethod) {
            case Get:
                req = HttpRequest.get(fullUrl);
                break;
            case Post:
                if (isForm) {
                    req = HttpRequest.post(fullUrl).contentType("application/x-www-form-urlencoded").form(bodies);
                } else {
                    req = HttpRequest.post(fullUrl).body(gson.toJson(bodies));
                }
                break;
            case Put:
                if (isForm) {
                    req = HttpRequest.put(fullUrl).contentType("application/x-www-form-urlencoded").form(bodies);
                } else {
                    req = HttpRequest.put(fullUrl).body(gson.toJson(bodies));
                }
                break;
            case Delete:
                req = HttpRequest.delete(fullUrl);
                break;
        }
        HttpResponse response = req.addHeaders(headers).setFollowRedirects(true).execute();

        if (aopMethod != null) {
            aopMethod.afterHttpMethod(response);
        }

        String resultString = response.body();

        log.debug("============ Hua-Http Invoke Debug End ============");
        log.debug("Return String: {}", resultString);
        log.debug("====================================================");

        if (method.getReturnType().getTypeName().equals("void")) return null;

        Object resultObject = gson.fromJson(resultString, method.getGenericReturnType());
        //endregion

        if (aopMethod != null) {
            resultObject = aopMethod.end(method, resultObject);
        }

        return resultObject;
    }
}
