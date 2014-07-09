package assig4;

import java.util.Random;

class Add128 implements SymCipher {
  
  private final byte[] key;
  
  /**
   * Constructs new Add128 cipher with random key
   */
  Add128() {
    Random rand = new Random();
    key = new byte[128];
    // fill key with random bytes
    rand.nextBytes(key);
  }
  
  /**
   * Construct Add128 cipher with copy of provided 128 byte key
   * @param key
   */
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
      // add key byte at char index % the key length (to wrap)
      cipherText[i] += key[i % key.length];
    }
    
    return cipherText;
  }

  public String decode(byte[] bytes) {
    
    byte[] clearText = new byte[bytes.length];
    System.arraycopy(bytes, 0, clearText, 0, bytes.length);
    for (int i=0; i<clearText.length; i++) {
      // subtract key byte at byte index % the key length (to wrap)
      clearText[i] -= key[i % key.length];
    }
    
    return new String(clearText);
  }
}
