#include "CanvasFactory.h"

std::shared_ptr<ICanvas> CanvasFactory::GetCanvas(std::string ext, int width,
                                                  int height,
                                                  std::string filename) {
    EnsureStatic();
    if (!factories_->count(ext)) return {};
    return factories_->at(ext)(width, height, filename);
}

void CanvasFactory::RegisterFactory(
    std::string ext,
    std::function<std::shared_ptr<ICanvas>(int, int, std::string)> f) {
    EnsureStatic();
    factories_->insert(std::make_pair(ext, f));
}

void CanvasFactory::EnsureStatic() {
    if (!factories_) {
        factories_ =
            new std::map<std::string, std::function<std::shared_ptr<ICanvas>(
                                          int, int, std::string)>>();
    }
}

std::map<std::string,
         std::function<std::shared_ptr<ICanvas>(int, int, std::string)>>*
    CanvasFactory::factories_;
