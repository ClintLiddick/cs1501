package assig3;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class LZWmodTest {
  
  private static File fileToCompress;
  private static File compressedFile;
  private static File copyFile;

  
  @BeforeClass
  public static void createCompressedAndDecompressedCopy() throws Exception {
    fileToCompress = new File("/Users/Clint/projects/assig3data/medium.txt");
    compressedFile = new File("/Users/Clint/projects/datafiles/compressed.lzw");
    copyFile = new File("/Users/Clint/projects/datafiles/medium.exp.txt");
    
    try {
      LZWmod.compress(fileToCompress, compressedFile);
      LZWmod.expand(compressedFile,copyFile);
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }
  
  @Test
  public void testCompress() {
    fail("unimplemented");
  }
  
  @Test
  public void testExpand() {
    fail("unimplemented");
  }
  
  

  @Test
  public void originalAndDecompressedCopySameSize() { // TODO add parameter
      assertNotEquals("zero compression",fileToCompress.length(), compressedFile.length());
      assertEquals("not same filesize",fileToCompress.length(),copyFile.length());
  }
  
  @Test
  public void originalAndDecompressedCopySameData() throws Exception {
    BufferedInputStream bisOrig = null;
    BufferedInputStream bisCopy = null;
    try {
      bisOrig = new BufferedInputStream(new FileInputStream(fileToCompress));
      bisCopy = new BufferedInputStream(new FileInputStream(copyFile));
      int orig;
      int copy;
      do {
        orig = bisOrig.read();
        copy = bisCopy.read();
        assertEquals("data inconsistency with copy",orig,copy);
      } while (orig != -1);
    } catch (Exception ex) {
      throw ex;
    } finally {
      try {
        bisOrig.close();
        bisCopy.close();
      } catch (IOException ex) { }
    }
  }
  
  @AfterClass
  public static void cleanUpFileCopies() {
    cleanUpFile(compressedFile);
    cleanUpFile(copyFile);
  }
  
  
  protected static void cleanUpFile(File file) {
    try {
      Files.deleteIfExists(file.toPath());
    } catch (IOException e) {
      // TODO
    }
  }

}
