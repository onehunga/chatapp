plugins {
	id("chatapp.java-application-conventions")
}

dependencies {
	implementation("com.google.code.gson:gson:2.10.1")

	implementation("org.java-websocket:Java-WebSocket:1.5.3")
	implementation("com.surrealdb:surrealdb-driver:0.1.0")

	implementation(project(":common"))
}

application {
	mainClass.set("chatapp.server.Server")
}

tasks.named<JavaExec>("run") {
	standardInput = System.`in`
}
