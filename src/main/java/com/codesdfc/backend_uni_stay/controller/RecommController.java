package com.codesdfc.backend_uni_stay.controller;

import com.codesdfc.backend_uni_stay.dto.RecoResponseDTO;
import com.codesdfc.backend_uni_stay.service.RecommService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recomm")
@RequiredArgsConstructor
public class RecommController {

    private final RecommService recommService;

    @GetMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RecoResponseDTO> recomendar(
            @PathVariable Long userId,
            @RequestParam(name = "top_n", required = false) Integer topN,
            @RequestParam(name = "excluisr", required = false) List<Long> excluir
    ) {
        RecoResponseDTO reco = recommService.recomendar(userId, topN, excluir);
        return ResponseEntity.ok(reco);
    }

    @GetMapping("/health")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> health() {
        return ResponseEntity.ok(recommService.health());
    }

    @PostMapping("/reload")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> reload() {
        return ResponseEntity.ok(recommService.reload());
    }
}
