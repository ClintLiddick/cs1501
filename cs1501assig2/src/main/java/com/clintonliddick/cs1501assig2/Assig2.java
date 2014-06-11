package com.clintonliddick.cs1501assig2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Assig2 {
  private File dictionaryFile;
  private File document;
  private double fileTime;

  // dictionary specific fields, all indexed by dictionary type
  private searchTest[] dictionaries = new searchTest[3];
  private double[] dictionaryTimes = new double[3];
  private int[] wordsCorrect = new int[3];
  private int[] searches = new int[3];
  
  private static final String TOKEN_REGEX = "[ \\t\\n\\r\\f.,!?\";:\\(\\)\\[\\]]+";

  public static void main(String[] args) {
    new Assig2().run(args);
  }

  public void run(String[] args) {
    setFilesFromArgs(args);
    fillDictionaries();
    
    computeFileTime(); // dummy run to potentially cache file
    
    computeDictionaryTimes();
    printResultsToFile();
  }

  void setFilesFromArgs(String[] args) {
    if (args.length == 2) {
      dictionaryFile = new File(args[0]);
      document = new File(args[1]);
    } else {
      exitWithUsage();
    }
  }

  void fillDictionaries() {
    try {
      FileReader fr = new FileReader(dictionaryFile);
      BufferedReader br = new BufferedReader(fr);

      dictionaries[0] = new SeqDict();
      dictionaries[1] = new SortDict();
      dictionaries[2] = new HashDict();

      String word;
      while ((word = br.readLine()) != null) {
        dictionaries[0].insert(word);
        dictionaries[1].insert(word);
        dictionaries[2].insert(word);
      }
      br.close();
    } catch (IOException ex) {
      exitBadFile(dictionaryFile.toString());
    }
  }
  

  
  // NOTE:
  // there is a connection between order of operations and runtime
  // the first operation is significantly slower than comparable later operations
  // and later operations show signs of a speed boost
  // I believe this is related to some kind of file caching optimization
  void computeDictionaryTimes() {
    int d;
    for (d=0; d < dictionaries.length; d++) {
      double fileTime = computeFileTime();
      double rawTime = computeDictionaryTime(d);
      if (rawTime < fileTime)
        dictionaryTimes[d] = 0;
      else
        dictionaryTimes[d] =  rawTime - fileTime;
    }
  }
  
  double computeFileTime() {
	    return computeDictionaryTime(-1);
	  }
  
  double computeDictionaryTime(int d) {
	  return Timer.getSecondsToComplete(new CheckDictionary(d));
  }
  
  void printResultsToFile() {
    try {
      FileWriter fw = new FileWriter("results.txt",false);
      PrintWriter pw = new PrintWriter(fw);
      pw.println("Dictionaries " + dictionaryFile.toString() + " read in");
      pw.println("Test File: " + document.toString() + "\n");
      
      pw.println("Dictionary 0 (unsorted array)");
      printSingleResultToFile(pw, 0);
      pw.println("Dictionary 1 (sorted array)");
      printSingleResultToFile(pw, 1);
      pw.println("Dictionary 2 (hash table)");
      printSingleResultToFile(pw,2);

      pw.close();
    } catch (IOException ex) {
      printResultsToStdout();
    }
  }
  
  void printSingleResultToFile(PrintWriter pw, int dictIndex) {
    pw.println("\tTotal words check: " + searches[dictIndex]);
    pw.println("\tNumber of words found: " + wordsCorrect[dictIndex]);
    pw.println("\tNumber of words not found: " + (searches[dictIndex] - wordsCorrect[dictIndex]));
    pw.format("\tTotal time required: %5g%n", dictionaryTimes[dictIndex]);
    pw.format("\tAverage time required: %5g%n", dictionaryTimes[dictIndex]/searches[dictIndex]);
  }
  
  void printResultsToStdout() {
    for (int i=0; i<dictionaries.length; i++) {
      System.out.println("Dictionary " + (i+1));
      System.out.println("Total words check: " + searches[i]);
      System.out.println("Number of words found: " + wordsCorrect[i]);
      System.out.println("Number of words not found: " + (searches[i] - wordsCorrect[i]));
      System.out.println("Total time required: " + dictionaryTimes[i]);
      System.out.println("Average time required: " + dictionaryTimes[i]/searches[i]);
    }
  }

  private void exitWithUsage() {
    System.out.println("usage: java Assig2 <dictionary_file> <file_to_spellcheck>");
    System.exit(0);
  }

  private void exitBadFile(String filename) {
    System.out.println("unable to open file: " + filename);
    System.exit(0);
  }
  
  
  
  class CheckDictionary implements Runnable {
    int dictIndex;
    CheckDictionary(int whichDict) {
      dictIndex = whichDict;
    }
    
    public void run() {
      BufferedReader br = null;
      try {
        br = new BufferedReader(new FileReader(document));
        String str;
 
      while ((str = br.readLine()) != null) {
          String[] tokens = str.split(TOKEN_REGEX);
          for (int t=0; t<tokens.length; t++) {
              
            if (dictIndex == -1)
              continue; // dummy for fileTime calculation

            if (dictionaries[dictIndex].find(tokens[t])) {
              wordsCorrect[dictIndex]++;
            }
            searches[dictIndex]++;
          }
        }
      } catch (IOException ex) {
        exitBadFile(document.toString());
        // Note, the finally block will not be executed in this catch case
      } finally {
        try {
          if (br != null) {
            br.close();
          }
        } catch (IOException ex) { } // do NOTHING JUST GIVE UP
      }
    }
  }
}
