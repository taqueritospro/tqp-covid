package com.covid.tqp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Clase [Application] base para la aplicación.
 *
 * Anotada con [HiltAndroidApp], esta clase inicializa Hilt para la inyección de dependencias
 * en toda la aplicación. Es el punto de entrada para el grafo de dependencias de la aplicación.
 *
 * Esta clase debe estar declarada en el `AndroidManifest.xml`.
 */
@HiltAndroidApp
class TqpApplication : Application()
