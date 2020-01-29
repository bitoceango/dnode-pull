package com.tiyxing.rpc.protocol;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.tiyxing.rpc.dnode.Callback;
import com.tiyxing.rpc.dnode.DNode;
import com.tiyxing.rpc.dnode.RemoteProxy;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author tiyxing
 * @date 2020-01-03
 * @since 1.0.0
 */
@Slf4j
public class ServerDNodeProtocol  extends AbstractDNodeProtocol{


    public ServerDNodeProtocol(Object instance, DNode dNode){
        super(instance,dNode);
        this.dNode=dNode;
    }



    //todo 为了简化，此处只允许一个回调函数
    @Override
    public void handle(JsonObject dnodeRequest) {
        log.info("cloud receive :{}",dnodeRequest.toString());
        JsonPrimitive method = (JsonPrimitive) dnodeRequest.get("method");
        if (method.isString() && method.getAsString().equals("methods")){
            remoteProxy=new RemoteProxy(dnodeRequest);
            dNode.emit("ready");
        }else {
            JsonArray arguments = (JsonArray) dnodeRequest.get("arguments");
            JsonObject callbackElement = (JsonObject) dnodeRequest.get("callbacks");
            String methodId="";
            for (Map.Entry<String, JsonElement> cb:callbackElement.entrySet()){
                methodId=cb.getKey();
                break;
            }
            if (methodId.length()==0){
                throw new IllegalArgumentException("");
            }
            String finalMethodId = methodId;
            Callback callback= args -> {
                JsonObject jsonObject=new JsonObject();
                jsonObject.addProperty("method", finalMethodId);
                JsonArray jsonArray=transform(args);
                jsonObject.add("arguments",jsonArray);
                jsonObject.add("callbacks",new JsonObject());
                jsonObject.add("links",new JsonArray());
                log.info("cloud invoke :{}",jsonObject.toString());
                dNode.emit("invoke",jsonObject);
            };
            Object[] args=new Object[arguments.size()];
            for (int i=0;i<arguments.size();i++){
                args[i]=arguments.get(i);
            }
            args[0]=callback;
            localInstance.invoke(method.getAsInt(),args);
        }


    }
}
