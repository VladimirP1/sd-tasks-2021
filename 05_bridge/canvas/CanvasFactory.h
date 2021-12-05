#pragma once

#include <functional>
#include <memory>
#include <string>
#include <map>

#include "ICanvas.h"

class CanvasFactory {
   public:
    std::shared_ptr<ICanvas> GetCanvas(std::string ext, int width, int height,
                                       std::string filename);

    static void RegisterFactory(
        std::string ext,
        std::function<std::shared_ptr<ICanvas>(int, int, std::string)>);
    
    static void EnsureStatic();
   private:
    
    static std::map<std::string, std::function<std::shared_ptr<ICanvas>(int, int, std::string)>>* factories_;
};