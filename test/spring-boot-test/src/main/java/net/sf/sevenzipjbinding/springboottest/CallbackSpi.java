package net.sf.sevenzipjbinding.springboottest;

/**
 * A service looked up with {@link java.util.ServiceLoader} from inside 7-Zip-JBinding callbacks. ServiceLoader uses
 * the context class loader of the current thread, as do logging frameworks, JNDI and many others.
 */
public interface CallbackSpi {
    String name();
}
