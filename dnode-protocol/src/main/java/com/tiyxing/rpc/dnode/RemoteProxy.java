package com.tiyxing.rpc.dnode;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author tiyxing
 * @date 2020-01-03
 * @since 1.0.0
 */
@Slf4j
public class RemoteProxy {
    private JsonObject jsonObject;
    private Map<String,String> methods;
    private Map<String, CallbackModel> callbackMap;

    public RemoteProxy(JsonObject jsonObject){
        this.jsonObject=jsonObject;
        methods=new ConcurrentHashMap<>();
        callbackMap=new ConcurrentHashMap<>();
        parseMethod(jsonObject);
        parseCallback(jsonObject);

    }

    private void parseCallback(JsonObject jsonObject) {
        JsonArray arguments = (JsonArray) jsonObject.get("arguments");
        JsonObject jsonElement = (JsonObject) arguments.get(0);
        for (Map.Entry<String, JsonElement> arg:jsonElement.entrySet()){
            methods.put(arg.getKey(),arg.getValue().getAsString());
        }

    }

    private void parseMethod(JsonObject jsonObject) {
        JsonObject callbacks = (JsonObject) jsonObject.get("callbacks");
        for (Map.Entry<String, JsonElement> cb:callbacks.entrySet()){
            int methodId = Integer.valueOf(cb.getKey());
            JsonArray method= (JsonArray) cb.getValue();
            int methodIndex=method.get(0).getAsInt();
            String methodName = method.get(1).getAsString();
            CallbackModel callbackModel=new CallbackModel(methodId,methodIndex,methodName);
            callbackMap.put(methodName,callbackModel);

        }

    }

    public int getMethodIdByName(String methodName){
        return callbackMap.get(methodName).getMethodId();
    }




}
