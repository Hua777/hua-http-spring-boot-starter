package com.hua.sss.huahttp.config;

import com.google.gson.Gson;
import com.hua.sss.huahttp.annotation.HuaHttp;
import com.hua.sss.huahttp.annotation.method.HuaDelete;
import com.hua.sss.huahttp.annotation.method.HuaGet;
import com.hua.sss.huahttp.annotation.method.HuaPost;
import com.hua.sss.huahttp.annotation.method.HuaPut;
import com.hua.sss.huahttp.annotation.param.HuaBody;
import com.hua.sss.huahttp.annotation.param.HuaHeader;
import com.hua.sss.huahttp.annotation.param.HuaParam;
import com.hua.sss.huahttp.annotation.param.HuaPath;
import com.hua.sss.huahttp.tool.HttpTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.security.InvalidParameterException;
import java.util.HashMap;

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
        HuaHttp huaHttp = interfaceClass.getAnnotation(HuaHttp.class);

        String baseUrl = getValue(huaHttp.value());

        //region 处理 Headers

        HuaHeader methodHeader = method.getAnnotation(HuaHeader.class);

        HashMap<String, String> headers = new HashMap<>();

        if (methodHeader != null) {
            String[] headerNames = methodHeader.names();
            String[] headerValues = methodHeader.values();

            if (headerNames.length != headerValues.length) {
                throw new InvalidParameterException("Header names 与 values 长度不匹配。");
            }

            for (int i = 0; i < headerNames.length; ++i) {
                headers.put(getValue(headerNames[i]), getValue(headerValues[i]));
            }
        }

        //endregion

        HuaGet huaGet = method.getAnnotation(HuaGet.class);
        HuaPost huaPost = method.getAnnotation(HuaPost.class);
        HuaPut huaPut = method.getAnnotation(HuaPut.class);
        HuaDelete huaDelete = method.getAnnotation(HuaDelete.class);

        //region 处理地址与请求名

        String subUrl = "";

        String methodName = "";

        if (huaGet != null) {
            methodName = "GET";
            subUrl = getValue(huaGet.value());
        } else if (huaPost != null) {
            methodName = "POST";
            subUrl = getValue(huaPost.value());
        } else if (huaPut != null) {
            methodName = "PUT";
            subUrl = getValue(huaPut.value());
        } else if (huaDelete != null) {
            methodName = "DELETE";
            subUrl = getValue(huaDelete.value());
        }

        String fullUrl = baseUrl + subUrl;

        if (fullUrl.equals("")) {
            throw new InvalidParameterException("请求地址为空！");
        }

        //endregion

        //region 处理参数

        HashMap<String, String> params = new HashMap<>();
        HashMap<String, Object> bodies = new HashMap<>();

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
                //region 处理地址路径转换
                fullUrl = fullUrl.replaceAll("\\{" + paramName + "}", arg.toString());
                //endregion
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

        String result = HttpTool.reqJson(fullUrl, methodName, params, bodies, headers);
        Class<?> returnType = method.getReturnType();
        switch (returnType.getTypeName()) {
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

        Gson gson = new Gson();
        return gson.fromJson(result, returnType);
    }
}
