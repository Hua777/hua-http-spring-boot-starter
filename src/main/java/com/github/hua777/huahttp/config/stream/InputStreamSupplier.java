package com.github.hua777.huahttp.config.stream;

import com.github.hua777.huahttp.bean.JsonMan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

public class InputStreamSupplier implements Supplier<Object> {

    static Logger log = LoggerFactory.getLogger(InputStreamSupplier.class);

    public InputStreamSupplier(
            long count,
            Type actualType,
            JsonMan jsonMan,
            InputStream inputStream
    ) {
        this.count = count;
        this.actualType = actualType;
        this.jsonMan = jsonMan;
        this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    }

    long count;
    Type actualType;
    JsonMan jsonMan;
    BufferedReader bufferedReader;

    long currentCount = 0;

    @Override
    public synchronized Object get() {
        if (bufferedReader == null) {
            throw new RuntimeException("读取早已经结束！");
        }
        String line;
        try {
            line = bufferedReader.readLine();
            if (line == null) {
                bufferedReader.close();
                bufferedReader = null;
            }
            currentCount += 1;
        } catch (Exception ex) {
            log.error("读取第 " + currentCount + " 条时出错！", ex);
            throw new RuntimeException(ex);
        }
        return jsonMan.fromJson(line, actualType);
    }
}
