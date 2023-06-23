package chatapp.encryption;

import chatapp.common.encryption.RSA;
import chatapp.common.encryption.RSAGenerator;
import org.junit.jupiter.api.Test;

public class RSAEncrypterTest {
	@Test
	void simple() {
		var genA = new RSAGenerator(1024);
		var genB = new RSAGenerator(1024);
		genA.generateKeys();
		genB.generateKeys();

		var encrypter = new RSA(genA.getPublicKey(), genB.getPrivateKey());
		var decrypter = new RSA(genB.getPublicKey(), genA.getPrivateKey());

		String message = "Hallo Welt";
	}
}
