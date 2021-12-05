#pragma once
#include <string>

class ICanvas {
   public:
    virtual int GetWidth() const = 0;
    virtual int GetHeight() const = 0;
    virtual void DrawCircle(int x, int y, int radius) = 0;
    virtual void DrawArrow(int x1, int y1, int x2, int y2) = 0;
    virtual void DrawText(int x, int y, std::string text) = 0;

    virtual void Finalize() = 0;

    virtual ~ICanvas() {}
};