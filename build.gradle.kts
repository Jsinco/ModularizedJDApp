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
    }

    dependencies {
        implementation("net.dv8tion:JDA:5.0.0-beta.24")
        implementation("club.minnced:discord-webhooks:0.8.4")
        // JDA's Logger
        implementation("org.slf4j:slf4j-simple:1.7.30")
        implementation("org.slf4j:slf4j-api:1.7.25")

        // Jetbrains Annotations
        implementation("org.jetbrains:annotations:25.0.0")
    }
}