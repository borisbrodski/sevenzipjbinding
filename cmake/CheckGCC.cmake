IF(NOT GCC_ARCH)
    MESSAGE("-- Checking gcc architecture")
    SET(GCC_TEST_DIR "${PROJECT_BINARY_DIR}/gcc-test")
    FILE(MAKE_DIRECTORY "${GCC_TEST_DIR}")
    FILE(WRITE "${GCC_TEST_DIR}/test64.c" "int a[sizeof(void*)-7];\n")
    FILE(WRITE "${GCC_TEST_DIR}/test32.c" "int a[sizeof(void*)-3];\n")
    EXECUTE_PROCESS(COMMAND ${CMAKE_C_COMPILER} -c test64.c 
                    WORKING_DIRECTORY ${GCC_TEST_DIR}
                    RESULT_VARIABLE gcc_test64_result
                    OUTPUT_VARIABLE gcc_test64_output 
                    ERROR_VARIABLE gcc_test64_err)
    EXECUTE_PROCESS(COMMAND ${CMAKE_C_COMPILER} -c test32.c
                    WORKING_DIRECTORY ${GCC_TEST_DIR}
                    RESULT_VARIABLE gcc_test32_result
                    OUTPUT_VARIABLE gcc_test32_output 
                    ERROR_VARIABLE gcc_test32_err)
    MESSAGE("${gcc_test64_result}")
    MESSAGE("${gcc_test32_result}")
    IF(gcc_test64_result)
        IF(gcc_test32_result)
            MESSAGE(FATAL_ERROR "${CMAKE_C_COMPILER} can't compile simple c-programs.
Test64 output: ${gcc_test64_err}
Test32 output: ${gcc_test32_err}")
        ELSE()
            SET(GCC_ARCH "32" CACHE INTERNAL "Javac test passed")
        ENDIF()
    ELSE()
        SET(GCC_ARCH "64" CACHE INTERNAL "Javac test passed")
    ENDIF()
    MESSAGE("-- Checking gcc architecture - ${GCC_ARCH} bit")
ENDIF()

