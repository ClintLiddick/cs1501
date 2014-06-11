package com.clintonliddick.cs1501assig2;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

public class HashDictTest {

  @Test
  public void testTableLoad() {
    HashDict dict = new HashDict();
    assertEquals(0,dict.loadFactor(),0);
    fillToForceResize(dict);
    assertEquals(0.5,dict.loadFactor(),0.05);
  }
  
  @Test
  public void testInsertAndFind() {
    HashDict dict = new HashDict();
    dict.insert("boy");
    assertTrue("word not found",dict.find("boy"));
  }
  
  @Test
  public void testInsertAndFindAfterResize() {
    HashDict dict = new HashDict();
    dict.insert("before");
    fillToForceResize(dict);
    dict.insert("after");
    assertTrue("word added before resize not found",dict.find("before"));
    assertTrue("word added after resize not found",dict.find("after"));
  }
  
  void fillToForceResize(HashDict dict) {
    StringBuilder sb = new StringBuilder();
    // force resize
    for (int i=65;i<85;i++) {
      sb.append((char) i);
      dict.insert(sb.toString());
    }
  }

}
