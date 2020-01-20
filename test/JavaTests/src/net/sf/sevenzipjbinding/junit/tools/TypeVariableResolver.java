package net.sf.sevenzipjbinding.junit.tools;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

// TODO Add support for interfaces
public final class TypeVariableResolver {
    public static class TypeVariableResolverException extends Exception {
        private static final long serialVersionUID = 42L;

        public TypeVariableResolverException() {
            super();
        }

        public TypeVariableResolverException(String message, Throwable cause) {
            super(message, cause);
        }

        public TypeVariableResolverException(String message) {
            super(message);
        }

        public TypeVariableResolverException(Throwable cause) {
            super(cause);
        }
    }

    private TypeVariableResolver() {
    }

    public static Class<?> resolveTypeVariable(Class<?> concreteClass, Class<?> definingClass, String typeVariableName)
            throws TypeVariableResolverException {
        if (concreteClass == definingClass || !definingClass.isAssignableFrom(concreteClass)) {
            throw new TypeVariableResolverException("The concreteClass (" + concreteClass.getName()
                    + ") should extend the definingClass (" + definingClass.getName() + ")");
        }
        List<Type> superTypes = new ArrayList<Type>();
        Class<?> clazz = concreteClass;
        while (clazz != null && clazz != Object.class && clazz != definingClass) {
            superTypes.add(clazz.getGenericSuperclass());
            clazz = clazz.getSuperclass();
        }
        if (clazz != definingClass) {
            throw new TypeVariableResolverException(
                    "Class " + concreteClass.getName() + " doesn't extend the class " + definingClass.getName());
        }

        String currentTypeVariableName = typeVariableName;
        superTypeLoop:
        for (int i = superTypes.size() - 1; i != -1; i--) {
            Class<?> rawType;
            if (superTypes.get(i) instanceof ParameterizedType) {
                ParameterizedType type = (ParameterizedType) superTypes.get(i);

                rawType = (Class<?>) type.getRawType();
                TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
                for (int j = 0; j < typeParameters.length; j++) {
                    if (typeParameters[j].getName().equals(currentTypeVariableName)) {
                        Type currentTypeVariable = type.getActualTypeArguments()[j];
                        if (currentTypeVariable instanceof Class) {
                            return (Class<?>) currentTypeVariable;
                        }
                        currentTypeVariableName = ((TypeVariable<?>) currentTypeVariable).getName();
                        continue superTypeLoop;
                    }
                }
            } else {
                rawType = (Class<?>) superTypes.get(i);
            }
            throw new TypeVariableResolverException("Type variable '" + currentTypeVariableName
                    + "' is not defined in the " + rawType.getName() + " class");
        }
        throw new TypeVariableResolverException("Can't trace the type variable to the concrete type");
    }

}
