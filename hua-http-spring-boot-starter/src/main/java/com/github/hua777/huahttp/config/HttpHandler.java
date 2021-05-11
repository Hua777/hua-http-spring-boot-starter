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
import com.github.hua777.huahttp.config.converter.DefaultParamConverter;
import com.github.hua777.huahttp.config.creator.DefaultParamCreator;
import com.github.hua777.huahttp.config.limiter.InputStreamSupplier;
import com.github.hua777.huahttp.enumrate.ParamType;
import com.github.hua777.huahttp.property.HttpProperty;
import com.github.hua777.huahttp.tool.MapTool;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.env.Environment;

import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class HttpHandler implements InvocationHandler {

    static Environment environment = HttpRegistry.APP_CONTEXT.getBean(Environment.class);
    static HttpProperty httpProperty = HttpRegistry.APP_CONTEXT.getBean(HttpProperty.class);
    Class<?> interfaceClass;
    public HttpHandler(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    private static String getValue(String key) {
        return ValueMan.parse(key).toString(environment);
    }

    private static com.github.hua777.huahttp.config.handler.HttpHandler getHttpHandlerConfig(Class<?> type) {
        return (com.github.hua777.huahttp.config.handler.HttpHandler) HttpRegistry.APP_CONTEXT.getBean(type);
    }

    @SuppressWarnings("unchecked")
    private static Supplier<Map<String, Object>> getParamCreator(Class<?> type) {
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
        com.github.hua777.huahttp.config.handler.HttpHandler httpHandler = null;
        HuaAop huaAop = AnnotatedElementUtils.findMergedAnnotation(method, HuaAop.class);
        if (huaAop == null) {
            huaAop = AnnotatedElementUtils.findMergedAnnotation(interfaceClass, HuaAop.class);
        }
        if (huaAop != null) {
            httpHandler = getHttpHandlerConfig(huaAop.value());
        }
        //endregion

        //region 处理地址与请求方法
        HuaHttp huaHttp = AnnotatedElementUtils.findMergedAnnotation(interfaceClass, HuaHttp.class);
        HuaMethod huaMethod = AnnotatedElementUtils.findMergedAnnotation(method, HuaMethod.class);
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
        Set<HuaParam> huaClassParams = AnnotatedElementUtils.findAllMergedAnnotations(interfaceClass, HuaParam.class);
        for (HuaParam huaParam : huaClassParams) {
            Map<String, Object> param = params.get(huaParam.type());
            for (int i = 0; i < huaParam.names().length; ++i) {
                String tmpName = getValue(huaParam.names()[i]);
                String tmpValue = getValue(huaParam.values()[i]);
                param.put(tmpName, tmpValue);
            }
            if (huaParam.create().isAssignableFrom(DefaultParamCreator.class)) {
                continue;
            }
            MapTool.mergeToLeft(param, getParamCreator(huaParam.create()).get());
        }
        //endregion

        //region 处理 函数上的 Params
        Set<HuaParam> huaMethodParams = AnnotatedElementUtils.findAllMergedAnnotations(method, HuaParam.class);
        for (HuaParam huaParam : huaMethodParams) {
            Map<String, Object> param = params.get(huaParam.type());
            for (int i = 0; i < huaParam.names().length; ++i) {
                String tmpName = getValue(huaParam.names()[i]);
                String tmpValue = getValue(huaParam.values()[i]);
                param.put(tmpName, tmpValue);
            }
            if (huaParam.create().isAssignableFrom(DefaultParamCreator.class)) {
                continue;
            }
            MapTool.mergeToLeft(param, getParamCreator(huaParam.create()).get());
        }
        //endregion

        //region 处理 参数上的 Params
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; ++i) {
            Parameter parameter = parameters[i];
            HuaParam huaParam = AnnotatedElementUtils.findMergedAnnotation(parameter, HuaParam.class);
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
        boolean isReturnInputStream = method.getReturnType().isAssignableFrom(InputStream.class);

        // 是否返回流
        boolean isReturnStream = method.getReturnType().isAssignableFrom(Stream.class);

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

        if (httpHandler != null) {
            httpHandler.beforeHttpMethod(request, params);
        }

        HttpResponse response;

        //region 发送请求
        if (isReturnInputStream || isReturnStream) {
            response = request.executeAsync();
        } else {
            response = request.execute();
        }
        //endregion

        if (httpHandler != null) {
            httpHandler.afterHttpMethod(response);
        }

        //region 处理返回值
        if (isReturnInputStream) {
            return response.bodyStream();
        } else if (isReturnStream) {
            Function<HttpResponse, Long> streamLimiter = getStreamLimiter(huaMethod.streamLimit());
            long count = streamLimiter.apply(response);
            InputStreamSupplier supplier = new InputStreamSupplier(
                    ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0],
                    response.bodyStream()
            );
            return Stream.generate(supplier).limit(count);
        } else {
            String resultString = response.body();
            if (httpHandler != null) {
                resultString = httpHandler.preHandleResponse(resultString);
            }
            return JsonMan.fromJson(resultString, method.getGenericReturnType());
        }
        //endregion
    }
}
