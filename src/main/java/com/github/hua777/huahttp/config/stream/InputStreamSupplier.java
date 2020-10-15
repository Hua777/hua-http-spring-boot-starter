package com.github.hua777.huahttp.config.stream;

import com.github.hua777.huahttp.annotation.method.HuaStream;
import com.github.hua777.huahttp.bean.JsonMan;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.function.Supplier;

public class InputStreamSupplier implements Supplier<Object> {

    public InputStreamSupplier(
            HuaStream huaStream,
            Type actualType,
            JsonMan jsonMan,
            InputStream inputStream
    ) {
        if (huaStream != null) {
            this.bufferSize = huaStream.bufferSize();
            this.endCharacter = huaStream.endCharacter();
        }
        this.actualType = actualType;
        this.jsonMan = jsonMan;
        this.inputStream = new BufferedInputStream(inputStream);
        data = new byte[this.bufferSize];
    }

    int bufferSize = 1024;
    String endCharacter = "\n";
    Type actualType;
    JsonMan jsonMan;
    BufferedInputStream inputStream;

    byte[] data;

    StringBuilder prevStringBuilder = new StringBuilder();

    @Override
    public synchronized Object get() {
        try {
            if (inputStream == null) {
                return null;
            }
            Object result = checkPrevStringBuilder();
            if (result != null) {
                return result;
            }
            while (true) {
                int realLength = inputStream.read(data);
                if (realLength == -1) {
                    inputStream.close();
                    inputStream = null;
                    break;
                }
                prevStringBuilder.append(new String(data, 0, realLength));
                result = checkPrevStringBuilder();
                if (result != null) {
                    return result;
                }
            }
            return jsonMan.fromJson(prevStringBuilder.toString(), actualType);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private Object checkPrevStringBuilder() {
        String prevString = prevStringBuilder.toString();
        if (prevString.contains(endCharacter)) {
            int index = prevString.indexOf(endCharacter);
            String result = prevString.substring(0, index);
            prevStringBuilder.setLength(0);
            prevStringBuilder.append(prevString.substring(index + endCharacter.length()));
            return jsonMan.fromJson(result, actualType);
        }
        return null;
    }
}
