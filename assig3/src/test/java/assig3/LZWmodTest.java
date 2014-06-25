package assig3;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import assig3.LZWmod.ResetCodewords;

@RunWith(Parameterized.class)
public class LZWmodTest {
  
  private ResetCodewords resetMethod;
  private File fileToCompress;
  private File compressedFile;
  private File copyFile;

  public LZWmodTest(ResetCodewords rm) {
    this.resetMethod = rm;
  }
  
  @Parameters
  public static Collection<ResetCodewords[]> resetMehtodParams() {
//    return Arrays.asList(ResetCodewords.values());
    return Arrays.asList(new ResetCodewords[][] {
        {ResetCodewords.NONE},
        {ResetCodewords.RESET}, 
        {ResetCodewords.MONITOR}
        });
  }
  
  @Before
  public void createCompressedAndDecompressedCopy() throws Exception {
    fileToCompress = new File("/Users/Clint/projects/assig3data/gone_fishing.bmp");
    compressedFile = new File("/Users/Clint/projects/datafiles/compressed.lzw");
    copyFile = new File("/Users/Clint/projects/datafiles/gfCopy.bmp");
    
    try {
      LZWmod.compress(fileToCompress,compressedFile,resetMethod);
      LZWmod.expand(compressedFile,copyFile);
    } catch (Exception e) {
      throw e;
    }
  }

  @Test
  public void originalAndDecompressedCopySameSize() {
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
//      ex.printStackTrace();
      throw ex;
    } finally {
      try {
        bisOrig.close();
        bisCopy.close();
      } catch (IOException ex) { }
    }
  }
  
  @After
  public void cleanUpFileCopies() {
    cleanUpFile(compressedFile);
    cleanUpFile(copyFile);
  }
  
  
  protected static void cleanUpFile(File file) {
    try {
      Files.deleteIfExists(file.toPath());
    } catch (IOException e) {  }
  }

}
