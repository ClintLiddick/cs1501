package com.clintonliddick.cs1501assig2;

import static org.junit.Assert.*;

import org.junit.Test;

public class TimerTest {

  @Test
  public void testGetSecondsToComplete() {
    Runnable myRunner = new Runnable() {
      public void run() {
        for (int i=0; i<10; i++);
      } 
    };
    assertTrue("time should be positive",Timer.getSecondsToComplete(myRunner) > 0.0);
  }

}
