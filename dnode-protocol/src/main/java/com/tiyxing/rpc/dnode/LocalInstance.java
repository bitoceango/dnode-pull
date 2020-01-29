package com.tiyxing.rpc.dnode;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * @author tiyxing
 * @date 2020-01-05
 * @since 1.0.0
 */
@Slf4j
public class LocalInstance {

    private final Object instance;

    public LocalInstance(Object instance) {
        this.instance = instance;
    }

    public JsonElement getSignature() {
        Class<?> klass = this.instance.getClass();
        JsonObject signature = new JsonObject();
        for (Method m : klass.getDeclaredMethods()) {
            signature.addProperty(m.getName(), "[Function]");
        }
        return signature;
    }

    public JsonObject getCallbacks() {
        Class<?> klass = this.instance.getClass();
        JsonObject callbacks = new JsonObject();
        int index = 0;
        for (Method m : klass.getDeclaredMethods()) {
            Class<?>[] parameterTypes = m.getParameterTypes();
            for (Class<?> parameterType : parameterTypes) {
                if (Callback.class.isAssignableFrom(parameterType)) {
                    JsonArray path = new JsonArray();
                    path.add(new JsonPrimitive("0"));
                    path.add(new JsonPrimitive(m.getName()));
                    callbacks.add(String.valueOf(index++), path);
                }
            }
        }
        return callbacks;
    }

    public String getMethodIndexByName(String methodName){
        Class<?> klass = this.instance.getClass();
        Method[] declaredMethods = klass.getDeclaredMethods();
        for (int i=0;i<declaredMethods.length;i++){
            Method method=declaredMethods[i];
            String name=method.getName();
            if (name.equals(methodName)){
                return String.valueOf(i);
            }
        }
        return null;
    }

    public void invoke(int methodIndex, Object[] args){
        try {
            Method[] declaredMethods = instance.getClass().getDeclaredMethods();
            Method invokeMethod = declaredMethods[methodIndex];
            invokeMethod.invoke(instance,args);
        } catch (Exception e) {
            log.error("invoke error",e);
        }
    }


}
