plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.typesafe.akka:akka-actor-typed_2.13:2.6.18")
    implementation("org.slf4j:slf4j-simple:2.0.0-alpha6")
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("org.eclipse.jetty:jetty-server:11.0.8")
    implementation("org.eclipse.jetty:jetty-servlet:11.0.8")
    testImplementation("com.typesafe.akka:akka-actor-testkit-typed_2.13:2.6.18")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.2.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.2.0")
    //testRuntime("org.junit.platform:junit-platform-console:1.2.0")
}

application {
    // Define the main class for the application.
    mainClass.set("b01_actors.App")
    // mainClass.set("b01_actors.server.MiniServer")
    
}

tasks.withType<Test> {
    useJUnitPlatform()
}

