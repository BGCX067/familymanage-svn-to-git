package com.jayce.eopengljni;

public class OpenGLJniLib {

     static {
         System.loadLibrary("gljni");
     }

     public static native void init(int width, int height);
     public static native void create();
     public static native void step();
}
