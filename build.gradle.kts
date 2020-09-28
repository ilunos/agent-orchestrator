plugins {
    kotlin("jvm") version "1.4.10"
    kotlin("kapt") version "1.4.10"
    kotlin("plugin.allopen") version "1.4.10"
    id("com.github.johnrengelman.shadow") version "5.2.0"
    application
}

version = "0.1"
group = "com.ilunos.orchestrator"

repositories {
    mavenCentral()
    jcenter()
}

val developmentOnly = configurations.create("developmentOnly")

dependencies {
    kapt(platform("io.micronaut:micronaut-bom:2.0.1"))
    kapt("io.micronaut:micronaut-inject-java")
    kapt("io.micronaut:micronaut-validation")
    kapt("io.micronaut.security:micronaut-security-annotations")
    kapt("io.micronaut.data:micronaut-data-processor")

    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.8")

    implementation(platform("io.micronaut:micronaut-bom:2.0.1"))
    implementation("io.micronaut:micronaut-inject")
    implementation("io.micronaut:micronaut-validation")
    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut:micronaut-http-server-netty")
    implementation("io.micronaut.security:micronaut-security")
    implementation("io.micronaut.security:micronaut-security-oauth2")
    implementation("io.micronaut.security:micronaut-security-jwt")
    implementation("io.micronaut.kotlin:micronaut-kotlin-extension-functions")
    implementation("io.micronaut.data:micronaut-data-hibernate-jpa")
    implementation("javax.annotation:javax.annotation-api")

    runtimeOnly("ch.qos.logback:logback-classic")
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")
    runtimeOnly("io.micronaut.sql:micronaut-jdbc-tomcat")
    runtimeOnly("com.h2database:h2")

    kaptTest(enforcedPlatform("io.micronaut:micronaut-bom:2.0.1"))
    kaptTest("io.micronaut:micronaut-inject-java")

    testImplementation(enforcedPlatform("io.micronaut:micronaut-bom:2.0.1"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("io.micronaut.test:micronaut-test-junit5")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}


allOpen {
    annotations("io.micronaut.aop.Around", "io.micronaut.scheduling.annotation.Scheduled")
}

kapt {
    arguments {
        arg("micronaut.processing.incremental", true)
        arg("micronaut.processing.annotations", "com.ilunos.orchestrator.*")
        arg("micronaut.processing.group", "com.ilunos.orchestrator")
        arg("micronaut.processing.module", "Agent-Orchestrator")
    }
}

java {
    sourceCompatibility = JavaVersion.toVersion("13")
}

application {
    mainClassName = "com.ilunos.orchestrator.ApplicationKt"
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "13"
            javaParameters = true
        }
    }
    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "13"
            javaParameters = true
        }
    }

    shadowJar {
        mergeServiceFiles()
    }

    // use JUnit 5 platform
    test {
        useJUnitPlatform()
    }
}

tasks.withType<JavaCompile> {
    classpath += developmentOnly
    options.compilerArgs.addAll(arrayOf("-XX:TieredStopAtLevel=1", "-Dcom.sun.management.jmxremote"))
}