package chatapp.client.local;

import chatapp.client.Client;
import chatapp.common.encryption.IEncryptionManager;
import chatapp.common.serialize.InterfaceAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static chatapp.client.util.FileUtil.*;

public class LocalStore {
	private static LocalStore instance;
	private File directory;
	private Gson gson;

	private LocalStore() {
		this.directory = new File("local");

		if(!directory.isDirectory()) {
			directory.mkdirs();
		}

		gson = new GsonBuilder()
			.registerTypeAdapter(IEncryptionManager.class, new InterfaceAdapter<IEncryptionManager>())
			.create();
	}

	public static LocalStore getInstance() {
		if(instance == null) {
			instance = new LocalStore();
		}
		return instance;
	}

	public boolean contains(String user) {
		mkdir();
		var file = new File(directory.getPath() + File.separatorChar + Client.state.username + File.separatorChar + user);
		return file.exists();
	}
	public void saveEncryptionManager(String user, IEncryptionManager manager) {
		mkdir();
		var ser = gson.toJson(manager, IEncryptionManager.class);

		System.out.println(directory.getAbsolutePath() + File.separatorChar + Client.state.username + File.separatorChar + user);
		var writer = openWrite(directory.getAbsolutePath() + File.separatorChar + Client.state.username + File.separatorChar + user);

		// Pfad laden
		try {
			writer.write(ser);
			writer.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public IEncryptionManager loadEncryptionManager(String user) {
		mkdir();
		String data;
		try {
			var raw = Files.readAllBytes(Path.of(directory.getPath() + File.separatorChar + Client.state.username + File.separatorChar + user));
			data = new String(raw);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		return gson.fromJson(data, IEncryptionManager.class);
	}

	private void mkdir() {
		new File(directory.getPath() + File.separatorChar + Client.state.username).mkdirs();
	}
}
