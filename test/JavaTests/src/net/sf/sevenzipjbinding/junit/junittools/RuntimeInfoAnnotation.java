package net.sf.sevenzipjbinding.junit.junittools;

import java.lang.annotation.Annotation;

public class RuntimeInfoAnnotation implements Annotation {
    private boolean doRunMultithreaded;

    public RuntimeInfoAnnotation(boolean doRunMultithreaded) {
        this.doRunMultithreaded = doRunMultithreaded;
    }

    public Class<? extends Annotation> annotationType() {
        return RuntimeInfoAnnotation.class;
    }

    public boolean isDoRunMultithreaded() {
        return doRunMultithreaded;
    }
}
