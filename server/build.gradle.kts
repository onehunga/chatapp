plugins {
	id("chatapp.java-application-conventions")
}

dependencies {
	implementation("com.google.code.gson:gson:2.10.1")

	implementation(project(":common"))
}

application {
	mainClass.set("chatapp.server.Server")
}

tasks.named<JavaExec>("run") {
	standardInput = System.`in`
}
