add_library(lru_cache INTERFACE)
target_include_directories(lru_cache INTERFACE inc/)

find_package(Catch2 REQUIRED)
add_executable(tests test/tests.cpp)
target_link_libraries(tests lru_cache Catch2::Catch2)
include(CTest)
include(Catch)
catch_discover_tests(tests)
