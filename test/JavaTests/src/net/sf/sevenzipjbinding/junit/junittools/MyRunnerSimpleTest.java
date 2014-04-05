package net.sf.sevenzipjbinding.junit.junittools;

//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//
//import org.junit.After;
//import org.junit.AfterClass;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//public class MyRunnerSimpleTest extends MyRunnerTestBase {
//    static {
//        addClassToCheck(MyRunnerSimpleTest.class);
//    }
//
//    private static int beforeClassCallNumber;
//    private static int afterClassCallNumber;
//    private static int callNumberMinimum = 0;
//    private static int callNumberMaximum;
//
//    private int beforeCallNumber;
//    private int afterCallNumber;
//
//    private int testCallNumber;
//
//    @BeforeClass
//    public static void beforeClass() {
//        beforeClassCallNumber = nextNumber();
//        setTestCount(0);
//    }
//
//    @AfterClass
//    public static void afterClass() {
//        afterClassCallNumber = nextNumber();
//    }
//
//    @ClassCheckMethod
//    public static void classCheck() {
//        assertTrue(beforeClassCallNumber != 0);
//        assertTrue(beforeClassCallNumber < callNumberMinimum);
//        assertTrue(beforeClassCallNumber < afterClassCallNumber);
//        assertTrue(afterClassCallNumber > callNumberMaximum);
//        assertEquals(3, getTestCount());
//        baseClassCheck(callNumberMaximum);
//    }
//
//    private synchronized static int localNextNumber() {
//        int number = nextNumber();
//        if (callNumberMinimum == 0) {
//            callNumberMinimum = number;
//        }
//        callNumberMaximum = number;
//        return number;
//    }
//
//    @Before
//    public void before() {
//        beforeCallNumber = localNextNumber();
//    }
//
//    @After
//    public void after() {
//        afterCallNumber = localNextNumber();
//    }
//
//    @ClassCheckMethod
//    protected void instanceCheck() {
//        assertTrue(testCallNumber > 0);
//        checkBeforeAndAfter(testCallNumber);
//    }
//
//    @Override
//    protected void checkBeforeAndAfter(int testCallNumber) {
//        super.checkBeforeAndAfter(testCallNumber);
//
//        assertTrue(beforeCallNumber > 0);
//        assertTrue(beforeCallNumber < testCallNumber);
//        assertTrue(afterCallNumber > testCallNumber);
//    }
//
//    @Test
//    public void test1() {
//        assertEquals(0, testCallNumber);
//        testCallNumber = localNextNumber();
//        addInstanceToCheck(this);
//        countCalledTest();
//    }
//
//    @Test
//    public void test2() {
//        assertEquals(0, testCallNumber);
//        testCallNumber = localNextNumber();
//        countCalledTest();
//    }
//
//    @Test
//    public void test3() {
//        assertEquals(0, testCallNumber);
//        testCallNumber = localNextNumber();
//        countCalledTest();
//    }
//}
