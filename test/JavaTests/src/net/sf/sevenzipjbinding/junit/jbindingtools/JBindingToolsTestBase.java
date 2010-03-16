package net.sf.sevenzipjbinding.junit.jbindingtools;

import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;

public class JBindingToolsTestBase extends JUnitNativeTestBase {
    protected static final int TEST_REPEAT_COUNT = 30;
    protected static final int THREAD_COUNT = 10;
    protected static final int THREAD_TIMEOUT = 20000 * TEST_REPEAT_COUNT;

}
