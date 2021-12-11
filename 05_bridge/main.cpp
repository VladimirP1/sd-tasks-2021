#include "canvas/CanvasFactory.h"
#include "canvas/ICanvas.h"
#include "graph/AbstractGraph.h"

int main(int argc, char** argv) {
    if (argc != 5) {
        return 1;
    }

    CanvasFactory f;

    const auto arg_graph_type = argv[1];
    const auto arg_canvas_type = argv[2];
    const auto arg_input_file = argv[3];
    const auto arg_output_file = argv[4];

    auto canvas = f.GetCanvas(arg_canvas_type, 1024, 1024, arg_output_file);
    auto graph = std::shared_ptr<AbstractGraph>(
        std::atoi(arg_graph_type)
            ? static_cast<AbstractGraph*>(new EdgeListGraph(canvas))
            : static_cast<AbstractGraph*>(new AdjMatrixGraph(canvas)));
    
    graph->Load(arg_input_file);

    graph->Draw();

    canvas->Finalize();

    return 0;

    // EdgeListGraph g(canvas);
    // g.Load("in.txt");
    // g.Draw();
    // canvas->Finalize();
}