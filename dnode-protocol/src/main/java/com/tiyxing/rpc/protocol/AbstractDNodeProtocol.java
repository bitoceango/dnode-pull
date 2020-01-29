package com.tiyxing.rpc.protocol;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.tiyxing.rpc.dnode.DNode;
import com.tiyxing.rpc.dnode.LocalInstance;
import com.tiyxing.rpc.dnode.RemoteProxy;

/**
 * @author tiyxing
 * @date 2020-01-06
 * @since 1.0.0
 */
public class AbstractDNodeProtocol implements DNodeProtocol {

    protected RemoteProxy remoteProxy;

    protected LocalInstance localInstance;

    protected DNode dNode;


    public AbstractDNodeProtocol(Object instance,DNode dNode){
        localInstance=new LocalInstance(instance);
        this.dNode=dNode;
    }

    @Override
    public JsonObject getAllMethod() {
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("method","methods");
        JsonArray arguments=new JsonArray();
        arguments.add(localInstance.getSignature());
        jsonObject.add("arguments",arguments);
        jsonObject.add("callbacks",localInstance.getCallbacks());
        return jsonObject;

    }

    @Override
    public void request(Object method, String callbackName, JsonArray args) {

    }

    @Override
    public void handle(JsonObject dnodeRequest) {

    }

    protected JsonArray transform(Object[] args) {
        JsonArray result = new JsonArray();
        for (Object arg : args) {
            result.add(toJson(arg));
        }
        return result;
    }

    private JsonElement toJson(Object o) {
        JsonElement e;
        if (o instanceof String) {
            e = new JsonPrimitive((String) o);
        } else if (o instanceof Number) {
            e = new JsonPrimitive((Number) o);
        } else {
            throw new RuntimeException("Unsupported type: " + o.getClass());
        }
        return e;
    }
}
