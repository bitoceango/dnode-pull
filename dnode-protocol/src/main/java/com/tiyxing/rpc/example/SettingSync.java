package com.tiyxing.rpc.example;

import com.google.gson.JsonPrimitive;
import lombok.extern.slf4j.Slf4j;

/**
 * @author tiyxing
 * @date 2020-01-05
 * @since 1.0.0
 */
@Slf4j
public class SettingSync {
    public void syncRespond(JsonPrimitive setting){
       log.info("vehicle handle setting sync respond complete :{}",setting);
    }
}
