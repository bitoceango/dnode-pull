package com.tiyxing.rpc.example;

import com.google.gson.JsonPrimitive;
import com.tiyxing.rpc.dnode.Callback;
import lombok.extern.slf4j.Slf4j;

/**
 * @author tiyxing
 * @date 2020-01-05
 * @since 1.0.0
 */
@Slf4j
public class SettingService {
    public void handleSettingSync(Callback callback, JsonPrimitive setting){
         String settingResponse="{\n" +
                "    \"voice\":60,\n" +
                "\"soc\":\"100\"\n" +
                "}";
        callback.call(settingResponse);
        log.info("cloud handle setting complete");

    }
}
