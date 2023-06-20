package chatapp.common.encryption;

import java.math.BigInteger;
import java.util.Random;

public class RSAGenerator {
	private static Random random = new Random();

	private int size;

	RSAKey publicKey;
	RSAKey privateKey;

	public RSAGenerator(int size) {
		this.size = size;
	}

	public void generateKeys() {
		BigInteger p = largePrime();
		BigInteger q = largePrime();

		BigInteger n = p.multiply(q);

		// phi(n) = (p - 1) * (q - 1)
		BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

		// öffentlicher exponent
		BigInteger e = BigInteger.valueOf(65537);


		// privater schlüssel
		BigInteger d = e.modInverse(phi);

		this.publicKey = new RSAKey(e, n);
		this.privateKey = new RSAKey(d, n);
	}

	/**
	 * Generiert eine große Primzahl mit einer hohen Wahrscheinlichkeit.
	 * @return die große Primzahl
	 */
	private BigInteger largePrime() {
		BigInteger prime;

		do {
			prime = BigInteger.probablePrime(size / 2, random);
		} while(!prime.isProbablePrime(1000)); // wiederhohle bis der primzahl test 1000 mal bestanden wurde

		return prime;
	}

	public RSAKey getPublicKey() {
		return publicKey;
	}

	public RSAKey getPrivateKey() {
		return privateKey;
	}
}
