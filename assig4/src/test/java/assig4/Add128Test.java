package assig4;

import static org.junit.Assert.*;

import org.junit.Test;

public class Add128Test {
  
  private static final String plain = "The quick fox jumps over the lazy brown dog.";

  @Test
  public void testEncode() {
    Add128 sub = new Add128();
    String cipher = new String(sub.encode(plain));
    assertNotEquals("ciphertext matches plaintext", plain, cipher);
  }

  @Test
  public void testDecryptDataSame() {
    Add128 sub1 = new Add128();
    byte[] cipher = sub1.encode(plain);
    Add128 sub2 = new Add128(sub1.getKey());
    String decrypted = sub2.decode(cipher);
    assertEquals("decrypted data doesn't match", plain, decrypted);
  }
}
