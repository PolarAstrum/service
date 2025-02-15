import io.izzel.taboolib.gradle.App
import io.izzel.taboolib.gradle.Basic
import io.izzel.taboolib.gradle.CommandHelper
import io.izzel.taboolib.gradle.Database
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
        install(Basic, Database, CommandHelper)
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
    compileOnly("org.ow2.asm:asm:9.6")
    compileOnly("org.ow2.asm:asm-util:9.6")
    compileOnly("org.ow2.asm:asm-commons:9.6")
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
