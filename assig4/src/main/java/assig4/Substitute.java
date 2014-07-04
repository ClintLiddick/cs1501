package assig4;

import java.util.ArrayList;
import java.util.Collections;

class Substitute implements SymCipher {
  
  private final byte[] key;
  
  Substitute() {
    ArrayList<Byte> temp = new ArrayList<Byte>();
    for (int i=0; i<256; i++) {
      temp.add(new Byte((byte) i));
    }
    Collections.shuffle(temp);
    key = new byte[256];
    for (int i=0; i<key.length; i++) {
      key[i] = temp.get(i);
    }
  }
  
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
    for (int i=0; i<str.length; i++) {
      cipherText[i] = key[(str[i] & 0xFF)]; // byte -> positive int
    }
    
    return cipherText;
  }

  public String decode(byte[] bytes) {
    byte[] clearText = new byte[bytes.length];
    for (int i=0; i<bytes.length; i++) {
      int ascii = 0;
      for (int j=0; j<key.length; j++) {
        if (key[j] == bytes[i]) {
          ascii = j;
          break;
        }
      }
      clearText[i] = (byte) ascii;
    }
    return new String(clearText);
  }

}
