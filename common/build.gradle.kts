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

    // kapt와 annotation-api는 빌드 시점에만 필요하므로 그대로 유지
    kapt("com.querydsl:querydsl-apt:5.1.0:jakarta")
    kapt("jakarta.persistence:jakarta.persistence-api")
    kapt("jakarta.annotation:jakarta.annotation-api")
    
    implementation("org.springframework.boot:spring-boot-starter-web")
    runtimeOnly("com.h2database:h2")

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
}

tasks.jar {
    enabled = true
    archiveClassifier.set("")
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}
