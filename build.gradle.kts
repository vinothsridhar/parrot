plugins {
	id("org.springframework.boot") version "3.3.1"
	id("io.spring.dependency-management") version "1.1.5"
	kotlin("jvm") version "1.9.24"
	kotlin("plugin.spring") version "1.9.24"
}

group = "ai.sridhar"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/milestone") }
}

extra["springAiVersion"] = "1.0.0-M1"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.flywaydb:flyway-core")
	implementation("org.flywaydb:flyway-database-postgresql")
	implementation("org.springframework.ai:spring-ai-openai-spring-boot-starter")
	implementation("org.springframework.ai:spring-ai-pgvector-store-spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-jetty")
	implementation("org.springframework.ai:spring-ai-pdf-document-reader")
	implementation("org.springframework.ai:spring-ai-tika-document-reader")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:postgresql")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

configurations {
	all {
		exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
	}
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.ai:spring-ai-bom:${property("springAiVersion")}")
	}
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.getByName<Jar>("jar") {
	enabled = false
}

tasks.register<Copy>("processFrontendResources") {
	// Directory containing the artifacts produced by the frontend project
	val frontendProjectBuildDir = "${projectDir}/src/main/dashboard"
	val frontendBuildDir = file("${frontendProjectBuildDir}/build")
	// Directory where the frontend artifacts must be copied to be packaged alltogether with the backend by the 'war'
	// plugin.
	val frontendResourcesDir = file("${projectDir}/src/main/resources/static")

	group = "Frontend"
	description = "Process frontend resources"

	from(frontendBuildDir)
	into(frontendResourcesDir)
}

tasks.named<Task>("processResources") {
	dependsOn("processFrontendResources")
}