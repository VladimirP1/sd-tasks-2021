#include <algorithm>
#include <cctype>
#include <cmath>
#include <cstdint>
#include <fstream>
#include <tuple>

#include "CanvasFactory.h"
#include "ICanvas.h"

struct RGB {
    RGB() {}
    RGB(uint8_t r, uint8_t g, uint8_t b) : r(r), g(g), b(b) {}
    uint8_t &ch(uint8_t i) { return i ? (i == 1 ? g : b) : r; }

    uint8_t r{};
    uint8_t g{};
    uint8_t b{};
};

template <class T>
class Image {
   public:
    Image() : width_(0), height_(0), data_(nullptr) {}

    Image(size_t width, size_t height)
        : width_(width), height_(height), data_(new T[width * height]) {}

    Image(const Image &img)
        : width_(img.width_),
          height_(img.height_),
          data_(new T[width_ * height_]) {
        std::copy(img.data_, img.data_ + width_ * height_, data_);
    }

    Image(Image &&img)
        : width_(img.width_), height_(img.height_), data_(img.data_) {
        img.width_ = 0;
        img.height_ = 0;
        img.data_ = nullptr;
    }

    Image &operator=(const Image &img) {
        if (data_) {
            delete[] data_;
        }
        width_ = img.width_;
        height_ = img.height_;
        data_ = new T[width_ * height_];
        std::copy(img.data_, img.data_ + width_ * height_, data_);
        return *this;
    }

    Image &operator=(Image &&img) {
        if (data_) {
            delete[] data_;
        }
        width_ = img.width_;
        height_ = img.height_;
        data_ = img.data_;
        img.width_ = 0;
        img.height_ = 0;
        img.data_ = nullptr;
        return *this;
    }

    T &at(size_t x, size_t y) {
        if (x >= width_ || y >= height_) return fake_;
        return data_[x + width_ * y];
    }

    const T &at(size_t x, size_t y) const { return data_[x + width_ * y]; }

    T *data() { return data_; }

    const T *data() const { return data_; }

    size_t dataSize() const { return width_ * height_; }

    size_t dataSizeBytes() const { return width_ * height_ * sizeof(T); }

    size_t width() const { return width_; }

    size_t height() const { return height_; }

    virtual ~Image() {
        if (data_) {
            delete[] data_;
        }
    }

   private:
    size_t width_;
    size_t height_;
    T *data_;
    T fake_;
};

template <class T>
std::string PpmMagic();

template <>
std::string PpmMagic<RGB>() {
    return "P6";
}

template <>
std::string PpmMagic<uint8_t>() {
    return "P5";
}

template <class T>
class ImageWriter {
   public:
    ImageWriter() { state_ = State::kInvalid; }
    explicit ImageWriter(const std::string &path) {
        output_file_ =
            std::ofstream{path, std::ios_base::binary | std::ios_base::out};
        if (!output_file_) {
            throw std::runtime_error{"Cannot open output file"};
        }
    }
    ImageWriter(const ImageWriter &other) = delete;
    ImageWriter &operator=(const ImageWriter &) = delete;
    ImageWriter(ImageWriter &&other) { (*this) = std::move(other); }
    ImageWriter &operator=(ImageWriter &&other) {
        output_file_.close();
        output_file_ = std::move(other.output_file_);
        state_ = other.state_;
        return *this;
    }

    void write(const Image<T> &img) {
        if (state_ == State::kInvalid) {
            throw std::logic_error{"Write called on an invalid writer"};
        }
        state_ = State::kInvalid;
        output_file_ << PpmMagic<T>() << "\n"
                     << img.width() << " " << img.height() << "\n"
                     << 255 << "\n";

        output_file_.write(reinterpret_cast<const char *>(img.data()),
                           img.dataSizeBytes());

        output_file_.flush();

        if (!output_file_) {
            throw std::runtime_error{"Write failed"};
        }

        output_file_.close();
    }

   private:
    enum class State { kFileOpened, kInvalid };
    State state_{State::kFileOpened};
    std::ofstream output_file_{};
};

template <class T>
class ImageReader {
   public:
    ImageReader() { state_ = State::kInvalid; }
    explicit ImageReader(const std::string &path) {
        input_file_ =
            std::ifstream{path, std::ios_base::binary | std::ios_base::in};
        if (!input_file_) {
            throw std::runtime_error{"Cannot open input file"};
        }
    }
    ImageReader(const ImageReader &) = delete;
    ImageReader &operator=(const ImageReader &) = delete;
    ImageReader(ImageReader &&other) { (*this) = std::move(other); }
    ImageReader &operator=(ImageReader &&other) {
        input_file_.close();
        input_file_ = std::move(other.input_file_);
        state_ = other.state_;
        std::tie(width_, height_, max_value_) =
            std::tie(other.width_, other.height_, other.max_value_);
        return *this;
    }

    bool probe() {
        size_t tmp;
        std::string magic;
        if (state_ != State::kFileOpened) {
            return state_ == State::kHeaderParsed;
        }
        state_ = State::kInvalid;
        input_file_ >> magic;
        if (magic != PpmMagic<T>() || !input_file_) {
            return false;
        }
        input_file_ >> tmp;
        if (!input_file_) {
            return false;
        }
        width_ = tmp;
        input_file_ >> tmp;
        if (!input_file_) {
            return false;
        }
        height_ = tmp;
        input_file_ >> tmp;
        if (!input_file_ || tmp > 255) {
            return false;
        }
        max_value_ = tmp;
        input_file_.get();
        if (!input_file_) {
            return false;
        }
        state_ = State::kHeaderParsed;
        return true;
    }

    Image<T> read() {
        if (state_ != State::kHeaderParsed) {
            throw std::logic_error{"Read called on an invalid reader"};
        }
        Image<T> ret;
        try {
            ret = Image<T>{width_, height_};
        } catch (const std::bad_alloc &) {
            throw ram_error_;
        }

        input_file_.read(reinterpret_cast<char *>(ret.data()),
                         ret.dataSizeBytes());

        if (input_file_.gcount() != ret.dataSizeBytes() || !input_file_) {
            throw std::runtime_error{"Input file corrupted"};
        }

        auto predicted_size = input_file_.tellg();
        if (predicted_size < 0 || !input_file_) {
            throw std::runtime_error{"Input file unreadable"};
        }
        input_file_.seekg(0, input_file_.end);
        if (!input_file_) {
            throw std::runtime_error{"Input file unreadable"};
        }
        if (input_file_.tellg() != predicted_size) {
            throw std::runtime_error{"Garbage in the end of file"};
        }

        input_file_.close();
        return ret;
    }

    std::pair<size_t, size_t> size() const { return {width_, height_}; }

    ~ImageReader() { input_file_.close(); }

   private:
    enum class State { kFileOpened, kHeaderParsed, kInvalid };

    std::ifstream input_file_{};
    State state_{State::kFileOpened};
    size_t width_, height_, max_value_;

    const std::runtime_error ram_error_{"Image does not fit in ram"};
};

class PpmCanvas : public ICanvas {
   public:
    PpmCanvas(int width, int height, std::string filename)
        : image_(width, height), filename_{filename} {
        ImageReader<uint8_t> reader("font.pgm");
        if (!reader.probe()) {
            throw std::runtime_error("Cannot load font");
        }
        font_ = reader.read();
    }

    int GetWidth() const override { return image_.width(); };
    int GetHeight() const override { return image_.height(); };
    void DrawCircle(int x, int y, int radius) override {
        const double step = 1. / (2. * M_PI * radius) / 2.;
        for (double phi = 0.; phi < 2. * M_PI; phi += step) {
            DrawPixel(x + radius * cos(phi), y + radius * sin(phi));
        }
    };
    void DrawArrow(int x1, int y1, int x2, int y2) override {
        DrawLine(x1, y1, x2, y2);
        DrawCircle(x2, y2, 3);
    };
    void DrawText(int x, int y, std::string text) override {
        for (auto &c : text) {
            for (int i = x; i < x + font_w; ++i) {
                for (int j = y; j < y + font_h; ++j) {
                    image_.at(i, j) =
                        font_.at((i - x) + ((size_t)c) * font_w, j - y);
                }
            }
            x += font_w;
        }
    }

    void Finalize() override {
        ImageWriter<uint8_t> writer{filename_};
        writer.write(image_);
    }

    ~PpmCanvas() {}

   private:
    Image<uint8_t> image_;
    std::string filename_;

    Image<uint8_t> font_;
    const int font_w = 7, font_h = 16;

   private:
    void DrawLine(int x1, int y1, int x2, int y2) {
        if (std::abs(x2 - x1) > std::abs(y2 - y1)) {
            if (x1 > x2) DrawLine(x2, y2, x1, y1);
            for (int i = x1; i < x2; ++i) {
                double yy = ((double)(i - x1)) / ((double)(x2 - x1)) *
                                ((double)(y2 - y1)) +
                            y1;
                DrawPixel(i, std::round(yy));
            }
        } else {
            if (y1 > y2) DrawLine(x2, y2, x1, y1);
            for (int i = y1; i < y2; ++i) {
                double xx = ((double)(i - y1)) / ((double)(y2 - y1)) *
                                ((double)(x2 - x1)) +
                            x1;
                DrawPixel(std::round(xx), i);
            }
        }
    }

    void DrawPixel(int x, int y) { image_.at(x, y) = 255; }
};

namespace {
struct KeeperPpm {
    KeeperPpm() {
        CanvasFactory::RegisterFactory(
            "pgm", [](int width, int height, std::string filename) {
                return std::make_shared<PpmCanvas>(width, height, filename);
            });
    }
} __kpr__;
}  // namespace