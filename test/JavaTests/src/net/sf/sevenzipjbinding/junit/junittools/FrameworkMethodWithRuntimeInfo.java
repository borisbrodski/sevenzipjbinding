package net.sf.sevenzipjbinding.junit.junittools;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.runners.model.FrameworkMethod;

public abstract class FrameworkMethodWithRuntimeInfo extends FrameworkMethod {

    public FrameworkMethodWithRuntimeInfo(Method method) {
        super(method);
    }

    protected abstract RuntimeInfoAnnotation getRuntimeInfoAnnotation();

    @Override
    public Annotation[] getAnnotations() {
        Annotation[] annotations = super.getAnnotations();
        List<Annotation> enhancedAnnotations = new ArrayList<Annotation>();
        enhancedAnnotations.addAll(Arrays.asList(annotations));
        enhancedAnnotations.add(getRuntimeInfoAnnotation());
        return enhancedAnnotations.toArray(new Annotation[enhancedAnnotations.size()]);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        if (annotationType == RuntimeInfoAnnotation.class) {
            return (T) getRuntimeInfoAnnotation();
        }
        return super.getAnnotation(annotationType);
    }
}
