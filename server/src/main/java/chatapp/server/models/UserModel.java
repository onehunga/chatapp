package chatapp.server.models;

public class UserModel {
	public String id;
	public long time;

	public UserModel(String username) {
		this.id = username;
		this.time = System.currentTimeMillis();
	}

	/**
	 * Ich habe keine Ahnung warum das so muss.
	 * @return die query, die den benutzer akualisiert.
	 */
	public String getUpdateQuery() {
		return "UPDATE user:" + this.id + " SET time=" + String.valueOf(this.time) + ";";
	}
}
