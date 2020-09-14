package com.github.hua777.huahttp.config;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.hua777.huahttp.annotation.HuaAop;
import com.github.hua777.huahttp.annotation.HuaHttp;
import com.github.hua777.huahttp.annotation.method.*;
import com.github.hua777.huahttp.annotation.param.HuaBody;
import com.github.hua777.huahttp.annotation.param.HuaHeader;
import com.github.hua777.huahttp.annotation.param.HuaParam;
import com.github.hua777.huahttp.annotation.param.HuaPath;
import com.github.hua777.huahttp.bean.JsonMan;
import com.github.hua777.huahttp.config.aop.HttpHandlerConfig;
import com.github.hua777.huahttp.config.aop.HttpHandlerMethod;
import com.github.hua777.huahttp.config.convert.Converter;
import com.github.hua777.huahttp.config.convert.DefaultConverter;
import com.github.hua777.huahttp.config.creator.DefaultHeadersCreator;
import com.github.hua777.huahttp.config.creator.HeadersCreator;
import com.github.hua777.huahttp.property.HttpProperty;
import com.github.hua777.huahttp.tool.MapTool;
import com.github.hua777.huahttp.tool.TokenTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.env.Environment;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpHandler implements InvocationHandler {

    static Logger log = LoggerFactory.getLogger(HttpHandler.class);

    Class<?> interfaceClass;

    Environment environment;
    HttpProperty httpProperty;
    HttpHandlerConfig httpHandlerConfig;
    BeanFactory beanFactory;

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

    public HttpHandler setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
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

    private HeadersCreator getHeadersCreator(HuaHeader huaHeader) throws IllegalAccessException, InstantiationException {
        HeadersCreator creator;
        if (huaHeader != null) {
            Class<? extends HeadersCreator> clazz = huaHeader.creator();
            try {
                creator = beanFactory.getBean(clazz);
            } catch (BeansException ignored) {
                creator = huaHeader.creator().newInstance();
            }
        } else {
            creator = new DefaultHeadersCreator();
        }
        return creator;
    }

    private Converter<?> getConverter(HuaConvert huaConvert) throws IllegalAccessException, InstantiationException {
        Converter<?> converter;
        if (huaConvert != null) {
            Class<? extends Converter> clazz = huaConvert.value();
            try {
                converter = beanFactory.getBean(clazz);
            } catch (BeansException ignored) {
                converter = huaConvert.value().newInstance();
            }
        } else {
            converter = new DefaultConverter();
        }
        return converter;
    }

    private void mergeHeaders(HashMap<String, String> headers, HuaHeader huaHeader) throws IllegalAccessException, InstantiationException {
        if (huaHeader != null) {
            MapTool.merge(headers, getHeadersCreator(huaHeader).headers());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {

        cn.hutool.http.Method httpMethod = cn.hutool.http.Method.GET;

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
                    log.error("无法从配置文件中找到 {} 函数", methodName);
                }
            }
        }
        if (aopMethod == null) {
            aopMethod = new HttpHandlerMethod<Object>() {
            };
        }
        //endregion

        //region 检查是否为表单类型
        boolean isForm = false;
        HuaForm huaForm = method.getAnnotation(HuaForm.class);
        if (huaForm != null) {
            isForm = huaForm.value();
        }
        //endregion

        //region 处理地址与请求方法
        HuaHttp huaHttp = interfaceClass.getAnnotation(HuaHttp.class);
        HuaGet huaGet = method.getAnnotation(HuaGet.class);
        HuaPost huaPost = method.getAnnotation(HuaPost.class);
        HuaPut huaPut = method.getAnnotation(HuaPut.class);
        HuaDelete huaDelete = method.getAnnotation(HuaDelete.class);
        JsonMan jsonMan = JsonMan.of(huaHttp.jsonType());
        if (httpHandlerConfig != null) {
            jsonMan.setGson(httpHandlerConfig.getGson());
        }
        baseUrl = getValue(huaHttp.value());
        if (huaGet != null) {
            subUrl = getValue(huaGet.url());
            httpMethod = cn.hutool.http.Method.GET;
        } else if (huaPost != null) {
            subUrl = getValue(huaPost.url());
            httpMethod = cn.hutool.http.Method.POST;
        } else if (huaPut != null) {
            subUrl = getValue(huaPut.url());
            httpMethod = cn.hutool.http.Method.PUT;
        } else if (huaDelete != null) {
            subUrl = getValue(huaDelete.url());
            httpMethod = cn.hutool.http.Method.DELETE;
        }
        fullUrl = baseUrl + subUrl;
        if (StrUtil.isEmpty(fullUrl)) {
            throw new IllegalArgumentException("请求地址为空！");
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
            String name = getValue(huaToken.name());
            String token = TokenTool.createJWTByHMAC256(key, iss, sub, iat, vp);
            if (!StrUtil.isEmpty(name) && !StrUtil.isEmpty(token)) {
                headers.put(name, token);
            }
        }
        //endregion

        //region 处理 Headers
        HuaHeader typeHeader = interfaceClass.getAnnotation(HuaHeader.class);
        if (typeHeader != null) {
            String[] names = typeHeader.names();
            String[] values = typeHeader.values();
            if (names.length != values.length) {
                throw new IllegalArgumentException("Header names 与 values 长度不匹配。");
            }
            for (int i = 0; i < names.length; ++i) {
                String name = getValue(names[i]);
                String value = getValue(values[i]);
                if (!StrUtil.isEmpty(name) && !StrUtil.isEmpty(value)) {
                    headers.put(name, value);
                }
            }
            mergeHeaders(headers, typeHeader);
        }

        HuaHeader methodHeader = method.getAnnotation(HuaHeader.class);
        if (methodHeader != null) {
            String[] names = methodHeader.names();
            String[] values = methodHeader.values();
            if (names.length != values.length) {
                throw new IllegalArgumentException("Header names 与 values 长度不匹配。");
            }
            for (int i = 0; i < names.length; ++i) {
                String name = getValue(names[i]);
                String value = getValue(values[i]);
                if (!StrUtil.isEmpty(name) && !StrUtil.isEmpty(value)) {
                    headers.put(name, value);
                }
            }
            mergeHeaders(headers, methodHeader);
        }
        //endregion

        //region 处理 Params
        HuaParam methodParam = method.getAnnotation(HuaParam.class);
        if (methodParam != null) {
            String[] names = methodParam.names();
            String[] values = methodParam.values();
            if (names.length != values.length) {
                throw new IllegalArgumentException("Param names 与 values 长度不匹配。");
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
                throw new IllegalArgumentException("Body names 与 values 长度不匹配。");
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
                throw new IllegalArgumentException("Path names 与 values 长度不匹配。");
            }
            for (int i = 0; i < names.length; ++i) {
                paths.put(getValue(names[i]), getValue(values[i]));
            }
        }
        //endregion

        //region 处理参数
        boolean bodyIsFull = false;
        String bodyFullKey = null;
        boolean paramIsFull = false;
        String paramFullKey = null;
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
                if (huaParam.full()) {
                    paramIsFull = true;
                    paramFullKey = paramName;
                }
                params.put(paramName, arg);
            } else if (huaBody != null) {
                if (huaBody.full()) {
                    bodyIsFull = true;
                    bodyFullKey = paramName;
                }
                bodies.put(paramName, arg);
            } else if (huaPath != null) {
                paths.put(paramName, arg.toString());
            } else if (huaHeader != null) {
                String value = arg.toString();
                if (!StrUtil.isEmpty(paramName) && !StrUtil.isEmpty(value)) {
                    headers.put(paramName, value);
                }
            } else {
                if (huaPost != null || huaPut != null) {
                    bodies.put(paramName, arg);
                } else {
                    params.put(paramName, arg);
                }
            }
        }
        //endregion

        //region 处理 Paths
        for (Map.Entry<String, String> entry : paths.entrySet()) {
            fullUrl = fullUrl.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        //endregion

        //region 处理 Params
        if (paramIsFull) {
            fullUrl = HttpUtil.urlWithForm(fullUrl, jsonMan.toMap(params.get(paramFullKey)), StandardCharsets.UTF_8, true);
        } else {
            fullUrl = HttpUtil.urlWithForm(fullUrl, params, StandardCharsets.UTF_8, true);
        }
        //endregion

        //region 发送请求
        HttpRequest req = (new HttpRequest(fullUrl)).method(httpMethod);
        switch (httpMethod) {
            case POST:
            case PUT:
                if (isForm) {
                    req = req.contentType("application/x-www-form-urlencoded");
                    if (bodyIsFull) {
                        req = req.form(jsonMan.toMap(bodies.get(bodyFullKey)));
                    } else {
                        req = req.form(bodies);
                    }
                } else {
                    req = req.contentType("application/json");
                    if (bodyIsFull) {
                        req = req.body(jsonMan.toJson(bodies.get(bodyFullKey)));
                    } else {
                        req = req.body(jsonMan.toJson(bodies));
                    }
                }
                break;
        }
        log.info(JSONObject.toJSONString(headers));
        HttpRequest request = req.addHeaders(headers).setFollowRedirects(true);
        //endregion

        aopMethod.beforeHttpMethod(request);

        HttpResponse response = request.execute();

        aopMethod.afterHttpMethod(response);

        //region 处理返回值
        String resultString = response.body();
        HuaConvert huaConvert = method.getAnnotation(HuaConvert.class);
        Object resultObject = getConverter(huaConvert).convert(resultString, method.getGenericReturnType(), jsonMan);
        //endregion

        return resultObject;
    }
}
