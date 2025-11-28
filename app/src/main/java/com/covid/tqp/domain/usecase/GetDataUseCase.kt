package com.covid.tqp.domain.usecase

import com.covid.tqp.data.model.CovidDataResponse
import com.covid.tqp.domain.repository.DataRepository
import javax.inject.Inject

/**
 * Caso de uso para obtener datos de COVID.
 *
 * Esta clase encapsula la lógica para obtener los datos desde [DataRepository].
 * Simplifica la interacción del ViewModel con el repositorio de datos, proporcionando
 * un punto de entrada claro para esta acción específica.
 *
 * @param dataRepository El repositorio desde el cual se obtendrán los datos.
 */
class GetDataUseCase @Inject constructor(
    private val dataRepository: DataRepository
) {
    /**
     * Ejecuta el caso de uso.
     *
     * Permite llamar a la clase como si fuera una función. Delega la llamada
     * al método `getCovidData` del repositorio.
     *
     * @param country El nombre del país para filtrar los datos (opcional).
     * @param date La fecha para filtrar los datos (opcional).
     * @param type El tipo de datos a solicitar (e.g., "cases", "deaths") (opcional).
     * @return Una lista de [CovidDataResponse] que coincide con los filtros.
     */
    suspend operator fun invoke(country: String? = null, date: String? = null, type: String? = null): List<CovidDataResponse> {
        return dataRepository.getCovidData(country, date, type)
    }
}