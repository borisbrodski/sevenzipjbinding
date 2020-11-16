package net.sf.sevenzipjbinding.junit.junittools.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark test a as 'long running'. Used for stress tests.
 *
 * @author Boris Brodski
 * @since 9.20-2.00
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface LongRunning {

}
