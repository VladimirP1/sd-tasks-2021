cmake_minimum_required(VERSION 3.16)

project(sd-task-1)
set (CMAKE_CXX_STANDARD 20)


add_subdirectory(lru_cache)

add_executable(main main.cpp)
target_link_libraries(main lru_cache)


enable_testing()