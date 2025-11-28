package com.covid.tqp.data.repository

import com.covid.tqp.data.model.CovidDataResponse
import com.covid.tqp.data.source.remote.RemoteDataSource
import com.covid.tqp.domain.repository.DataRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación del [DataRepository] que obtiene datos de COVID desde una fuente de datos remota.
 *
 * Esta clase es un Singleton, lo que significa que solo existirá una instancia de ella
 * durante el ciclo de vida de la aplicación, gestionada por Hilt.
 *
 * @param remoteDataSource La fuente de datos remota (API) de donde se obtendrán los datos.
 */
@Singleton
class DataRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : DataRepository {
    /**
     * Obtiene los datos de COVID delegando la llamada a la [RemoteDataSource].
     *
     * @param country El nombre del país a consultar.
     * @param date La fecha específica a consultar.
     * @param type El tipo de datos a solicitar ("cases" o "deaths").
     * @return Una lista de [CovidDataResponse] obtenida desde la fuente remota.
     */
    override suspend fun getCovidData(country: String?, date: String?, type: String?): List<CovidDataResponse> {
        return remoteDataSource.getCovidData(country = country, date = date, type = type)
    }
}