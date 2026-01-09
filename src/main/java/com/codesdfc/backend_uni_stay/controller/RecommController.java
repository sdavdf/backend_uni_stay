package com.codesdfc.backend_uni_stay.controller;

import com.codesdfc.backend_uni_stay.dto.RecoResponseDTO;
import com.codesdfc.backend_uni_stay.service.RecommService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recomm")
@RequiredArgsConstructor
public class RecommController {

    private final RecommService recommService;

    @GetMapping("/{userId}")
    public ResponseEntity<RecoResponseDTO> recomendar(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "5") int top
    ) {
        return ResponseEntity.ok(
                recommService.recomendar(userId, top)
        );
    }
}
