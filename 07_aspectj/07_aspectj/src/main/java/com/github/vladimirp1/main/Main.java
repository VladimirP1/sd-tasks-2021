package com.github.vladimirp1.main;

import com.github.vladimirp1.annotations.GetProfiler;
import com.github.vladimirp1.dummycode.Dummy;
import com.github.vladimirp1.profiler.Profiler;


public class Main {
    @GetProfiler
    static Profiler GetProfiler() {
        return null;
    }

    public static void main(String[] args) {
        Dummy dummy = new Dummy();
        for (int i = 0; i < 10000; ++i) {
            dummy.run();
        }

        System.out.println(GetProfiler().GetStats());
    }
}
