package com.tiyxing.rpc.protocol;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.tiyxing.rpc.dnode.DNode;
import com.tiyxing.rpc.dnode.RemoteProxy;
import lombok.extern.slf4j.Slf4j;

/**
 * @author tiyxing
 * @date 2020-01-05
 * @since 1.0.0
 */
@Slf4j
public class ClientDNodeProtocol extends AbstractDNodeProtocol {


    public ClientDNodeProtocol (Object instance, DNode dNode){
       super(instance,dNode);
    }

    @Override
    public void request(Object method, String callbackName, JsonArray args) {
        int methodId;
        if (method instanceof String){
            methodId= remoteProxy.getMethodIdByName((String) method);
        }else {
            throw new IllegalArgumentException("");
        }
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("method",methodId);
        JsonArray argumentsArray=new JsonArray();
        argumentsArray.add(new JsonPrimitive("[Function]"));
        jsonObject.add("arguments",argumentsArray);
        for (JsonElement jsonElement:args){
            argumentsArray.add(jsonElement);
        }
        JsonObject callback=new JsonObject();
        JsonArray methodPath = new JsonArray();
        methodPath.add(new JsonPrimitive("0"));
        callback.add(localInstance.getMethodIndexByName(callbackName),methodPath);
        jsonObject.add("callbacks",callback);
        jsonObject.add("links",new JsonArray());
        log.info("vehicle call :{}",jsonObject.toString());
        dNode.emit("invoke",jsonObject);

    }

    @Override
    public void handle(JsonObject dnodeRequest) {
        log.info("vehicle receive :{}",dnodeRequest.toString());
        JsonPrimitive method = (JsonPrimitive) dnodeRequest.get("method");
        if (method.isString() && method.getAsString().equals("methods")){
            remoteProxy=new RemoteProxy(dnodeRequest);
            dNode.emit("ready");
        }else {
            JsonArray arguments = (JsonArray) dnodeRequest.get("arguments");
            Object[] args=new Object[arguments.size()];
            for (int i=0;i<arguments.size();i++){
                args[i]=arguments.get(i);
            }
            localInstance.invoke(method.getAsInt(),args);
        }

    }
}
