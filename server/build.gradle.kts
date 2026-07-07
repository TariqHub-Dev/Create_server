
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(ktorLibs.plugins.ktor)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlinx.rpc)
}


application {
    mainClass = "io.ktor.server.cio.EngineMain"
}

kotlin {
    jvmToolchain(21)
}
dependencies {
    implementation(ktorLibs.serialization.kotlinx.json)
    implementation(ktorLibs.server.cio)
    implementation(ktorLibs.server.config.yaml)
    implementation(ktorLibs.server.contentNegotiation)
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.resources)
    implementation(libs.kotlinx.rpc.server)
    implementation(libs.logback.classic)
    implementation(libs.openfolder.kotlinAsyncapiKtor)
    implementation(project(":core"))

    // Exposed Core & DAO untuk manajemen SQL ala Kotlin
    implementation("org.jetbrains.exposed:exposed-core:0.50.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.50.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.50.0")

    // Driver untuk H2 Database
    implementation("com.h2database:h2:2.2.224")

    testImplementation(kotlin("test"))
    testImplementation(ktorLibs.server.testHost)
    testImplementation(libs.kotlinx.rpc.client)

    implementation("org.postgresql:postgresql:42.6.0")
}
