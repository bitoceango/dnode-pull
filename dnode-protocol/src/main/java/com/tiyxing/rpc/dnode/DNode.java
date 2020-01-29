package com.tiyxing.rpc.dnode;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tiyxing.rpc.protocol.ClientDNodeProtocol;
import com.tiyxing.rpc.protocol.DNodeProtocol;
import com.tiyxing.rpc.protocol.ServerDNodeProtocol;
import com.zman.event.EventEmitter;
import com.zman.pull.stream.IDuplex;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

/**
 * @author tiyxing
 * @date 2020-01-05
 * @since 1.0.0
 */
@Slf4j
public class DNode extends EventEmitter {

    private IDuplex duplex;

    private DNodeProtocol dNodeProtocol;

    public DNode(DNodeType dNodeType, Object instance) {
        switch (dNodeType) {
            case CLINET:
                dNodeProtocol = new ClientDNodeProtocol(instance,this);
                break;
            case SERVER:
                dNodeProtocol = new ServerDNodeProtocol(instance,this);
                break;
            default:
                log.error("impossible");

        }
        on("invoke", (Consumer<JsonObject>) jsonObject -> {
            duplex.push(jsonObject);
        });


    }

    public JsonObject startDNode(){
        return dNodeProtocol.getAllMethod();
    }

    public void invoke(String methodName,String callbackName,JsonArray arg){
        dNodeProtocol.request(methodName,callbackName,arg);
    }

    public void handleMessage(JsonObject data){
       dNodeProtocol.handle(data);
    }

    public IDuplex createDNodeStream() {
        DNodeStream dNodeStream = new DNodeStream(this);
        duplex=dNodeStream.getDuplex();
        return duplex;
    }


}
