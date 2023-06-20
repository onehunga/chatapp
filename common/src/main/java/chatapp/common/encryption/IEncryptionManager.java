package chatapp.common.encryption;

public interface IEncryptionManager {
	String encrypt(String message);
	String decrypt(String message);
}
