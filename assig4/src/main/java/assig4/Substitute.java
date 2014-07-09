package assig4;

import java.util.ArrayList;
import java.util.Collections;

class Substitute implements SymCipher {
  
  private final byte[] key;
  
  /**
   * Construct a new Substitute cipher with a random key of 256 bytes
   */
  Substitute() {
    // create collection of all bytes
    ArrayList<Byte> temp = new ArrayList<Byte>();
    for (int i=0; i<256; i++) {
      temp.add(new Byte((byte) i));
    }
    // get random permutation of bytes
    Collections.shuffle(temp);
    // store permutation as key
    key = new byte[256];
    for (int i=0; i<key.length; i++) {
      key[i] = temp.get(i);
    }
  }
  /**
   * Construct a new Substitute cipher with a copy of the given key
   * @param key is 256 bytes
   */
  Substitute(final byte[] key) {
    assert (key.length == 256);
    this.key = new byte[256];
    System.arraycopy(key, 0, this.key, 0, 256);
  }

  public byte[] getKey() {
    return key;
  }

  public byte[] encode(String S) {
    byte[] str = S.getBytes();
    byte[] cipherText = new byte[str.length];
    // use char byte to index substitution in key
    for (int i=0; i<str.length; i++) {
      cipherText[i] = key[toUnsignedByteAsInt(str[i])];
    }
    
    return cipherText;
  }
  
  // returns the unsigned (positive) int value of byte
  private int toUnsignedByteAsInt(byte b) {
    return (b & 0xFF);
  }

  public String decode(byte[] encryptedBytes) {
    byte[] clearText = new byte[encryptedBytes.length];
    for (int i=0; i<encryptedBytes.length; i++) {
      int ascii = 0; // decoded byte
      // linear search for substitution in key
      for (int j=0; j<key.length; j++) {
        if (key[j] == encryptedBytes[i]) {
          ascii = j; // found, original index/byte is j
          break;
        }
      }
      clearText[i] = (byte) ascii;
    }
    return new String(clearText);
  }
}
