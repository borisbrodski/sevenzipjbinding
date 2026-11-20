package net.sf.sevenzipjbinding.springboottest;

import org.springframework.stereotype.Component;

/**
 * A Spring bean called from inside 7-Zip-JBinding callbacks.
 */
@Component
public class CallbackGreeter {
    public String greet(String from) {
        return "hello from " + from;
    }
}
