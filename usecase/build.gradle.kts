plugins {
    alias(libs.plugins.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    implementation(project(":entity"))
    implementation("javax.inject:javax.inject:1")
    implementation(libs.coroutines.core)
}
