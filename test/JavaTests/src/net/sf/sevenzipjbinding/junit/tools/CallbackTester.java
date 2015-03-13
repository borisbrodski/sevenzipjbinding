package net.sf.sevenzipjbinding.junit.tools;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

// TODO Test more callbacks (extraction + compression)
public class CallbackTester<E> implements InvocationHandler {
    private Set<String> methodNameSet = new HashSet<String>();
    private E proxyInstance;
    private E instance;

    @SuppressWarnings("unchecked")
    public CallbackTester(E instance) {
        this.instance = instance;

        this.proxyInstance = (E) Proxy.newProxyInstance(instance.getClass().getClassLoader(), instance.getClass()
                .getInterfaces(), this);
    }

    public int getDifferentMethodsCalled() {
        return methodNameSet.size();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<String> list = new ArrayList<String>(methodNameSet);
        Collections.sort(list);
        for (String methodName : list) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(", ");
            }

            stringBuilder.append(methodName);
            stringBuilder.append("()");
        }
        return "Called methods: " + stringBuilder;
    }

    public E getProxyInstance() {
        return proxyInstance;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (methodNameSet.add(method.getName())) {
            // System.out.println(method);
        }
        try {
            return method.invoke(instance, args);
        } catch (InvocationTargetException exception) {
            throw exception.getCause();
        }
    }

    public E getOriginalCallback() {
        return instance;
    }
}
