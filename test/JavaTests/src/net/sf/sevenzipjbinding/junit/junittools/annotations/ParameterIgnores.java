package net.sf.sevenzipjbinding.junit.junittools.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Ignore tests according to parameter values.
 *
 * @author Boris Brodski
 * @since 15.09-2.01
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ParameterIgnores {

}
