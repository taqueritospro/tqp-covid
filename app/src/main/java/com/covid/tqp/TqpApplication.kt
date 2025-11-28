package com.covid.tqp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Clase Application principal para la aplicación Covid-19.1
 * Esta clase está anotada con [HiltAndroidApp] para habilitar la inyección de dependencias con Hilt.
 * Debe registrarse en AndroidManifest.xml (`android:name=".TqpApplication"`).
 */
@HiltAndroidApp
class TqpApplication : Application()
