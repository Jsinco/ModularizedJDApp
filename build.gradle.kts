plugins {
    java
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "idea")

    group = "dev.jsinco.discord"
    version = "1.0-SNAPSHOT"



    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }

    java {
        toolchain.languageVersion = JavaLanguageVersion.of(17) // im going with java 16
    }

    repositories {
        mavenCentral()
        maven("https://storehouse.okaeri.eu/repository/maven-public/")
    }

    dependencies {
        implementation("net.dv8tion:JDA:5.0.0-beta.24") {
            exclude("org.slf4j", "slf4j-api")
        }
        implementation("club.minnced:discord-webhooks:0.8.4") {
            exclude("org.slf4j", "slf4j-api")
        }
        // Logger
        implementation("org.apache.logging.log4j:log4j-core:2.24.1")
        implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.24.1")
        implementation("org.apache.logging.log4j:log4j-api:2.24.1")

        // Jetbrains Annotations
        implementation("org.jetbrains:annotations:25.0.0")

        // Google guava/gson
        implementation("com.google.guava:guava:33.3.1-jre")
        implementation("com.google.code.gson:gson:2.10.1")

        // Lombok
        compileOnly("org.projectlombok:lombok:1.18.30")
        annotationProcessor("org.projectlombok:lombok:1.18.30")

        implementation("eu.okaeri:okaeri-configs-yaml-snakeyaml:5.0.5")
    }
}