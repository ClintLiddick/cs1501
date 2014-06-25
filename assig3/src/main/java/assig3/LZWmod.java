/*************************************************************************
 *  Compilation:  javac LZW.java
 *  Execution:    java LZW - < input.txt   (compress)
 *  Execution:    java LZW + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *
 *************************************************************************/
package assig3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LZWmod {
  final static Logger log = LoggerFactory.getLogger(LZWmod.class);

  private static final int EOF = 256;        // number of input chars
  private static final int CLEAR = 257;
  private static final int STARTING_W = 9;
  private static final double COMPRESSION_RATIO_THRESHOLD = 1.1;
  private static final int MAX_CODE_WIDTH = 16;

  public static void compress(File inFile, File outFile, ResetCodewords reset) { 
    int W = STARTING_W;
    int L = (int) Math.pow(2, W);
    try {
      log.debug("Compressing file " + inFile.toPath());
      BinaryStdIn binaryIn = new BinaryStdIn(new FileInputStream(inFile));
      BinaryStdOut binaryOut = new BinaryStdOut(new PrintStream(new FileOutputStream(outFile)));

      int bitsUncompressed = 0;
      int bitsCompressed = 0;
      double originalCompressionRatio = 1;
      double currentCompressionRation = 1;
      double ratioOfRatios = 1;
      boolean monitoring = false;
      
      // write 2-bit flag for reset method used in compression
      switch (reset) {
      case NONE:
        log.info("Compression reset method: NONE");
        binaryOut.write(0, 2);
        break;
      case RESET:
        log.info("Compression reset method: RESET");
        binaryOut.write(1,2);
        break;
      case MONITOR:
        log.info("Compression reset method: MONITOR");
        binaryOut.write(2,2);
      }
  
      String input = binaryIn.readString();
      TST<Integer> st = new TST<Integer>();
      for (int i = 0; i < EOF; i++)
        st.put("" + (char) i, i);
      int freeCode = CLEAR+1;

      while (input.length() > 0) {
        String longestPrefix = st.longestPrefixOf(input);  // Find max prefix match s.
        int code = st.get(longestPrefix);
        binaryOut.write(code, W);      // Print prefix's encoding.
        bitsCompressed += W;
        int t = longestPrefix.length();
        
        bitsUncompressed += t * 8; // TODO verify, java treats char as unicode 16 bit
        if (!monitoring)
          originalCompressionRatio = (double) bitsUncompressed / bitsCompressed;
        else {
          currentCompressionRation = (double) bitsUncompressed / bitsCompressed;
          ratioOfRatios = originalCompressionRatio/currentCompressionRation;
        }
        log.debug("COMPRESS code: " + code + " freeCode: "+freeCode+" st: " + st + " t: " + t + " s: " + longestPrefix);

        if (freeCode < L) {
          if (t < input.length()) {    // Add prefix + next char to symbol table.
            st.put(input.substring(0, t + 1), freeCode++);
            log.debug("new codword: " + input.substring(0,t+1));
          }
        } else if (W < MAX_CODE_WIDTH) {
          log.debug("COMPRESS -- increasing code width: " + (W+1));
          W++;
          L = (int) Math.pow(2, W);
          if (t < input.length())    // Add s to symbol table.
            st.put(input.substring(0, t + 1), freeCode++); // TODO should add?
        } else {
          switch (reset) {
          case MONITOR:
            monitoring = true;
            if (ratioOfRatios < COMPRESSION_RATIO_THRESHOLD) {
              break; // do not reset
            } else {
              monitoring = false;
              log.debug("compression ratio threshold exceeded");
            }
            // FALLTHROUGH
          case RESET:
            log.debug("Compress -- resetting table");
            st = new TST<Integer>();
            W = STARTING_W;
            L = (int) Math.pow(2, W);
            for (int i = 0; i < EOF; i++)
              st.put("" + (char) i, i);
            freeCode = CLEAR+1;
            binaryOut.write(CLEAR,W);
            break;
          case NONE:
            break;
          }
        }
        input = input.substring(t);            // Scan past s in input.
      }
      binaryOut.write(EOF, W);
      binaryOut.close();
    } catch (FileNotFoundException ex) {
      log.error(ex.getMessage());
    }
  } 


  public static void expand(File inFile, File outFile) {
    int W = STARTING_W;
    int L = (int) Math.pow(2, W);
    log.debug("L: " + L);
    try {
      BinaryStdIn binaryIn = new BinaryStdIn(new FileInputStream(inFile));
      BinaryStdOut binaryOut = new BinaryStdOut(new PrintStream(new FileOutputStream(outFile)));

      ResetCodewords reset = ResetCodewords.NONE;
      
      int resetFlag = binaryIn.readInt(2);
      log.debug("Expand reset flag: " + resetFlag);
      if (resetFlag != 0) {
        reset = ResetCodewords.RESET;
      }
      

      String[] st = new String[L];
      int freeCWIndex; // next available codeword value

      // initialize symbol table with all 1-character strings
      for (freeCWIndex = 0; freeCWIndex < EOF; freeCWIndex++)
        st[freeCWIndex] = "" + (char) freeCWIndex;
      
      st[freeCWIndex++] = "";   // (unused) lookahead for EOF
      st[freeCWIndex++] = "";   // unused, codeword for CLEAR

      
      int compressedCode = binaryIn.readInt(W);
      String expandedVal = st[compressedCode];
      binaryOut.write(expandedVal);

      while (true) {
        
        compressedCode = binaryIn.readInt(W);

        if (compressedCode == EOF) // EOF flag
          break;        
        // reset if hit flag
        if (compressedCode == CLEAR && reset != ResetCodewords.NONE) {
          log.info("reset flag encoutered. resetting table");
          W = STARTING_W;
          L = (int) Math.pow(2, W);
          
          st = new String[L];
          for (freeCWIndex = 0; freeCWIndex < EOF; freeCWIndex++)
            st[freeCWIndex] = "" + (char) freeCWIndex;
          
          st[freeCWIndex++] = "";   // (unused) lookahead for EOF
          st[freeCWIndex++] = "";   // unused, codeword for CLEAR
          
          compressedCode = binaryIn.readInt(W);
          expandedVal = st[compressedCode];
          binaryOut.write(expandedVal);
          continue;
        }
        
        String newCodeword = st[compressedCode];
        if (freeCWIndex == compressedCode) 
          newCodeword = expandedVal + expandedVal.charAt(0);   // special case hack
        
        log.debug("EXPAND compressedCode: " + compressedCode +
            " expandedVal: " + expandedVal + " newCodeword: " + newCodeword + " free: " + freeCWIndex);

        if (freeCWIndex < L - 1) {
          st[freeCWIndex++] = expandedVal + newCodeword.charAt(0);
        } else if(W < MAX_CODE_WIDTH) {
          log.debug("EXPAND -- increasing code width: " + (W+1));
          W++;
          L = (int) Math.pow(2, W);
          String[] newst = new String[L];
          for (int i=0; i<st.length; i++)
            newst[i] = st[i];
          st = newst;
          st[freeCWIndex++] = expandedVal + newCodeword.charAt(0);
        }
                
        expandedVal = newCodeword;
        binaryOut.write(expandedVal);
        log.debug("uncompressed: " + expandedVal);
      }
      binaryOut.close();
    } catch (FileNotFoundException ex) {
      log.error(ex.getMessage());
    }
  }



  public static void main(String[] args) {
     
    if (args[0].equals("-")) {
      if (args.length != 4) {
        System.out.println("usage: java assig3.LZWmod - <reset_method> <input_file> <output_file>");
        System.exit(1);
      }
      ResetCodewords resetMethod = ResetCodewords.NONE;
      switch(args[1].toLowerCase().charAt(0)) {
      case 'n':
        resetMethod = ResetCodewords.NONE;
        break;
      case 'r':
        resetMethod = ResetCodewords.RESET;
        break;
      case 'm':
        resetMethod = ResetCodewords.MONITOR;
        break;
      default:
        System.out.println("Invalid reset method");
        System.out.println("n = none, r = reset always, m = monitor then reset");
        System.exit(1);
        break;
      }
      compress(new File(args[2]), new File(args[3]),resetMethod);
    } else if (args[0].equals("+")) {
      if (args.length != 3) {
        System.out.println("usage: java assig3.LZWmod + <input_file> <output_file>");
        System.exit(1);
      }
      expand(new File(args[1]), new File(args[2]));
    } else {
      System.out.println("Enter - to compress or + to decomopress");
      System.exit(1);
    }
  }
  
  protected static enum ResetCodewords {
    NONE,
    MONITOR,
    RESET,
  }

}