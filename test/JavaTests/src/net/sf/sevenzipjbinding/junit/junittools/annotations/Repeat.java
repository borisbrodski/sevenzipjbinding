package net.sf.sevenzipjbinding.junit.junittools.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sf.sevenzipjbinding.junit.TestConfiguration;

/**
 * Mark the test to be repeated a number of times according to the {@link TestConfiguration}. (single- and
 * multi-threaded)
 *
 * @author Boris Brodski
 * @since 15.09-2.01
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface Repeat {
    /**
     * Multiply the number of repetitions configured in {@link TestConfiguration} by the given number.
     */
    int multiplyBy() default 1;
}
