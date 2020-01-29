package com.tiyxing.rpc.protocol;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * @author tiyxing
 * @date 2020-01-03
 * @since 1.0.0
 */
public interface DNodeProtocol {

    JsonObject getAllMethod();

    void request(Object method, String callbackName, JsonArray args);

    void handle(JsonObject dnodeRequest);
}
