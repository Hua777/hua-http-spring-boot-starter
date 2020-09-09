package com.github.hua777.huahttp.aware;

import com.github.hua777.huahttp.property.HttpHandlerConfig;
import org.springframework.beans.factory.Aware;

public interface HuaHttpHandlerConfigAware extends Aware {

    void setHttpHandlerConfig(HttpHandlerConfig httpHandlerConfig);

}
