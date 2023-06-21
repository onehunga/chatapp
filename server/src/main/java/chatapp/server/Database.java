package chatapp.server;

import chatapp.server.models.MessageModel;
import com.surrealdb.connection.SurrealConnection;
import com.surrealdb.connection.SurrealWebSocketConnection;
import com.surrealdb.driver.SyncSurrealDriver;

import java.util.Map;
import java.util.stream.Collectors;

public class Database {
	private static Database instance;
	private SurrealConnection conn;
	private SyncSurrealDriver driver;

	private Database() {
		conn = new SurrealWebSocketConnection("localhost", 8000, false);
		conn.connect(10);
		driver = new SyncSurrealDriver(conn);
		driver.signIn("root", "root");
		driver.use("chatapp", "server");
	}

	public void insertMessage(MessageModel model) {
		driver.create("message", model);
	}

	public void disconnect() {
		conn.disconnect();
	}

	public static Database getInstance() {
		if(instance == null) {
			instance = new Database();
		}
		return instance;
	}
}
