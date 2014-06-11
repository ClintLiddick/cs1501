package com.clintonliddick.cs1501assig2;

import java.util.ArrayList;

public class SortDict extends ArrayList<String> implements searchTest {
  
  public void insert(String s) {
    s = s.toLowerCase();
    int index = findSortedInsertPoint(s);
    if (index > this.size())
      this.add(s);
    else
      this.add(index,s);
  }

  public boolean find(String s) {
    s = s.toLowerCase();
    return s.equals(this.get(binaryMidSearch(s)));
  }
  
  int binaryMidSearch(String s) {
    int start = 0;
    int end = this.size() - 1;
    int mid = 0;
    
    while (end >= start) {
      mid = (end + start)/2;
      int cmp = s.compareTo(this.get(mid));
      if (cmp == 0) {
        break;
      } else if (cmp > 0) {
        start = mid + 1;
      } else {
        end = mid - 1;
      }
    }
    
    return mid;
  }
  
  int findSortedInsertPoint(String s) {
    if (this.size() == 0)
      return 0;
    
    int index = binaryMidSearch(s);
    
    if (!s.equals(this.get(index))) {
      index = checkLeftRightForInsertPoint(index,s);
    }
    return index;
  }
  
  int checkLeftRightForInsertPoint(int index, String s) {
    int cmp = s.compareTo(this.get(index));
    while (cmp > 0) {
      index++;
      if (index >= this.size())
        break;
      cmp = s.compareTo(this.get(index));
    }

    return index;
  }
  
}
