import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation(project(":framework"))
    implementation("com.github.Jsinco:canvas-api:2.0.2") // Canvas integration
    implementation("io.github.furstenheim:copy_down:1.0") // HTML to markdown
}

tasks {
    // Run me with `gradlew build`
    build {
        dependsOn(shadowJar)
    }

//    processResources {
//        // check if file is not yaml
//        if (file("src/main/resources").exists()) {
//            from("src/main/resources") {
//                exclude("**/*.yml")
//            }
//        }
//        outputs.upToDateWhen { false }
//        filter<ReplaceTokens>(mapOf(
//            "tokens" to mapOf("version" to project.version.toString()),
//            "beginToken" to "\${",
//            "endToken" to "}"
//        ))
//    }

    shadowJar {
        dependencies {

        }
        archiveBaseName.set(project.rootProject.name)
        archiveClassifier.set("")
    }

    jar {
        manifest {
            attributes(
                "Main-Class" to "dev.jsinco.discord.modules.Main",
            )
        }
        enabled = false
    }
}