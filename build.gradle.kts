plugins {
    kotlin("jvm") version "2.3.0"
    kotlin("plugin.serialization") version "2.3.0"
    application
}

repositories { mavenCentral() }

dependencies {
    val akkaVersion = "2.6.21"

    // Akka Typed
    implementation(platform("com.typesafe.akka:akka-bom_2.13:$akkaVersion"))
    implementation("com.typesafe.akka:akka-actor-typed_2.13")

    // Rabbitmq
    implementation("com.rabbitmq:amqp-client:5.20.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("ch.qos.logback:logback-classic:1.5.35")

    // Tests
    testImplementation("com.typesafe.akka:akka-actor-testkit-typed_2.13")
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

application {
    mainClass.set("com.example.warehouse.MainKt")
    applicationName = "app"
}

tasks.test { useJUnitPlatform() }
kotlin { jvmToolchain(17) }