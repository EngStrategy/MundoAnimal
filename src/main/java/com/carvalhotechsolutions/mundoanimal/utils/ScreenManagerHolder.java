package com.carvalhotechsolutions.mundoanimal.utils;

public class ScreenManagerHolder {
    private static ScreenManager instance;

    public static void initialize(ScreenManager manager) {
        instance = manager;
    }

    public static ScreenManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ScreenManager ainda não foi inicializado.");
        }
        return instance;
    }
}
