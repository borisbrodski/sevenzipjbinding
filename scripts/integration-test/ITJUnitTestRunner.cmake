EXECUTE_PROCESS(COMMAND
                    java -cp "lib/junit-4.6.jar:sevenzipjbinding-tests.jar:lib/sevenzipjbinding.jar:lib/sevenzipjbinding-Platform.jar"
                    "-DSINGLEBUNDLE=${SINGLEBUNDLE}"
		    org.junit.runner.JUnitCore net.sf.sevenzipjbinding.junit.AllTestSuite
                                            WORKING_DIRECTORY .
                                            RESULT_VARIABLE RESULT)
                                     IF(RESULT)
                                         MESSAGE(SEND_ERROR "Error during JUnit Tests")
                                     ENDIF(RESULT)
