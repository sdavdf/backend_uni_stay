package com.codesdfc.backend_uni_stay.service;

import com.codesdfc.backend_uni_stay.dto.*;
import com.codesdfc.backend_uni_stay.recomm.FeatureEncoder;
import com.codesdfc.backend_uni_stay.recomm.UserFeatureRow;
import com.codesdfc.backend_uni_stay.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommService {

    private final UsuarioRepository usuarioRepository;
    //recomendaciones por similitud
    private FeatureEncoder encoder;
    private boolean trained = false;

    public RecoResponseDTO recomendar(Long userId, int topN) {

        if (!trained) {
            List<UserFeatureRow> rows = usuarioRepository.fetchUserFeatureRows();
            encoder = new FeatureEncoder();
            encoder.fitTransform(rows);
            trained = true;
        }

        var recs = encoder.recommend(userId, topN);

        List<RecoItemDTO> items = recs.stream()
                .map(e -> new RecoItemDTO(
                        e.getKey(),
                        usuarioRepository.findNombreById(e.getKey()),
                        e.getValue()
                ))
                .collect(Collectors.toList());

        return new RecoResponseDTO(userId, items);
    }
}
