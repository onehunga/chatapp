package chatapp.common.encryption;

import java.math.BigInteger;

/**
 * Verwendet den eigenen privaten Schlüssel und den Öffentlichen Schlüssel des Partners
 */
public class RSAEncrypter implements IEncryptionManager {
	private final RSAKey publicKey;
	private final RSAKey privateKey;

	public RSAEncrypter(RSAKey publicKey, RSAKey privateKey) {
		this.publicKey = publicKey;
		this.privateKey = privateKey;
	}

	@Override
	public String encrypt(String message) {
		return this.publicKey.run(message).toString();
	}

	@Override
	public String decrypt(String message) {
		return this.privateKey.run(new BigInteger(message));
	}
}
