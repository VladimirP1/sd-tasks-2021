#include "canvas/CanvasFactory.h"
#include "graph/AbstractGraph.h"

int main() {
    CanvasFactory f;
    auto canvas = f.GetCanvas("svg", 1024, 1024, "out.svg");
    // canvas->DrawCircle(100, 100, 10);
    // canvas->DrawArrow(0, 0, 90, 90);
    // canvas->DrawText(0, 20, "1est text");
    // canvas->Finalize();

    EdgeListGraph g(canvas);
    g.Load("in.txt");
    g.Draw();
    canvas->Finalize();
}