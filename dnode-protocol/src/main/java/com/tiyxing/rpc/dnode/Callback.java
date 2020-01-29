package com.tiyxing.rpc.dnode;

public interface Callback {
    void call(Object... args) throws RuntimeException;
}
