package assig4;

import java.util.Random;

class Add128 implements SymCipher {
  
  private final byte[] key;
  
  Add128() {
    Random rand = new Random();
    key = new byte[128];
    rand.nextBytes(key);
  }
  
  Add128(final byte[] key) {
    assert (key.length == 128);
    this.key = new byte[128];
    System.arraycopy(key, 0, this.key, 0, 128);
  }

  public byte[] getKey() {
    return key;
  }

  public byte[] encode(String S) {
    byte[] cipherText = S.getBytes();
    for (int i=0; i<cipherText.length; i++) {
      cipherText[i] += key[i % key.length];
    }
    
    return cipherText;
  }

  public String decode(byte[] bytes) {
    
    byte[] clearText = new byte[bytes.length];
    System.arraycopy(bytes, 0, clearText, 0, bytes.length);
    for (int i=0; i<clearText.length; i++) {
      clearText[i] -= key[i % key.length];
    }
    
    return new String(clearText);
  }

//  private static String toASCII(byte[] bytes) {
//    StringBuilder sb = new StringBuilder();
//    for (int i=0; i<bytes.length; i++) {
//      sb.append((char)bytes[i]);
//    }
//    return sb.toString();
//  }
}
