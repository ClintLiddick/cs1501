package com.clintonliddick.cs1501assig2;

import java.util.ArrayList;

public class SeqDict extends ArrayList<String> implements searchTest {
    
  public void insert(String s) {
    this.add(s.toLowerCase());
  }

  public boolean find(String s) {;
    return this.contains(s.toLowerCase());
  }

}
