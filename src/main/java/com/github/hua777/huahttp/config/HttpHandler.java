package com.github.hua777.huahttp.config;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.github.hua777.huahttp.annotation.HuaAop;
import com.github.hua777.huahttp.annotation.HuaHttp;
import com.github.hua777.huahttp.annotation.HuaMethod;
import com.github.hua777.huahttp.annotation.HuaParam;
import com.github.hua777.huahttp.bean.JsonMan;
import com.github.hua777.huahttp.bean.ValueMan;
import com.github.hua777.huahttp.config.aop.HttpHandlerConfig;
import com.github.hua777.huahttp.config.aop.HttpHandlerMethod;
import com.github.hua777.huahttp.config.converter.DefaultParamConverter;
import com.github.hua777.huahttp.config.creator.DefaultParamCreator;
import com.github.hua777.huahttp.config.limiter.InputStreamSupplier;
import com.github.hua777.huahttp.enumrate.ParamType;
import com.github.hua777.huahttp.property.HttpProperty;
import com.github.hua777.huahttp.tool.MapTool;
import com.github.hua777.huahttp.tool.ReflectTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;

import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class HttpHandler implements InvocationHandler {

    static Logger log = LoggerFactory.getLogger(HttpHandler.class);

    Class<?> interfaceClass;

    public HttpHandler(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    static Environment environment = HttpRegistry.APP_CONTEXT.getBean(Environment.class);
    static HttpProperty httpProperty = HttpRegistry.APP_CONTEXT.getBean(HttpProperty.class);
    static HttpHandlerConfig httpHandlerConfig = HttpHandlerConfig.merge();

    private static String getValue(String key) {
        return ValueMan.parse(key).toString(environment);
    }

    @SuppressWarnings("unchecked")
    private static Supplier<Map<String, Object>> getHeadersCreator(Class<?> type) {
        return (Supplier<Map<String, Object>>) HttpRegistry.APP_CONTEXT.getBean(type);
    }

    @SuppressWarnings("unchecked")
    private static Function<HttpResponse, Long> getStreamLimiter(Class<?> type) {
        return (Function<HttpResponse, Long>) HttpRegistry.APP_CONTEXT.getBean(type);
    }

    @SuppressWarnings("rawtypes")
    private static Function getParamConverter(Class<?> type) {
        return (Function) HttpRegistry.APP_CONTEXT.getBean(type);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {

        Map<ParamType, Map<String, Object>> params = new HashMap<>();
        for (ParamType paramType : ParamType.values()) {
            params.put(paramType, new HashMap<>());
        }

        String baseUrl;
        String subUrl;
        String fullUrl;

        //region 获取切片方法
        HttpHandlerMethod aopMethod = null;
        if (httpHandlerConfig != null) {
            HuaAop huaAop = AnnotationUtils.getAnnotation(interfaceClass, HuaAop.class);
            if (huaAop == null) {
                huaAop = AnnotationUtils.getAnnotation(method, HuaAop.class);
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
            aopMethod = new HttpHandlerMethod() {
            };
        }
        //endregion

        //region 处理地址与请求方法
        HuaHttp huaHttp = AnnotationUtils.getAnnotation(interfaceClass, HuaHttp.class);
        HuaMethod huaMethod = AnnotationUtils.getAnnotation(method, HuaMethod.class);
        assert huaHttp != null;
        assert huaMethod != null;
        baseUrl = getValue(huaHttp.value());
        subUrl = getValue(huaMethod.url());
        fullUrl = baseUrl + subUrl;
        if (StrUtil.isEmpty(fullUrl)) {
            throw new IllegalArgumentException("请求地址为空！");
        }
        //endregion

        //region 处理 类上的 Params
        HuaParam[] huaClassParams = interfaceClass.getAnnotationsByType(HuaParam.class);
        for (HuaParam huaParam : huaClassParams) {
            if (huaParam.create().isAssignableFrom(DefaultParamCreator.class)) {
                continue;
            }
            Map<String, Object> param = params.get(huaParam.type());
            MapTool.mergeToLeft(param, getHeadersCreator(huaParam.create()).get());
        }
        //endregion

        //region 处理 函数上的 Params
        HuaParam[] huaMethodParams = method.getAnnotationsByType(HuaParam.class);
        for (HuaParam huaParam : huaMethodParams) {
            if (huaParam.create().isAssignableFrom(DefaultParamCreator.class)) {
                continue;
            }
            Map<String, Object> param = params.get(huaParam.type());
            MapTool.mergeToLeft(param, getHeadersCreator(huaParam.create()).get());
        }
        //endregion

        //region 处理 参数上的 Params
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; ++i) {
            Parameter parameter = parameters[i];
            HuaParam huaParam = AnnotationUtils.getAnnotation(parameter, HuaParam.class);
            String paramName = getValue(parameter.getName());
            Object paramValue = args[i];
            if (huaParam != null) {
                if (StrUtil.isNotBlank(huaParam.name())) {
                    // 自定义参数名
                    paramName = huaParam.name();
                }
                if (!huaParam.convert().isAssignableFrom(DefaultParamConverter.class)) {
                    // 自定义参数转换器
                    paramValue = getParamConverter(huaParam.convert()).apply(paramValue);
                }
            }
            Map<String, Object> param;
            if (huaParam == null) {
                // 没有指定参数类型时，预设根据方法类型指定参数类型
                switch (huaMethod.method()) {
                    case GET:
                    case DELETE:
                        param = params.get(ParamType.QUERY);
                        break;
                    case PUT:
                    case POST:
                        param = params.get(ParamType.BODY);
                        break;
                    default:
                        throw new RuntimeException("不支持的类型");
                }
            } else {
                param = params.get(huaParam.type());
            }
            if (huaParam != null && huaParam.full()) {
                MapTool.mergeToLeft(param, JsonMan.toMapStringObject(paramValue));
            } else {
                paramValue = JsonMan.prepareArgs(paramValue);
                param.put(paramName, paramValue);
            }
        }
        //endregion

        //region 处理 Paths
        for (Map.Entry<String, Object> entry : params.get(ParamType.PATH).entrySet()) {
            fullUrl = fullUrl.replace("{" + entry.getKey() + "}", entry.getValue().toString());
        }
        //endregion

        //region 处理 Query
        fullUrl = HttpUtil.urlWithForm(fullUrl, params.get(ParamType.QUERY), StandardCharsets.UTF_8, true);
        //endregion

        // 是否返回串流
        boolean isReturnInputStream = ReflectTool.fromClass(method.getReturnType(), InputStream.class);

        // 是否返回流
        boolean isReturnStream = ReflectTool.isClass(method.getReturnType(), Stream.class);

        //region 创建请求
        HttpRequest request = (new HttpRequest(fullUrl))
                .method(huaMethod.method())
                .timeout(httpProperty.getHttpTimeoutSeconds() * 1000)
                .addHeaders(JsonMan.fromJsonCast(JsonMan.toJson(params.get(ParamType.HEADER)),
                        new TypeReference<Map<String, String>>() {
                        }.getType()))
                .setFollowRedirects(httpProperty.getHttpRedirects());
        switch (huaMethod.method()) {
            case POST:
            case PUT:
                if (huaMethod.form()) {
                    request = request.contentType("application/x-www-form-urlencoded").form(params.get(ParamType.BODY));
                } else {
                    request = request.contentType("application/json").body(JsonMan.toJson(params.get(ParamType.BODY)));
                }
                break;
        }
        //endregion

        aopMethod.beforeHttpMethod(request);

        HttpResponse response;

        //region 发送请求
        if (isReturnInputStream || isReturnStream) {
            response = request.executeAsync();
        } else {
            response = request.execute();
        }
        //endregion

        aopMethod.afterHttpMethod(response);

        //region 处理返回值
        if (isReturnInputStream) {
            return response.bodyStream();
        } else if (isReturnStream) {
            Function<HttpResponse, Long> streamLimiter = getStreamLimiter(huaMethod.streamLimit());
            long count = streamLimiter.apply(response);
            InputStreamSupplier supplier = new InputStreamSupplier(
                    ReflectTool.getActualTypes(method.getGenericReturnType())[0],
                    response.bodyStream()
            );
            return Stream.generate(supplier).limit(count);
        } else {
            String resultString = response.body();
            resultString = aopMethod.preHandleResponse(resultString);
            return JsonMan.fromJson(resultString, method.getGenericReturnType());
        }
        //endregion
    }
}
