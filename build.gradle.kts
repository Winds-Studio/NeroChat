plugins {
    `java-library`
    `maven-publish`
    id("com.gradleup.shadow") version "8.3.0"
}

group = "me.softik.nerochat"
version = "3.0.0-SNAPSHOT"
description = "An advanced chat plugin for survival/anarchy servers."
var url = "https://github.com/xGinko/NeroChat"

repositories {
    mavenCentral()

    maven {
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }

    maven {
        url = uri("https://repo.codemc.org/repository/maven-public")
    }

    maven {
        url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }

    maven {
        url = uri("https://ci.pluginwiki.us/plugin/repository/everything/")
    }
}

dependencies {
    compileOnly(libs.com.destroystokyo.paper.paper.api)

    api(libs.org.bstats.bstats.bukkit)
    api(libs.com.github.thatsmusic99.configurationmaster.api)
    // FuzzyWuzzy for string comparison
    api(libs.me.xdrop.fuzzywuzzy)
    // Fast Caching
    api(libs.com.github.ben.manes.caffeine.caffeine)
    api(libs.com.ibm.icu.icu4j)

    // Adventure API
    api(libs.net.kyori.adventure.text.minimessage)
    api(libs.net.kyori.adventure.platform.bukkit)
    api(libs.net.kyori.adventure.text.serializer.gson)

    compileOnly(libs.com.google.code.findbugs.jsr305)
    compileOnly(libs.me.clip.placeholderapi)
    compileOnly(libs.org.projectlombok.lombok)
    annotationProcessor(libs.org.projectlombok.lombok)
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    build.configure {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveFileName = "${project.name}-${project.version}.${archiveExtension.get()}"
        exclude("META-INF/**")
        relocate("org.bstats", "me.softik.nerochat.shade.bstats")
        relocate("com.github.benmanes.caffeine", "me.softik.nerochat.shade.caffeine")
    }

    processResources {
        filesMatching("**/plugin.yml") {
            expand(
                "version" to project.version,
                "description" to project.description,
                "url" to url
            )
        }
    }
}