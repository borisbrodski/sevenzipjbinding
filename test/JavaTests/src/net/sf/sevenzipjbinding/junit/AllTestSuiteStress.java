package net.sf.sevenzipjbinding.junit;

import static net.sf.sevenzipjbinding.junit.TestConfiguration.PR_STRESS;
import static net.sf.sevenzipjbinding.junit.TestConfiguration.TEST_PARAM__PROFILE;

import junit.framework.Test;

public class AllTestSuiteStress {
    public static Test suite() throws Exception {
        AllTestSuite.setSystemPropretyIfNotAlready(TEST_PARAM__PROFILE, PR_STRESS);
        return AllTestSuite.suite();
    }
}
