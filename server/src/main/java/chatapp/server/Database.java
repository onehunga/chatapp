package chatapp.server;

import chatapp.server.models.MessageModel;
import chatapp.server.models.UserModel;
import com.surrealdb.connection.SurrealConnection;
import com.surrealdb.connection.SurrealWebSocketConnection;
import com.surrealdb.driver.SyncSurrealDriver;

import java.util.List;
import java.util.Map;

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

	public List<MessageModel> getMessages(String username) {
		String query = "SELECT * FROM message WHERE receiver.id == user:" +
						username +
						" && time > receiver.time ORDER BY time;";

		return driver.query(query, Map.of(), MessageModel.class).get(0).getResult();
	}

	public void updateUserTime(String username) {
		driver.query(new UserModel(username).getUpdateQuery(), Map.of(), UserModel.class);
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
