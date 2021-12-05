#include <fstream>
#include <memory>
#include <sstream>
#include <string>
#include <vector>

#include "CanvasFactory.h"
#include "ICanvas.h"

class ISvgShape {
   public:
    virtual void AppendXml(std::ostream& s) = 0;

    virtual ~ISvgShape() {}
};

class SvgShapeCircle : public ISvgShape {
   public:
    SvgShapeCircle(int x, int y, int radius) : x_{x}, y_{y}, radius_{radius} {}

    void AppendXml(std::ostream& s) override {
        s << "<circle cx=\"" << x_ << "\" cy=\"" << y_ << "\" r=\"" << radius_
          << "\"/>\n";
    }

    ~SvgShapeCircle() {}

   private:
    int x_{}, y_{};
    int radius_{};
};

class SvgShapeArrow : public ISvgShape {
   public:
    SvgShapeArrow(int x1, int y1, int x2, int y2)
        : x1_{x1}, y1_{y1}, x2_{x2}, y2_{y2} {}

    void AppendXml(std::ostream& s) override {
        s << "<line x1=\"" << x1_ << "\" y1=\"" << y1_ << "\" x2=\"" << x2_
          << "\" y2=\"" << y2_
          << "\" stroke=\"#000\" "
             "stroke-width=\"2\" marker-end=\"url(#arrowhead)\" />";
    }

    ~SvgShapeArrow() {}

   private:
    int x1_{}, y1_{};
    int x2_{}, y2_{};
};

class SvgShapeText : public ISvgShape {
   public:
    SvgShapeText(int x, int y, std::string text) : x_{x}, y_{y}, text_{text} {}

    void AppendXml(std::ostream& s) override {
        s << "  <text x=\"" << x_ << "\" y=\"" << y_ << "\">"
          << EscapeXml(text_) << "</text>";
    }

    ~SvgShapeText() {}

   private:
    int x_{}, y_{};
    std::string text_;

    std::string EscapeXml(std::string s) {
        std::stringstream out;
        for (auto& c : s) {
            out << "&#" << (int)c << ";";
        }
        return out.str();
    }
};

class SvgCanvas : public ICanvas {
   public:
    SvgCanvas(int width, int height, std::string filename)
        : width_{width}, height_{height}, filename_{filename} {}

    int GetWidth() const override { return width_; };
    int GetHeight() const override { return height_; };
    void DrawCircle(int x, int y, int radius) override {
        shapes_.emplace_back(new SvgShapeCircle(x, y, radius));
    };
    void DrawArrow(int x1, int y1, int x2, int y2) override {
        shapes_.emplace_back(new SvgShapeArrow(x1, y1, x2, y2));
    };

    void DrawText(int x, int y, std::string text) override {
        shapes_.emplace_back(new SvgShapeText(x, y, text));
    }

    void Finalize() override { WriteSvg(); }

    ~SvgCanvas() {}

   private:
    int width_{}, height_{};
    std::vector<std::unique_ptr<ISvgShape>> shapes_;
    std::string filename_;

   private:
    void AppendHeader(std::ostream& s) {
        s << R"--(<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 )--"
          << width_ << " " << height_ << R"--("> <defs>)--"
          << R"--(<marker id="arrowhead" markerWidth="10" markerHeight="7" refX="5" refY="3.5" orient="auto">)--"
          << R"--(<polygon points="0 0, 5 3.5, 0 7" />)--"
          << "</marker>"
          << "</defs>";
    }

    void AppendFooter(std::ostream& s) { s << "</svg>"; }

    void WriteSvg() {
        std::ofstream ret{filename_};

        AppendHeader(ret);

        for (auto& shape : shapes_) {
            shape->AppendXml(ret);
        }

        AppendFooter(ret);
    }
};

namespace {
struct KeeperSvg {
    KeeperSvg() {
        CanvasFactory::RegisterFactory(
            "svg", [](int width, int height, std::string filename) {
                return std::make_shared<SvgCanvas>(width, height, filename);
            });
    }
} __kpr__;
}  // namespace