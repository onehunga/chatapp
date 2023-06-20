package chatapp.encryption;

import chatapp.common.encryption.RSAGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RSATest {

	@Test
	void rsaHelloWorld() {
		RSAGenerator generator = new RSAGenerator(1024);
		generator.generateKeys();

		var encrypted = generator.getPublicKey().run("hello world");
		var decrypted = generator.getPrivateKey().run(encrypted);

		assertEquals("hello world", decrypted);
	}
}
