plugins {
  id("com.android.application")
  kotlin("android")
}

android {
  compileSdk = 28

  defaultConfig {
    applicationId = "dev.joxit.androidapp.audiorecorder"
    minSdk = 26
    targetSdk = 31
    versionCode = 1
    versionName = "1.0.0"

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
  kotlinOptions {
    jvmTarget = "1.8"
  }
  buildFeatures {
    viewBinding = true
  }
  buildToolsVersion = "31.0.0"
}

dependencies {

  implementation("androidx.appcompat:appcompat:${property("version.androidx.appcompat")}")
  implementation("com.google.android.material:material:${property("version.android.material")}")
  implementation("androidx.constraintlayout:constraintlayout:${property("version.androidx.constraintlayout")}")
  implementation("androidx.lifecycle:lifecycle-livedata-ktx:${property("version.androidx.lifecycle")}")
  implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${property("version.androidx.lifecycle")}")
  implementation("androidx.lifecycle:lifecycle-common-java8:${property("version.androidx.lifecycle")}")
  implementation("androidx.viewpager2:viewpager2:${property("version.androidx.viewpager2")}")
  implementation("com.mikepenz:materialdrawer:${property("version.mikepenz.materialdrawer")}")

  implementation("androidx.navigation:navigation-fragment-ktx:${property("version.androidx.navigation")}")
  implementation("androidx.navigation:navigation-ui-ktx:${property("version.androidx.navigation")}")
  testImplementation("junit:junit:${property("version.junit")}")
  androidTestImplementation("androidx.test.ext:junit:${property("version.androidx.test.ext")}")
  androidTestImplementation("androidx.test.espresso:espresso-core:${property("version.androidx.test.espresso")}")
}