package com.github.hua777.huahttp.config.limiter;

import com.github.hua777.huahttp.bean.JsonMan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class InputStreamSupplier implements Supplier<Object> {

    static Logger log = LoggerFactory.getLogger(InputStreamSupplier.class);

    public InputStreamSupplier(
            Type actualType,
            InputStream inputStream
    ) {
        this.actualType = actualType;

        new Thread(() -> {
            BufferedReader bufferedReader = null;
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    dataQueue.offer(line);
                }
            } catch (Exception ex) {
                log.error("流读取错误！", ex);
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (Exception ex) {
                        log.error("无法关闭流！", ex);
                    }
                }
            }
        }).start();

    }

    Type actualType;

    Queue<String> dataQueue = new ConcurrentLinkedQueue<>();

    @Override
    public synchronized Object get() {
        while (dataQueue.size() <= 0) {
            try {
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (Exception ignored) {

            }
        }
        String line = dataQueue.poll();
        return JsonMan.fromJson(line, actualType);
    }
}
