package net.sf.sevenzipjbinding.junit.jbindingtools;

import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;

public class JBindingToolsTestBase extends JUnitNativeTestBase {
    protected static final int TEST_REPEAT_COUNT = 100;
    protected static final int THREAD_COUNT = 40;
    protected static final int THREAD_TIMEOUT = 200 * TEST_REPEAT_COUNT;

}
