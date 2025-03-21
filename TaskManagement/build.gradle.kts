plugins {
	java
	id("org.springframework.boot") version "3.2.3"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_19
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-aop")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("org.springframework.boot:spring-boot-starter-security")
//	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("javax.servlet:javax.servlet-api:4.0.1")
//	implementation("redis.clients:jedis:5.1.5")
	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
	implementation("io.jsonwebtoken:jjwt-jackson:0.11.5")
	implementation("mysql:mysql-connector-java:8.0.33")
	implementation("org.apache.commons:commons-lang3:3.17.0")
	implementation("jakarta.validation:jakarta.validation-api:3.0.2")
	implementation("org.hibernate.validator:hibernate-validator:8.0.1.Final")
	implementation("org.glassfish:jakarta.el:4.0.2")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
	runtimeOnly("com.h2database:h2")
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	implementation("org.mapstruct:mapstruct:1.5.3.Final")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.5.3.Final")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
