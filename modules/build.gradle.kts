import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    id("com.gradleup.shadow") version "8.3.2"
}

repositories {
    maven("https://jitpack.io")
}

dependencies {
    implementation(project(":framework"))
    implementation("com.github.Jsinco:AbstractJavaFileLib:2.2")
}

tasks {

    // Run me with `gradlew build`
    build {
        dependsOn(shadowJar)
    }

    processResources {
        outputs.upToDateWhen { false }
        println(project.version.toString())
        filter<ReplaceTokens>(mapOf(
            "tokens" to mapOf("version" to project.version.toString()),
            "beginToken" to "\${",
            "endToken" to "}"
        ))
    }

    shadowJar {
        archiveClassifier.set("")
    }

    jar {
        enabled = false
    }
}