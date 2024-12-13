/*
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

@file:Suppress("UnstableApiUsage")

import com.google.protobuf.gradle.id
import org.eclipse.kuksa.version.SemanticVersion
import org.eclipse.kuksa.version.VERSION_FILE_DEFAULT_PATH_KEY
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("com.google.protobuf")
    alias(libs.plugins.dokka)
    publish
}

val versionPath = rootProject.ext[VERSION_FILE_DEFAULT_PATH_KEY] as String
val semanticVersion = SemanticVersion(versionPath)
version = semanticVersion.versionName
group = "org.eclipse.kuksa"

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()

    kotlin {
        compilerOptions {
            // https://youtrack.jetbrains.com/issue/KT-48678/Coroutine-debugger-disable-was-optimised-out-compiler-feature
            // We don't want local variables to be optimized out while debugging into tests
            freeCompilerArgs.add("-Xdebug")
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xdebug")
    }
}

publish {
    mavenPublicationName = "release"
    componentName = "java"
    description = "Android Connectivity Library for the KUKSA Databroker"
}

tasks.register("javadocJar", Jar::class) {
    dependsOn("dokkaHtml")

    val buildDir = layout.buildDirectory.get()
    from("$buildDir/dokka/html")
    archiveClassifier.set("javadoc")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11

    withJavadocJar() // needs to be called after tasks.register("javadocJar")
    withSourcesJar()
}

dependencies {
    api(project(":vss-core")) // Models are exposed

    testImplementation(project(":test-core"))

    // needs to be api as long as we expose ProtoBuf specific objects
    api(libs.grpc.protobuf)

    implementation(kotlin("reflect"))

    implementation(libs.grpc.okhttp)
    implementation(libs.grpc.stub)
    implementation(libs.tomcat.annotations)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotest)
    testImplementation(libs.mockk)

    testImplementation(libs.docker.java.core)
    testImplementation(libs.docker.java.transport.httpclient5)
}

protobuf {
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }
    plugins {
        id("grpc") {
            artifact = libs.grpc.protoc.java.gen.get().toString()
        }
        generateProtoTasks {
            all().forEach {
                it.builtins {
                    named("java") {
                        option("lite")
                    }
                }
                it.plugins {
                    create("grpc") {
                        option("lite")
                    }
                }
            }
        }
    }
}