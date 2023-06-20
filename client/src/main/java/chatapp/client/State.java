package chatapp.client;

public class State {
	public String username;
	public String partner;
	public boolean loggedIn;
	public boolean appQuit;

	public State() {
		this.username = "";
		this.loggedIn = false;
		this.appQuit = false;
	}
}
