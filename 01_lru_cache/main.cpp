#include <iostream>

#include <LruCache.hpp>

struct TracedObject {
    TracedObject(int x) : x_(x) {
        std::cout << "TracedObject(" << x_ << ")" << std::endl;
    }

    ~TracedObject() { std::cout << "~TracedObject(" << x_ << ")" << std::endl; }

   private:
    int x_;
};

int main() {
    LruCache<int, TracedObject> c(6);

    c.put(1, 1);
    c.put(2, 2);
    c.put(3, 3);
    c.put(4, 4);
    c.put(4, 5);
    c.put(1, 1);
    std::cout << "---" << std::endl;
}