package com.covid.tqp.domain.model

/**
 * Representa un modelo de datos simplificado dentro de la capa de dominio.
 *
 * Este tipo de clases se utiliza para desacoplar la lógica de negocio de los modelos
 * específicos de la capa de datos (DTOs) o de la capa de UI (modelos de vista).
 *
 * @property id El identificador único del modelo.
 * @property name El nombre asociado al modelo.
 */
data class DomainModel(
    val id: String,
    val name: String
)