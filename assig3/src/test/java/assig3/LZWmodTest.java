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
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import assig3.LZWmod.TableResetPolicy;

@RunWith(Parameterized.class)
public class LZWmodTest {
  
  private TableResetPolicy resetPolicy;
  private File fileToCompress;
  private File compressedFile;
  private File copyFile;

  public LZWmodTest(String filename, TableResetPolicy rp) {
    this.fileToCompress = new File("/Users/Clint/projects/assig3data/"+filename);
    this.compressedFile = new File("/Users/Clint/projects/datafiles/" + rp.toString() + filename + ".lzw");
    this.copyFile = new File("/Users/Clint/projects/datafiles/" + rp.toString() + filename);
    this.resetPolicy = rp;
  }
  
  @Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] {
        /*{"large.txt",TableResetPolicy.NONE},
        {"large.txt", TableResetPolicy.RESET}, 
        {"large.txt", TableResetPolicy.MONITOR},
        
        {"texts.tar",TableResetPolicy.NONE},
        {"texts.tar", TableResetPolicy.RESET}, 
        {"texts.tar", TableResetPolicy.MONITOR},*/
        
        {"all.tar",TableResetPolicy.NONE},
        {"all.tar", TableResetPolicy.RESET}, 
        {"all.tar", TableResetPolicy.MONITOR}
        });
  }
  
  @Before
  public void createCompressedAndDecompressedCopy() throws Exception {
//    compressedFile = new File("/Users/Clint/projects/datafiles/tempcompressed.lzw");
    
    try {
      LZWmod.compress(fileToCompress,compressedFile,resetPolicy);
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
      throw ex;
    } finally {
      try {
        bisOrig.close();
        bisCopy.close();
      } catch (IOException ex) { }
    }
  }
  
  private void cleanUpFileCopies() {
//    cleanUpFile(compressedFile);
//    cleanUpFile(copyFile);
  }
  
  
  protected static void cleanUpFile(File file) {
    try {
      Files.deleteIfExists(file.toPath());
    } catch (IOException e) {  }
  }

}
