package net.sf.sevenzipjbinding.junit;

import static net.sf.sevenzipjbinding.junit.TestConfiguration.PR_MINUMAL;
import static net.sf.sevenzipjbinding.junit.TestConfiguration.TEST_PARAM__PROFILE;

import junit.framework.Test;

public class AllTestSuiteMinimal {
    public static Test suite() throws Exception {
        AllTestSuite.setSystemPropretyIfNotAlready(TEST_PARAM__PROFILE, PR_MINUMAL);
        return AllTestSuite.suite();
    }
}
