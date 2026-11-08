rootProject.name = "idea-resin-plugin"

plugins {
    // Allows Gradle to auto-download the JDK 21 toolchain needed to compile
    // plugins targeting IntelliJ IDEA 2026.x.
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
