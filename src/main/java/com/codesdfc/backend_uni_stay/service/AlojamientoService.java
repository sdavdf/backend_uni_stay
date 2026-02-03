package com.codesdfc.backend_uni_stay.service;

import com.codesdfc.backend_uni_stay.dto.AlojamientoDTO;
import com.codesdfc.backend_uni_stay.dto.AlojamientoFilterDTO;
import com.codesdfc.backend_uni_stay.dto.AlojamientoRequest;
import com.codesdfc.backend_uni_stay.model.Alojamiento;
import com.codesdfc.backend_uni_stay.model.Usuario;
import com.codesdfc.backend_uni_stay.repository.AlojamientoRepository;
import com.codesdfc.backend_uni_stay.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlojamientoService {

    private final AlojamientoRepository alojamientoRepository;
    private final UsuarioRepository usuarioRepository;

    /* ===================== CREAR / ACTUALIZAR ===================== */

    public AlojamientoDTO crearAlojamiento(AlojamientoRequest request, String emailUsuario) {
        Usuario publicador = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Alojamiento alojamiento = new Alojamiento();
        alojamiento.setPublicador(publicador);

        return guardarAlojamientoDesdeRequest(alojamiento, request);
    }

    public AlojamientoDTO crearAlojamiento(AlojamientoRequest request) {
        Alojamiento alojamiento = new Alojamiento();
        return guardarAlojamientoDesdeRequest(alojamiento, request);
    }

    public AlojamientoDTO actualizarAlojamiento(Long id, AlojamientoRequest request, String emailUsuario) {
        Alojamiento alojamiento = alojamientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alojamiento no encontrado"));

        if (!alojamiento.getPublicador().getEmail().equals(emailUsuario)) {
            throw new RuntimeException("Solo el publicador puede modificar este alojamiento");
        }

        return guardarAlojamientoDesdeRequest(alojamiento, request);
    }

    public void eliminarAlojamiento(Long id, String emailUsuario) {
        Alojamiento alojamiento = alojamientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alojamiento no encontrado"));

        if (!alojamiento.getPublicador().getEmail().equals(emailUsuario)) {
            throw new RuntimeException("Solo el publicador puede eliminar este alojamiento");
        }

        alojamientoRepository.deleteById(id);
    }

    /* ===================== OBTENER ===================== */

    public List<AlojamientoDTO> obtenerTodos() {
        return alojamientoRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<Alojamiento> obtenerEntityPorId(Long id) {
        return alojamientoRepository.findById(id);
    }

    /**
     * Obtiene un alojamiento por ID e incrementa las visitas
     */
    public AlojamientoDTO obtenerPorId(Long id) {
        Alojamiento alojamiento = alojamientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alojamiento no encontrado"));

        alojamiento.setVisitas(
                alojamiento.getVisitas() == null ? 1 : alojamiento.getVisitas() + 1
        );

        alojamientoRepository.save(alojamiento);
        return convertToDTO(alojamiento);
    }

    public List<AlojamientoDTO> obtenerMisPublicaciones(String emailUsuario) {
        Usuario publicador = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return alojamientoRepository.findByPublicador(publicador)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /* ===================== BÚSQUEDAS ===================== */

    public List<AlojamientoDTO> buscarPorNombre(String nombre) {
        return alojamientoRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AlojamientoDTO> buscarPorRangoPrecio(Double precioMin, Double precioMax) {
        return alojamientoRepository.findByPrecioBetween(precioMin, precioMax)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AlojamientoDTO> buscarPorDistanciaMaxima(Double distanciaMax) {
        return alojamientoRepository.findByDistanciaLessThanEqual(distanciaMax)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AlojamientoDTO> buscarPorMinHabitaciones(Integer minHabitaciones) {
        return alojamientoRepository.findByHabitacionesGreaterThanEqual(minHabitaciones)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AlojamientoDTO> buscarPorCalificacionMinima(Double minCalificacion) {
        return alojamientoRepository.findByCalificacionGreaterThanEqual(minCalificacion)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AlojamientoDTO> buscarConFiltros(
            String nombre,
            Double precioMin,
            Double precioMax,
            Double distanciaMax,
            Integer minHabitaciones,
            Double minCalificacion
    ) {
        return alojamientoRepository.buscarAlojamientosConFiltros(
                        nombre, precioMin, precioMax, distanciaMax, minHabitaciones, minCalificacion
                )
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AlojamientoDTO> buscarPorDireccion(String texto) {
        return alojamientoRepository.buscarPorDireccion(texto)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    /* ===================== ORDENAMIENTOS ===================== */

    public List<AlojamientoDTO> ordenarPorPrecioAsc() {
        return alojamientoRepository.findAllByOrderByPrecioAsc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AlojamientoDTO> ordenarPorPrecioDesc() {
        return alojamientoRepository.findAllByOrderByPrecioDesc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AlojamientoDTO> ordenarPorCalificacion() {
        return alojamientoRepository.findAllByOrderByCalificacionDesc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /* ===================== RECOMENDACIONES ===================== */

    public List<AlojamientoDTO> mejoresCerca(Double lat, Double lon, Double radio) {
        return alojamientoRepository.mejoresCerca(lat, lon, radio)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AlojamientoDTO> masVisitadosCerca(
            Double lat, Double lon, Double radio
    ) {
        return alojamientoRepository.masVisitadosCerca(lat, lon, radio)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    public List<AlojamientoFilterDTO> listarRecientes() {
        return alojamientoRepository.findAllByOrderByFechaPublicacionDesc()
                .stream()
                .map(this::convertToResumenDTO)
                .collect(Collectors.toList());
    }

    /* ===================== MÉTODOS PRIVADOS ===================== */

    private AlojamientoDTO guardarAlojamientoDesdeRequest(Alojamiento alojamiento, AlojamientoRequest request) {
        alojamiento.setNombre(request.getNombre());
        alojamiento.setDescripcion(request.getDescripcion());
        alojamiento.setPrecio(request.getPrecio());
        alojamiento.setDistancia(request.getDistancia());
        alojamiento.setHabitaciones(request.getHabitaciones());
        alojamiento.setDisponibilidad(request.getDisponibilidad());
        alojamiento.setCalificacion(request.getCalificacion());
        alojamiento.setDireccion(request.getDireccion());
        alojamiento.setLatitud(request.getLatitud());
        alojamiento.setLongitud(request.getLongitud());

        // Inicializar visitas si es nuevo
        if (alojamiento.getId() == null) {
            alojamiento.setVisitas(0L);
        }

        if (request.getFotos() != null) {
            alojamiento.setFotos(request.getFotos());
        }

        if (request.getCaracteristicas() != null) {
            alojamiento.setCaracteristicas(request.getCaracteristicas());
        }

        Alojamiento saved = alojamientoRepository.save(alojamiento);
        return convertToDTO(saved);
    }

    private AlojamientoDTO convertToDTO(Alojamiento alojamiento) {
        AlojamientoDTO dto = new AlojamientoDTO();
        dto.setId(alojamiento.getId());
        dto.setNombre(alojamiento.getNombre());
        dto.setDescripcion(alojamiento.getDescripcion());
        dto.setPrecio(alojamiento.getPrecio());
        dto.setDistancia(alojamiento.getDistancia());
        dto.setHabitaciones(alojamiento.getHabitaciones());
        dto.setDisponibilidad(alojamiento.getDisponibilidad());
        dto.setCalificacion(alojamiento.getCalificacion());
        dto.setDireccion(alojamiento.getDireccion());
        dto.setLatitud(alojamiento.getLatitud());
        dto.setLongitud(alojamiento.getLongitud());
        dto.setVisitas(alojamiento.getVisitas());
        dto.setFotos(alojamiento.getFotos());
        dto.setCaracteristicas(alojamiento.getCaracteristicas());
        dto.setFecha_publicacion(alojamiento.getFechaPublicacion());

        if (alojamiento.getPublicador() != null) {
            dto.setPublicadorNombre(alojamiento.getPublicador().getNombre());
            dto.setPublicadorId(alojamiento.getPublicador().getId());
        }

        return dto;
    }

    private AlojamientoFilterDTO convertToResumenDTO(Alojamiento a) {
        AlojamientoFilterDTO dto = new AlojamientoFilterDTO();
        dto.setId(a.getId());
        dto.setNombre(a.getNombre());
        dto.setPrecio(a.getPrecio());
        dto.setDistancia(a.getDistancia());
        dto.setHabitaciones(a.getHabitaciones());
        dto.setDireccion(a.getDireccion());
        dto.setFechaPublicacion(a.getFechaPublicacion());

        if (a.getPublicador() != null) {
            dto.setPublicadorId(a.getPublicador().getId());
            dto.setPublicadorNombre(a.getPublicador().getNombre());
        }

        return dto;
    }
}
