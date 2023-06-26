package chatapp.common.message;

public enum MessageKind {
	Login,
	Disconnect,

	ChatRequested,
	ChatAccepted,
	ChatMessage,

	ServerError,
	LoginFailedError,
}
