package com.codesdfc.backend_uni_stay.repository;

import com.codesdfc.backend_uni_stay.model.Alojamiento;
import com.codesdfc.backend_uni_stay.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlojamientoRepository extends JpaRepository<Alojamiento, Long> {

    // Buscar alojamientos por nombre (búsqueda parcial)
    List<Alojamiento> findByNombreContainingIgnoreCase(String nombre);

    // Buscar alojamientos por rango de precio
    List<Alojamiento> findByPrecioBetween(Double precioMin, Double precioMax);

    // Buscar alojamientos por distancia máxima
    List<Alojamiento> findByDistanciaLessThanEqual(Double distanciaMax);

    // Buscar alojamientos por número mínimo de habitaciones
    List<Alojamiento> findByHabitacionesGreaterThanEqual(Integer minHabitaciones);

    // Buscar alojamientos por calificación mínima
    List<Alojamiento> findByCalificacionGreaterThanEqual(Double minCalificacion);

    // Buscar alojamientos por disponibilidad
    List<Alojamiento> findByDisponibilidad(String disponibilidad);

    // Búsqueda avanzada con múltiples criterios
    @Query("SELECT a FROM Alojamiento a WHERE " +
            "(:nombre IS NULL OR LOWER(a.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
            "(:precioMin IS NULL OR a.precio >= :precioMin) AND " +
            "(:precioMax IS NULL OR a.precio <= :precioMax) AND " +
            "(:distanciaMax IS NULL OR a.distancia <= :distanciaMax) AND " +
            "(:minHabitaciones IS NULL OR a.habitaciones >= :minHabitaciones) AND " +
            "(:minCalificacion IS NULL OR a.calificacion >= :minCalificacion)")
    List<Alojamiento> buscarAlojamientosConFiltros(
            @Param("nombre") String nombre,
            @Param("precioMin") Double precioMin,
            @Param("precioMax") Double precioMax,
            @Param("distanciaMax") Double distanciaMax,
            @Param("minHabitaciones") Integer minHabitaciones,
            @Param("minCalificacion") Double minCalificacion);

    // Buscar alojamientos que contengan una característica específica
    @Query("SELECT a FROM Alojamiento a JOIN a.caracteristicas c WHERE c = :caracteristica")
    List<Alojamiento> findByCaracteristica(@Param("caracteristica") String caracteristica);

    // Ordenar alojamientos por precio ascendente
    List<Alojamiento> findAllByOrderByPrecioAsc();

    // Ordenar alojamientos por precio descendente
    List<Alojamiento> findAllByOrderByPrecioDesc();

    // Ordenar alojamientos por calificación descendente
    List<Alojamiento> findAllByOrderByCalificacionDesc();

    // Nuevo método para buscar alojamientos por ofertante
    List<Alojamiento> findByPublicador(Usuario publicador);

    List<Alojamiento> findAllByOrderByFechaPublicacionDesc();
    
}