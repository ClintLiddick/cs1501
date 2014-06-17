package assig3;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Ignore;
import org.junit.Test;

public class LZWmodTest {

  @Test
  public void originalAndDecompressedCopySameSize() { // TODO add parameter
    File fileToCompress = new File("/Users/Clint/projects/assig3data/medium.txt");
    File compressedFile = new File("/Users/Clint/projects/datafiles/compressed.lzw");
    File copyFile = new File("/Users/Clint/projects/datafiles/medium.exp.txt");
    try {
      PrintStream fileOutStream = setStdIOToFiles(fileToCompress,compressedFile);
      LZWmod.compress();
      fileOutStream.close();
      fileOutStream = setStdIOToFiles(compressedFile, copyFile);
      LZWmod.expand();
      fileOutStream.close();
      assertEquals("not same filesize",fileToCompress.length(),copyFile.length());
    
    } catch (Exception e) {
      fail("exception while trying to (de)compress file: " + e.getMessage());
    } finally {
      cleanUpFile(compressedFile);
      cleanUpFile(copyFile);
    }
  }

//  protected static void compressFile(File inFile, File outFile) 
//      throws FileNotFoundException {
//    PrintStream fileOutStream = setStdIOToFiles(inFile, outFile);
//    LZWmod.compress();
//    fileOutStream.close();
//  }
//  
//  protected static void expandFile(File inFile, File outFile) 
//      throws FileNotFoundException {
//    PrintStream fileOutStream = setStdIOToFiles(inFile, outFile);
//    LZWmod.expand();
//    fileOutStream.close();
//  }
  
  protected static PrintStream setStdIOToFiles(File inFile, File outFile) 
      throws FileNotFoundException {
    FileInputStream fileInStream = new FileInputStream(inFile);
    PrintStream fileOutStream = new PrintStream(new FileOutputStream(outFile));
    System.setIn(fileInStream);
    System.setOut(fileOutStream);
    return fileOutStream;
  }
  
  protected static void cleanUpFile(File file) {
    try {
      Files.deleteIfExists(file.toPath());
    } catch (IOException e) {
      // TODO
    }
  }
}
