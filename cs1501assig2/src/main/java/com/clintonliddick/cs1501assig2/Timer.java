package com.clintonliddick.cs1501assig2;

public class Timer {
  
  public static double getSecondsToComplete(Runnable r) {
    long start = System.nanoTime();
    r.run();
    long time = System.nanoTime() - start;
    return time * Math.pow(10,-9);
  }
  
}
