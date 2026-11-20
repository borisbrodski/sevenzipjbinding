package net.sf.sevenzipjbinding.springboottest;

public class CallbackSpiImpl implements CallbackSpi {
    public static final String NAME = "spring-boot-test";

    @Override
    public String name() {
        return NAME;
    }
}
