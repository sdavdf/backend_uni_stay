package com.codesdfc.backend_uni_stay.controller;

import com.codesdfc.backend_uni_stay.dto.AlojamientoDTO;
import com.codesdfc.backend_uni_stay.dto.AlojamientoFilterDTO;
import com.codesdfc.backend_uni_stay.dto.AlojamientoRequest;
import com.codesdfc.backend_uni_stay.service.AlojamientoService;
import com.codesdfc.backend_uni_stay.service.ArchivoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/alojamientos")
@RequiredArgsConstructor
public class AlojamientoController {

    private final AlojamientoService alojamientoService;
    private final ArchivoService archivoService;


    // GET - Obtener todos los alojamientos
    @GetMapping
    public ResponseEntity<List<AlojamientoDTO>> obtenerTodos() {
        return ResponseEntity.ok(alojamientoService.obtenerTodos());
    }

    // GET - Obtener alojamiento por ID
    @GetMapping("/{id}")
    public ResponseEntity<AlojamientoDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(alojamientoService.obtenerPorId(id));
    }

    // POST - Crear nuevo alojamiento (solo ofertantes y admin)
    @PostMapping
    @PreAuthorize("isAuthenticated()") // ← Solo necesita estar autenticado
    public ResponseEntity<AlojamientoDTO> crearAlojamiento(
            @RequestBody AlojamientoRequest request,
            Authentication authentication) {

        String emailUsuario = authentication.getName();
        AlojamientoDTO alojamientoCreado = alojamientoService.crearAlojamiento(request, emailUsuario);
        return ResponseEntity.ok(alojamientoCreado);
    }

    // PUT - Actualizar alojamiento (solo ofertantes y admin)
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AlojamientoDTO> actualizarAlojamiento(
            @PathVariable Long id,
            @RequestBody AlojamientoRequest request,
            Authentication authentication) {

        String emailUsuario = authentication.getName();
        return ResponseEntity.ok(alojamientoService.actualizarAlojamiento(id, request, emailUsuario));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> eliminarAlojamiento(
            @PathVariable Long id,
            Authentication authentication) {

        String emailUsuario = authentication.getName();
        alojamientoService.eliminarAlojamiento(id, emailUsuario);
        return ResponseEntity.noContent().build();
    }

    // GET - Búsqueda por nombre
    @GetMapping("/buscar")
    public ResponseEntity<List<AlojamientoDTO>> buscarPorNombre(
            @RequestParam(required = false) String nombre) {
        return ResponseEntity.ok(alojamientoService.buscarPorNombre(nombre));
    }

    // GET - Búsqueda con múltiples filtros
    @GetMapping("/filtros")
    public ResponseEntity<List<AlojamientoDTO>> buscarConFiltros(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Double precioMin,
            @RequestParam(required = false) Double precioMax,
            @RequestParam(required = false) Double distanciaMax,
            @RequestParam(required = false) Integer minHabitaciones,
            @RequestParam(required = false) Double minCalificacion) {

        return ResponseEntity.ok(alojamientoService.buscarConFiltros(
                nombre, precioMin, precioMax, distanciaMax, minHabitaciones, minCalificacion));
    }

    // GET - Ordenar por precio ascendente
    @GetMapping("/ordenar/precio-asc")
    public ResponseEntity<List<AlojamientoDTO>> ordenarPorPrecioAsc() {
        return ResponseEntity.ok(alojamientoService.ordenarPorPrecioAsc());
    }

    // GET - Ordenar por precio descendente
    @GetMapping("/ordenar/precio-desc")
    public ResponseEntity<List<AlojamientoDTO>> ordenarPorPrecioDesc() {
        return ResponseEntity.ok(alojamientoService.ordenarPorPrecioDesc());
    }

    // GET - Ordenar por calificación
    @GetMapping("/ordenar/calificacion")
    public ResponseEntity<List<AlojamientoDTO>> ordenarPorCalificacion() {
        return ResponseEntity.ok(alojamientoService.ordenarPorCalificacion());
    }

    // GET - Buscar por rango de precio
    @GetMapping("/filtros/precio")
    public ResponseEntity<List<AlojamientoDTO>> buscarPorRangoPrecio(
            @RequestParam Double precioMin,
            @RequestParam Double precioMax) {
        return ResponseEntity.ok(alojamientoService.buscarPorRangoPrecio(precioMin, precioMax));
    }

    // GET - Buscar por distancia máxima
    @GetMapping("/filtros/distancia")
    public ResponseEntity<List<AlojamientoDTO>> buscarPorDistanciaMaxima(
            @RequestParam Double distanciaMax) {
        return ResponseEntity.ok(alojamientoService.buscarPorDistanciaMaxima(distanciaMax));
    }

    @GetMapping("/mis-publicaciones")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AlojamientoDTO>> obtenerMisPublicaciones(Authentication authentication) {
        String emailUsuario = authentication.getName();
        List<AlojamientoDTO> misPublicaciones = alojamientoService.obtenerMisPublicaciones(emailUsuario);
        return ResponseEntity.ok(misPublicaciones);
    }

    @GetMapping("/ordenar/recientes")
    public ResponseEntity<List<AlojamientoFilterDTO>> ordenarPorRecientes() {
        return ResponseEntity.ok(alojamientoService.listarRecientes());
    }
    @PostMapping("/fotos")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> subirFoto(@RequestParam("file") MultipartFile file) throws IOException {
        String filename = archivoService.guardarImagen(file);
        return ResponseEntity.ok(filename); // ej: "f2c4b3c1-...jpg"
    }

    @GetMapping("/cerca/mejores")
    public ResponseEntity<List<AlojamientoDTO>> mejoresCerca(
            @RequestParam Double lat,
            @RequestParam Double lon,
            @RequestParam Double radioKm
    ) {
        return ResponseEntity.ok(
                alojamientoService.mejoresCerca(lat, lon, radioKm)
        );
    }

    @GetMapping("/recom/mas-visitados")
    public List<AlojamientoDTO> masVisitados(
            @RequestParam Double lat,
            @RequestParam Double lon,
            @RequestParam Double radio
    ) {
        return alojamientoService.masVisitadosCerca(lat, lon, radio);
    }


    @GetMapping("/recom/filtros")
    public List<AlojamientoDTO> filtrar(
            @RequestParam(required = false) String nombre
    ) {
        if (nombre != null && !nombre.isBlank()) {
            return alojamientoService.buscarPorDireccion(nombre);
        }
        return alojamientoService.obtenerTodos();
    }


}