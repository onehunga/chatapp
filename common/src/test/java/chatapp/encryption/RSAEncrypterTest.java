package chatapp.encryption;

import chatapp.common.encryption.RSAEncrypter;
import chatapp.common.encryption.RSAGenerator;
import org.junit.jupiter.api.Test;

public class RSAEncrypterTest {
	@Test
	void simple() {
		var genA = new RSAGenerator(1024);
		var genB = new RSAGenerator(1024);
		genA.generateKeys();
		genB.generateKeys();

		var encrypter = new RSAEncrypter(genA.getPublicKey(), genB.getPrivateKey());
		var decrypter = new RSAEncrypter(genB.getPublicKey(), genA.getPrivateKey());

		String message = "Hallo Welt";
	}
}
