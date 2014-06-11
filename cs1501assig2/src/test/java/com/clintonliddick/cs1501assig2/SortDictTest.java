package com.clintonliddick.cs1501assig2;

import static org.junit.Assert.*;

import org.junit.Test;

public class SortDictTest {
  
  @Test
  public void testInsertAndFindSuccess() {
    SortDict dict = new SortDict();
    dict.insert("zed");
    dict.insert("adam");
    dict.insert("bill");
    
    assertTrue("inserted item not found",dict.find("adam"));
    assertTrue("inserted item not found",dict.find("zed"));
    assertTrue("inserted item not found",dict.find("bill"));
  }
  
  @Test
  public void testBinaryMidSearchEmpty() {
    SortDict dict = new SortDict();
    assertEquals(0,dict.binaryMidSearch("any"));
  }
  
  @Test
  public void testBinaryMidSearch1() {
    SortDict dict = new SortDict();
    dict.add("any");
    assertEquals(0,dict.binaryMidSearch("any"));
  }
  
  @Test
  public void testBinaryMidSearch2() {
    SortDict dict = new SortDict();
    dict.add("one");
    dict.add("two");
    assertEquals(1,dict.binaryMidSearch("two"));
  }
  
  @Test
  public void testCheckLeftRightForInsertPointMiddle() {
    SortDict dict = new SortDict();
    dict.add("a");
    dict.add("c");
    int index = dict.binaryMidSearch("b");
    assertEquals(1,dict.checkLeftRightForInsertPoint(index, "b"));
  }
  
  @Test
  public void testCheckLeftRightForInsertPointBeginning() {
    SortDict dict = new SortDict();
    dict.add("b");
    dict.add("c");
    int index = dict.binaryMidSearch("a");
    assertEquals(0,dict.checkLeftRightForInsertPoint(index, "a"));
  }
  
  @Test
  public void testCheckLeftRightForInsertPointEnd() {
    SortDict dict = new SortDict();
    dict.add("a");
    dict.add("b");
    int index = dict.binaryMidSearch("c");
    assertEquals(2,dict.checkLeftRightForInsertPoint(index, "c"));
  }
  
  @Test
  public void testInsertInOrder() {
    SortDict dict = new SortDict();
    dict.insert("boy");
    dict.insert("cow");
    dict.insert("any");
    assertEquals("any is 1st","any",dict.get(0));
    assertEquals("boy is 2nd","boy",dict.get(1));
    assertEquals("cow is 3rd","cow",dict.get(2));
  }

}
