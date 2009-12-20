## This file should be placed in the root directory of your project.
## Then modify the CMakeLists.txt file in the root directory of your
## project to incorporate the testing dashboard.
## # The following are required to uses Dart and the Cdash dashboard
##   ENABLE_TESTING()
##   INCLUDE(Dart)
set(CTEST_PROJECT_NAME "7-Zip-JBinding")
#set(CTEST_NIGHTLY_START_TIME "20:00:00 CET")

set(CTEST_DROP_METHOD "http")
set(CTEST_DROP_SITE "my.cdash.org")
set(CTEST_DROP_LOCATION "/submit.php?project=7-Zip-JBinding")
set(CTEST_DROP_SITE_CDASH TRUE)

set(CTEST_TEST_TIMEOUT 20000)
#set(DART_TESTING_TIMEOUT 7200)


set(CTEST_CUSTOM_MAXIMUM_NUMBER_OF_WARNINGS 10000)
#set(CTEST_CUSTOM_WARNING_EXCEPTION "kdecore/network/k3socket[a-z]+\\.h"
#                                   "kdecore/network/k3clientsocketbase\\.h" )