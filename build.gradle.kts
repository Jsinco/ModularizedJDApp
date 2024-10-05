plugins {
    java
}

tasks.jar {
    enabled = false
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "idea")

    group = "dev.jsinco.discord"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
        maven("https://jitpack.io")
    }

    dependencies {
        implementation("net.dv8tion:JDA:5.0.0-beta.24") {
            exclude("org.slf4j", "slf4j-api")
        }
        implementation("club.minnced:discord-webhooks:0.8.4") {
            exclude("org.slf4j", "slf4j-api")
        }
        // JDA's Logger
        //implementation("org.slf4j:slf4j-simple:2.0.0")
        //implementation("org.slf4j:slf4j-api:2.0.0")
        implementation("org.apache.logging.log4j:log4j-core:2.24.1")
        implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.24.1")
        implementation("org.apache.logging.log4j:log4j-api:2.24.1")
        // Jetbrains Annotations
        implementation("org.jetbrains:annotations:25.0.0")
        implementation("com.github.Jsinco:AbstractJavaFileLib:2.2")
    }
}