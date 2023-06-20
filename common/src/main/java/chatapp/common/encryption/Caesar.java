package chatapp.common.encryption;

public class Caesar implements IEncryptionManager {
	private int key;

	public Caesar(int key) {
		this.key = key;
	}

	@Override
	public String encrypt(String message) {
		StringBuilder ciphertext = new StringBuilder();

		for(int i = 0; i < message.length(); i++) {
			char c = message.charAt(i);

			if(Character.isAlphabetic(c)) {
				char base = Character.isUpperCase(c) ? 'A' : 'a';

				c = (char) ((c - base + key) % 26 + base);
			}

			ciphertext.append(c);
		}

		return ciphertext.toString();

		/*
		byte[] mBytes = message.getBytes(StandardCharsets.UTF_8);

		for(int i = 0; i < mBytes.length; i++) {
			int e = ((int) mBytes[i]) + key;
			if (e > Number.MAX_BYTE) {
				e -= 255;
			}
			mBytes[i] = (byte) e;
		}

		return new String(mBytes);

		 */
	}

	@Override
	public String decrypt(String message) {
		StringBuilder out = new StringBuilder();

		for(int i = 0; i < message.length(); i++) {
			char c = message.charAt(i);

			if(Character.isAlphabetic(c)) {
				char base = Character.isUpperCase(c) ? 'A' : 'a';

				c = (char) ((c - base + 26 - key) % 26 + base);
			}

			out.append(c);
		}

		return out.toString();

		/*
		byte[] mBytes = message.getBytes(StandardCharsets.UTF_8);

		for(int i = 0; i < mBytes.length; i++) {
			int d = ((int) mBytes[i]) - key;
			if(d < Number.MIN_BYTE) {
				System.out.println(d + 255);
				d += 255;
			}
			mBytes[i] = (byte) d;
		}

		return new String(mBytes);

		 */
	}
}
