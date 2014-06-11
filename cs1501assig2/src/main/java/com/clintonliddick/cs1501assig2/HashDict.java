package com.clintonliddick.cs1501assig2;

public class HashDict implements searchTest {
  
  private static final double MAX_LOAD_FACTOR = 0.75;
  
  private static int[] tablePrimes = { 
    41, 83, 167, 337, 677, 1361, 2729, 5471, 10949, 21911, 43853, 87719, 175447, 350899};
  private static int[] dhvalPrimes = { 
    37, 79, 163, 331, 673, 1327, 2719, 5449, 10939, 21893, 43801, 87701, 175433, 350891};
  
  private String[] table;
  private int dhval;
  private int size;
  private int primesIndex;
  
  
  public HashDict() {
    table = new String[19];
    dhval = 17;
    size = 0;
    primesIndex = 0;
  }

  public void insert(String s) {
    s = s.toLowerCase();
    if (loadFactor() > MAX_LOAD_FACTOR)
      resize();
    
    int index = findFirstIndex(s);
    if (table[index] == null) {
      table[index] = s; 
      size++;
    }
  }

  public boolean find(String s) {
    s = s.toLowerCase();
    return table[findFirstIndex(s)] != null;
  }
  
  
  /**
   * Returns where the String is or should be
   * Note: it is left up to the caller to handle both potentialities 
   * 
   * @param String s
   * @return the first index where String is found, or first available null position along its increment
   */
  int findFirstIndex(String s) {
    int index = hashIndex(s);
    int increment = hashIncrement(index);
    while (table[index] != null && !table[index].equals(s)) {
      index = (index + increment) % table.length;
    }
    return index;
  }
  
  int hashIncrement(int hashCode) {
    return (hashCode % dhval) + 1;
  }
  
  int hashIndex(String s) {
    return (s.hashCode() & 0x7FFFFFFF) % table.length;
  }
  
  double loadFactor() {
    return (double) size/table.length;
  }

  void resize() {
    try {
      String[] oldTable = table;
      table = new String[tablePrimes[primesIndex]];
      dhval = dhvalPrimes[primesIndex];
      primesIndex++;
      size = 0;
      
      for (int i=0; i < oldTable.length; i++) {
        if (oldTable[i] != null)
          insert(oldTable[i]);
      }
      
    } catch (IndexOutOfBoundsException ex) {
        throw new IndexOutOfBoundsException("table size exceeded stored prime limit"); 
    }
  }
}
