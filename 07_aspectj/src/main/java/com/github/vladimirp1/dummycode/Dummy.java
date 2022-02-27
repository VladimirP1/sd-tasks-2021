package com.github.vladimirp1.dummycode;

import com.github.vladimirp1.annotations.Profile;

public class Dummy {
    @Profile
    public void run() {
        run2();
        System.out.println("i'm a dummy method");
    }

    @Profile
    private void run2() {
    }
}
