package com.tiyxing.rpc.dnode;

import com.google.gson.JsonObject;
import com.zman.pull.stream.IDuplex;
import com.zman.pull.stream.impl.DefaultDuplex;
import lombok.extern.slf4j.Slf4j;

/**
 * @author tiyxing
 * @date 2020-01-05
 * @since 1.0.0
 */
@Slf4j
public class DNodeStream {
    private DNode dNode;
    private IDuplex duplex;

    private boolean onData(Object data){
        if (data instanceof JsonObject){
            dNode.handleMessage((JsonObject) data);
        }else {
            log.error("error");
        }
        return true;

    }



    public DNodeStream(DNode dNode) {
        this.dNode = dNode;

        duplex = new DefaultDuplex(this::onData, this::onClose);

        duplex.push(dNode.startDNode());
    }

    private void onClose(Object o) {
        log.info("stream close");

    }

    public IDuplex getDuplex() {
        return duplex;
    }
}
