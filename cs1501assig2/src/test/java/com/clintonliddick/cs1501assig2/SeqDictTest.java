package com.clintonliddick.cs1501assig2;

import static org.junit.Assert.*;

import org.junit.Test;

public class SeqDictTest {

  @Test
  public void testInsertAndFind() {
    SeqDict dict = new SeqDict();
    dict.insert("hello");
    assertTrue("\"hello\" should be in dict",dict.find("hello"));
    assertFalse("\"goodbye\" should not be in dict",dict.find("goodbye"));
  }
  
  

}
