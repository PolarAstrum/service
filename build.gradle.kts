import io.izzel.taboolib.gradle.Basic
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    id("io.izzel.taboolib") version "2.0.22"
    kotlin("jvm") version "2.0.0"
    id("net.mamoe.mirai-console") version "2.16.0"
}

taboolib {
    env {
        // 安装模块
        install(Basic)
    }
    version {
        taboolib = "6.2.2"
        skipKotlinRelocate = true
    }
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.apache.commons:commons-lang3:3.5")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
