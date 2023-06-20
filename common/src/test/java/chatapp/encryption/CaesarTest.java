package chatapp.encryption;

import chatapp.common.encryption.Caesar;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Random;

public class CaesarTest {

	@Test void caesarHelloWorld() {
		String hello = "Hello World!";

		Caesar c = new Caesar(new Random().nextInt(25) + 1);

		var encrypted = c.encrypt(hello);
		var decrypted = c.decrypt(encrypted);

		Assertions.assertEquals(hello, decrypted);
	}
}
