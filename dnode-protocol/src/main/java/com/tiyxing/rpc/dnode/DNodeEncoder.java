package com.tiyxing.rpc.dnode;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zman.pull.stream.ISink;
import com.zman.pull.stream.bean.ReadResult;
import com.zman.pull.stream.bean.ReadResultEnum;
import com.zman.pull.stream.impl.DefaultThrough;

import java.nio.charset.StandardCharsets;

/**
 * @author tiyxing
 * @date 2020-01-05
 * @since 1.0.0
 */
public class DNodeEncoder extends DefaultThrough<JsonObject, byte[]> {


    private Gson gson = new Gson();

    public DNodeEncoder(){

    }

    @Override
    public ReadResult get(boolean end, Throwable throwable, ISink sink) {
        ReadResult readResult = super.get(end, throwable, sink);
        if(ReadResultEnum.Available.equals(readResult.status)) {
            JsonObject data = (JsonObject) readResult.data;
            readResult.data=data.toString().getBytes(StandardCharsets.UTF_8);
        }
        return readResult;
    }
}
