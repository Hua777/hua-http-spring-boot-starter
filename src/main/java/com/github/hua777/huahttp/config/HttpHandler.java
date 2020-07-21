package com.github.hua777.huahttp.config;

import com.github.hua777.huahttp.annotation.HuaHttp;
import com.github.hua777.huahttp.annotation.enumrate.HttpMethod;
import com.github.hua777.huahttp.annotation.method.*;
import com.github.hua777.huahttp.annotation.param.HuaBody;
import com.github.hua777.huahttp.annotation.param.HuaHeader;
import com.github.hua777.huahttp.annotation.param.HuaParam;
import com.github.hua777.huahttp.annotation.param.HuaPath;
import com.github.hua777.huahttp.tool.HttpTool;
import com.github.hua777.huahttp.tool.TokenTool;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

public class HttpHandler implements InvocationHandler {

    static Logger log = LoggerFactory.getLogger(HttpHandler.class);

    Environment env;

    Class<?> interfaceClass;

    public HttpHandler(Environment env, Class<?> interfaceClass) {
        this.env = env;
        this.interfaceClass = interfaceClass;
    }

    private String getValue(String key) {
        if (key.startsWith("${") && key.endsWith("}")) {
            key = key.substring(2);
            key = key.substring(0, key.length() - 1);
            return env.getProperty(key);
        }
        return key;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {

        HttpMethod httpMethod = HttpMethod.Get;

        HashMap<String, String> headers = new HashMap<>();
        HashMap<String, String> params = new HashMap<>();
        HashMap<String, Object> bodies = new HashMap<>();
        HashMap<String, String> paths = new HashMap<>();

        String baseUrl = "";
        String subUrl = "";
        String fullUrl;

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
        HuaToken huaToken = method.getAnnotation(HuaToken.class);
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
            fullUrl = fullUrl.replaceAll("\\{" + entry.getKey() + "\\}", entry.getValue());
        }
        //endregion

        Gson gson = new Gson();

        log.debug("============ Hua-Http Invoke Debug Start ============");
        log.debug("Full Url: {}", fullUrl);
        log.debug("Http Type: {}", httpMethod.name());
        log.debug("Params: {}", gson.toJson(params));
        log.debug("Bodies: {}", gson.toJson(bodies));
        log.debug("Headers: {}", gson.toJson(headers));
        log.debug("======================================================");

        //region 处理返回值
        String result = HttpTool.req(fullUrl, httpMethod.name(), params, bodies, headers);
        Class<?> returnType = method.getReturnType();
        switch (returnType.getTypeName()) {
            case "void":
                return null;
            case "java.lang.String":
                return result;
            case "java.lang.Integer":
                return Integer.parseInt(result);
            case "java.lang.Float":
                return Float.parseFloat(result);
            case "java.lang.Double":
                return Double.parseDouble(result);
            case "java.lang.Boolean":
                return Boolean.parseBoolean(result);
        }
        return gson.fromJson(result, returnType);
        //endregion
    }
}