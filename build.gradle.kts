import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

object Versions {
	const val SPRING_BOOT_VERSION = "2.7.5"

	const val DGS_PLATFORM_DEPENDENCIES_VERSION = "5.3.0"
	const val DGS_SPRING_BOOT_STARTER = "5.3.0"
	const val DGS_EXTENDED_SCALARS_VERSION = "5.3.0"

	const val QUERYDSL_VERSION = "5.0.0"

	const val KOTLIN_LOGGING_JVM_VERSION = "2.1.21"
	const val JACKSON_MODULE_KOTLIN_VERSION = "2.13.0"

	const val MARIA_DB_JAVA_CLIENT_VERSION = "2.7.4"

	const val GRAPHQL_VOYAGER_VERSION = "11.1.0"
	const val GRAPHQL_PLAYGROUND_VERSION = "11.1.0"

	const val KOTEST_VERSION = "5.0.0"
	const val KOTEST_SPRING_VERSION = "1.1.2"
	const val MOCKK_VERSION = "1.13.3"
	const val TEST_CONTAINER_VERSION = "1.19.7"
	const val KOTEST_TEST_CONTAINER_VERSION = "1.3.3"
}

plugins {

	val kotlinVersion = "1.6.10"
	val springBootVersion = "2.7.5"
	val springDependencyManagementVersion = "1.0.11.RELEASE"
	val dgsCodegenVersion = "5.7.1"

	id("org.springframework.boot") version springBootVersion
	id("io.spring.dependency-management") version springDependencyManagementVersion
	id("com.netflix.dgs.codegen") version dgsCodegenVersion

	kotlin("jvm") version kotlinVersion
	kotlin("plugin.spring") version kotlinVersion
	kotlin("plugin.jpa") version kotlinVersion

	kotlin("kapt") version kotlinVersion
	kotlin("plugin.allopen") version kotlinVersion

	idea
}

group = "com.kotlin"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_11
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// starter
	implementation("org.springframework.boot:spring-boot-starter-data-jpa:${Versions.SPRING_BOOT_VERSION}")
	implementation("org.springframework.boot:spring-boot-starter-web:${Versions.SPRING_BOOT_VERSION}")

	// kotlin base
	implementation("io.github.microutils:kotlin-logging-jvm:${Versions.KOTLIN_LOGGING_JVM_VERSION}")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	// jackson
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${Versions.JACKSON_MODULE_KOTLIN_VERSION}")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

	// graphql (dgs)
	implementation(platform("com.netflix.graphql.dgs:graphql-dgs-platform-dependencies:${Versions.DGS_PLATFORM_DEPENDENCIES_VERSION}"))
	implementation("com.netflix.graphql.dgs:graphql-dgs-spring-boot-starter:${Versions.DGS_SPRING_BOOT_STARTER}")
	implementation("com.netflix.graphql.dgs:graphql-dgs-extended-scalars:${Versions.DGS_EXTENDED_SCALARS_VERSION}")

	// querydsl
	implementation("com.querydsl:querydsl-core:${Versions.QUERYDSL_VERSION}")
	implementation("com.querydsl:querydsl-jpa:${Versions.QUERYDSL_VERSION}")
	kapt("com.querydsl:querydsl-apt:${Versions.QUERYDSL_VERSION}:jpa")
	kapt("org.springframework.boot:spring-boot-configuration-processor:${Versions.SPRING_BOOT_VERSION}")

	// graphql starter
	implementation("com.graphql-java-kickstart:voyager-spring-boot-starter:${Versions.GRAPHQL_VOYAGER_VERSION}")
	implementation("com.graphql-java-kickstart:playground-spring-boot-starter:${Versions.GRAPHQL_PLAYGROUND_VERSION}")

	// mariadb java client
	implementation("org.mariadb.jdbc:mariadb-java-client:${Versions.MARIA_DB_JAVA_CLIENT_VERSION}")

	// test starter
	testImplementation("org.springframework.boot:spring-boot-starter-test:${Versions.SPRING_BOOT_VERSION}")

	// kotest
	testImplementation("io.kotest:kotest-runner-junit5:${Versions.KOTEST_VERSION}")
	testImplementation("io.kotest:kotest-assertions-core:${Versions.KOTEST_VERSION}")
	testImplementation("io.kotest.extensions:kotest-extensions-spring:${Versions.KOTEST_SPRING_VERSION}")

	// mockk
	testImplementation("io.mockk:mockk:${Versions.MOCKK_VERSION}")

	// spring security test
	testImplementation("org.springframework.security:spring-security-test:${Versions.SPRING_BOOT_VERSION}")

	// testcontainers
	testImplementation("org.testcontainers:testcontainers:${Versions.TEST_CONTAINER_VERSION}")
	testImplementation("io.kotest.extensions:kotest-extensions-testcontainers:${Versions.KOTEST_TEST_CONTAINER_VERSION}")
	testImplementation("org.testcontainers:mariadb:${Versions.TEST_CONTAINER_VERSION}")
}

allOpen {
	annotation("javax.persistence.Entity")
}

idea {
	module {
		val kaptMain = file("build/generated/source/kapt/main")
		sourceDirs.add(kaptMain)
		generatedSourceDirs.add(kaptMain)
	}
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()

//    testLogging {
//        events(
//            org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
//            org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_ERROR,
//            org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
//        )
//
//        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
//        showExceptions = true
//        showCauses = true
//        showStackTraces = true
//    }
}

tasks.generateJava {
	schemaPaths.add("${projectDir}/src/main/resources/schema")
	packageName = "com.kotlin.test.generated"

	generateClientv2 = true
    language = "java"

	typeMapping = mutableMapOf(
			"Long" to "java.lang.Long",
			"Short" to "java.lang.Short"
	)
}

