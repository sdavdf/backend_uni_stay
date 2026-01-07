// src/main/java/com/codesdf/unimatch/service/RecommService.java
package com.codesdfc.backend_uni_stay.service;

import com.codesdfc.backend_uni_stay.dto.RecoResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommService {

    private final WebClient recommClient; //inyectado desde WebClientConfig

    public RecoResponseDTO recomendar(Long userId, Integer topN, List<Long> excluir) {

        int top = (topN != null && topN > 0) ? topN : 5;

        return recommClient
                .get()
                .uri(uriBuilder -> {
                    // URL: /recomendar/{userId}?top_n=..
                    var ub = uriBuilder
                            .path("/recomendar/{userId}")
                            .queryParam("top_n", top);

                    if (excluir != null) {
                        excluir.forEach(e -> ub.queryParam("excluir", e));
                    }

                    return ub.build(userId);
                })
                .retrieve()
                .bodyToMono(RecoResponseDTO.class)
                .block();
    }

    public Object health() {
        return recommClient
                .get()
                .uri("/health")
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    public Object reload() {
        return recommClient
                .post()
                .uri("/admin/reload")
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }
}
