plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.jpa")
    id("java-library")
    id("maven-publish")
}

group = "com.github.kjylab"
version = "1.0.0"

dependencies {
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("com.querydsl:querydsl-jpa:5.1.0:jakarta")

    implementation("org.springframework.boot:spring-boot-starter-web")
    runtimeOnly("com.h2database:h2")

    kapt("com.querydsl:querydsl-apt:5.1.0:jakarta")
    kapt("jakarta.persistence:jakarta.persistence-api")
    kapt("jakarta.annotation:jakarta.annotation-api")
}

configurations.all {
    resolutionStrategy {
        // "누가 뭐래도 이 버전과 이 규격(jakarta)만 사용해라!"라고 강제합니다.
        force("com.querydsl:querydsl-jpa:5.1.0:jakarta")
        force("com.querydsl:querydsl-apt:5.1.0:jakarta")
    }
}

sourceSets {
    main {
        kotlin.srcDir("build/generated/source/kapt/main")
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "my-msa-common"
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/kjylab/my-msa-common")
            credentials {
                username = System.getenv("GITHUB_ACTOR") ?: ""
                password = System.getenv("GITHUB_TOKEN") ?: ""
            }
        }
    }
}

tasks.jar {
    enabled = true
    archiveClassifier.set("")
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}