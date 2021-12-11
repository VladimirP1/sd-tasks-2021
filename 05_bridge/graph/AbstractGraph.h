#pragma once

#include <cmath>
#include <fstream>
#include <iostream>
#include <memory>
#include <sstream>
#include <stdexcept>
#include <string>
#include <vector>

#include "../canvas/ICanvas.h"

class AbstractGraph {
   public:
    AbstractGraph(std::shared_ptr<ICanvas> canvas) : canvas_{canvas} {}

    virtual void Load(std::string filename) = 0;

    virtual void Draw() = 0;

    virtual ~AbstractGraph() {}

   protected:
    std::shared_ptr<ICanvas> canvas_;
};

class EdgeListGraph : public AbstractGraph {
   public:
    EdgeListGraph(std::shared_ptr<ICanvas> canvas) : AbstractGraph{canvas} {}

    void Load(std::string filename) override {
        std::ifstream in{filename};
        if (!in) {
            throw std::runtime_error("Cannot open graph");
        }
        while (true) {
            std::string line;
            std::getline(in, line);
            if (in.eof()) break;
            std::stringstream ss{line};
            std::vector<int> adj;
            graph_.push_back({});
            while (true) {
                int to;
                ss >> to;
                graph_.back().push_back(to);
                if (ss.eof()) break;
                if (ss.fail()) {
                    throw std::runtime_error("Cannot load graph");
                }
            }
        }
    }

    void Draw() override {
        const int radius =
            std::min(canvas_->GetWidth(), canvas_->GetHeight()) * 3 / 8;
        const int cx = canvas_->GetWidth() / 2, cy = canvas_->GetHeight() / 2;

        int vert_radius = 2 * M_PI * radius / graph_.size() * 3 / 8;

        std::cout << graph_.size() << std::endl;
        std::vector<std::pair<int, int>> vertex_coords;
        for (int i = 0; i < graph_.size(); ++i) {
            double phi = i * 2 * M_PI / graph_.size();

            int x = cos(phi) * radius + cx;
            int y = sin(phi) * radius + cy;

            vertex_coords.emplace_back(x, y);
            canvas_->DrawCircle(x, y, vert_radius);
            canvas_->DrawText(x + vert_radius, y, std::to_string(i));
        }

        for (int i = 0; i < graph_.size(); ++i) {
            for (const auto& j : graph_[i]) {
                std::cout << i << " " << j << std::endl;
                if (j < graph_.size()) {
                    double dx = vertex_coords[j].first - vertex_coords[i].first;
                    double dy =
                        vertex_coords[j].second - vertex_coords[i].second;
                    double length = std::sqrt(dx * dx + dy * dy);
                    dx /= length / vert_radius;
                    dy /= length / vert_radius;

                    canvas_->DrawArrow(vertex_coords[i].first + dx,
                                       vertex_coords[i].second + dy,
                                       vertex_coords[j].first - dx,
                                       vertex_coords[j].second - dy);
                }
            }
        }
    }

   private:
    std::vector<std::vector<int>> graph_;
};

class AdjMatrixGraph : public AbstractGraph {
   public:
    AdjMatrixGraph(std::shared_ptr<ICanvas> canvas) : AbstractGraph{canvas} {}

    void Load(std::string filename) override {
        std::ifstream in{filename};
        if (!in) {
            throw std::runtime_error("Cannot open graph");
        }
        while (true) {
            std::string line;
            std::getline(in, line);
            if (in.eof()) break;
            std::stringstream ss{line};
            std::vector<int> adj;
            adj_matrix.push_back({});
            while (true) {
                bool has_edge;
                ss >> has_edge;
                adj_matrix.back().push_back(has_edge);
                if (ss.eof()) break;
                if (ss.fail()) {
                    throw std::runtime_error("Cannot load graph");
                }
            }
        }
    }

    void Draw() override {
        const int radius =
            std::min(canvas_->GetWidth(), canvas_->GetHeight()) * 3 / 8;
        const int cx = canvas_->GetWidth() / 2, cy = canvas_->GetHeight() / 2;

        int vert_radius = 2 * M_PI * radius / adj_matrix.size() * 3 / 8;

        std::cout << adj_matrix.size() << std::endl;
        std::vector<std::pair<int, int>> vertex_coords;
        for (int i = 0; i < adj_matrix.size(); ++i) {
            double phi = i * 2 * M_PI / adj_matrix.size();

            int x = cos(phi) * radius + cx;
            int y = sin(phi) * radius + cy;

            vertex_coords.emplace_back(x, y);
            canvas_->DrawCircle(x, y, vert_radius);
            canvas_->DrawText(x + vert_radius, y, std::to_string(i));
        }

        for (int i = 0; i < adj_matrix.size(); ++i) {
            for (int j = 0; j < adj_matrix[i].size(); ++j) {
                if (!adj_matrix[i][j]) continue;
                std::cout << i << " " << j << std::endl;
                if (j < adj_matrix.size()) {
                    double dx = vertex_coords[j].first - vertex_coords[i].first;
                    double dy =
                        vertex_coords[j].second - vertex_coords[i].second;
                    double length = std::sqrt(dx * dx + dy * dy);
                    dx /= length / vert_radius;
                    dy /= length / vert_radius;

                    canvas_->DrawArrow(vertex_coords[i].first + dx,
                                       vertex_coords[i].second + dy,
                                       vertex_coords[j].first - dx,
                                       vertex_coords[j].second - dy);
                }
            }
        }
    }

   private:
    std::vector<std::vector<int>> adj_matrix;
};