plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.19.0"
}

group = "io.github.gt-yan"
version = "1.0.1"

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        // Use the locally installed IDEA to avoid downloading the platform
        // (JetBrains CDN may be unreachable in restricted environments).
        // For a fully remote build replace this with:
        //   intellijIdeaUltimate("2026.2.3")
        local("E:\\Program Files\\JetBrains\\IntelliJ IDEA 2023.3.1")
        bundledPlugin("com.intellij.javaee")
        bundledPlugin("com.intellij.javaee.app.servers.integration")
    }
}

// IDEA 2023.3 runs on JBR 17; target that bytecode level so the plugin loads
// on every supported platform from 2023.1 up to 2026.x.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

intellijPlatform {
    pluginConfiguration {
        name = "Resin (Community)"
        version = provider { project.version.toString() }

        ideaVersion {
            sinceBuild = "232"
            untilBuild = "263.*"
        }
    }

    // Code signing is required by JetBrains Marketplace.
    // Set these environment variables before running `signPlugin`:
    //   CERTIFICATE_CHAIN, PRIVATE_KEY, PRIVATE_KEY_PASSWORD
    signing {
        certificateChain = providers.environmentVariable("CERTIFICATE_CHAIN")
        privateKey = providers.environmentVariable("PRIVATE_KEY")
        password = providers.environmentVariable("PRIVATE_KEY_PASSWORD")
    }

    // `publishPlugin` uploads the signed archive to JetBrains Marketplace.
    // Requires the PUBLISH_TOKEN environment variable.
    publishing {
        token = providers.environmentVariable("PUBLISH_TOKEN")
    }
}
