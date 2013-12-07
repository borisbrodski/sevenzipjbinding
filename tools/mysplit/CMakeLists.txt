PROJECT(MySplit)

add_executable(mysplit mysplit.cpp)
set(CMAKE_EXE_LINKER_FLAGS "-static")
