package assig4;

import static org.junit.Assert.*;

import org.junit.Test;

public class SubstituteTest {
  
  private static final String plain = "The quick fox jumps over the lazy brown dog.";

  @Test
  public void testEncode() {
    Substitute sub = new Substitute();
    String cipher = new String(sub.encode(plain));
    assertNotEquals("ciphertext matches plaintext", plain, cipher);
  }

  @Test
  public void testDecryptDataSame() {
    Substitute sub1 = new Substitute();
    byte[] cipher = sub1.encode(plain);
    Substitute sub2 = new Substitute(sub1.getKey());
    String decrypted = sub2.decode(cipher);
    assertEquals("decrypted data doesn't match", plain, decrypted);
  }
}
