plugins {
    alias(libs.plugins.android.application)
    //id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.vecinapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.vecinapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.ui.text.android)
    implementation(libs.play.services.maps)
    implementation(libs.recyclerview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


    implementation ("com.google.android.material:material:1.9.0")
    implementation ("androidx.recyclerview:recyclerview:1.2.1")
    implementation ("androidx.cardview:cardview:1.0.0")

    //firebase
    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))

    // When using the BoM, don't specify versions in Firebase dependencies
    implementation ("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")
    // Add the dependencies for any other desired Firebase products
    // https://firebase.google.com/docs/android/setup#available-libraries

    // MAPA OSM
    implementation ("org.osmdroid:osmdroid-android:6.1.11")
//    implementation 'org.osmdroid:osmdroid-wms:6.1.11'
//    implementation 'org.osmdroid:osmdroid-mapsforge:(INSERT_VERSION_HERE)'
//    implementation 'org.osmdroid:osmdroid-geopackage:(INSERT_VERSION_HERE)'


}