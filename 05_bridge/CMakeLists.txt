cmake_minimum_required(VERSION 3.16)

project(sd-task-5)
set (CMAKE_CXX_STANDARD 20)

add_executable(main main.cpp canvas/SvgCanvas.cpp canvas/PpmCanvas.cpp canvas/CanvasFactory.cpp)
