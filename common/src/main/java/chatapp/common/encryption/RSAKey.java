package chatapp.common.encryption;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public record RSAKey(BigInteger exponent, BigInteger mod) {

	public String run(BigInteger encrypted) {
		BigInteger decrypted = encrypted.modPow(exponent, mod);
		byte[] msg = decrypted.toByteArray();
		return new String(msg);
	}

	public BigInteger run(String text) {
		byte[] textBytes = text.getBytes(StandardCharsets.UTF_8);
		BigInteger message = new BigInteger(textBytes);
		return message.modPow(exponent, mod);
	}
}
