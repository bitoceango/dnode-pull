package com.tiyxing.rpc.dnode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author tiyxing
 * @date 2020-01-05
 * @since 1.0.0
 */
@AllArgsConstructor
@Setter
@Getter
public class CallbackModel {
    private int methodId;
    private int index;
    private String methodName;
}
