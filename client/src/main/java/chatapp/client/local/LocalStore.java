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

public class LocalStore {
	private static LocalStore instance;
	private final File directory;
	private final Gson gson;

	private LocalStore() {
		this.directory = new File("local" + File.separatorChar + Client.state.username);

		if(!directory.isDirectory()) {
			directory.mkdirs();
		}

		gson = new GsonBuilder()
			.registerTypeAdapter(IEncryptionManager.class, new InterfaceAdapter<IEncryptionManager>())
			.create();
	}

	public boolean contains(String user) {
		var file = new File(directory.getPath() + File.separatorChar + user);
		return file.exists();
	}
	public void saveEncryptionManager(String user, IEncryptionManager manager) {
		var ser = gson.toJson(manager, IEncryptionManager.class);

		var file = new File(directory.getAbsolutePath() + File.separatorChar + user);
		if(!file.exists()) {
			try {
				if(!file.createNewFile()) {
					return;
				}
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
		}

		// Pfad laden
		try {
			Files.writeString(file.toPath(), ser);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public IEncryptionManager loadEncryptionManager(String user) {
		String data;
		try {
			var raw = Files.readAllBytes(Path.of(directory.getPath() + File.separatorChar + user));
			data = new String(raw);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		return gson.fromJson(data, IEncryptionManager.class);
	}

	public static LocalStore getInstance() {
		if(instance == null) {
			instance = new LocalStore();
		}
		return instance;
	}
}
