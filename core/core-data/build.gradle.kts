plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "app.kaup.core.data"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    implementation(project(":shared-kmp"))
    
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    
    // Preferences DataStore
    implementation(libs.androidx.datastore.preferences)

    // Coroutines for DAO flows
    implementation(libs.kotlinx.coroutines.core)

    // JSR-330 for @Inject / @Singleton
    implementation("javax.inject:javax.inject:1")
}
