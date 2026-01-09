package com.codesdfc.backend_uni_stay.recomm;

import java.util.*;
import java.util.stream.Collectors;

public class FeatureEncoder {

    private final List<Long> userIds = new ArrayList<>();
    private double[][] Xnorm;

    // ========= PUBLIC =========
    public void fitTransform(List<UserFeatureRow> users) {

        // ====== VOCABULARIOS ======
        List<String> generoVocab = vocab(users.stream()
                .map(UserFeatureRow::getGenero));

        List<String> personalidadVocab = vocab(users.stream()
                .map(UserFeatureRow::getPersonalidad));

        List<String> limpiezaVocab = vocab(users.stream()
                .map(UserFeatureRow::getNivelLimpieza));

        List<String> horarioVocab = vocab(users.stream()
                .map(UserFeatureRow::getHorarioEstudio));

        int dim =
                generoVocab.size() +
                        personalidadVocab.size() +
                        limpiezaVocab.size() +
                        horarioVocab.size() +
                        4 +     // booleanos
                        3;      // numéricas

        double[][] X = new double[users.size()][dim];

        // ====== BUILD MATRIX ======
        for (int i = 0; i < users.size(); i++) {
            UserFeatureRow u = users.get(i);
            int col = 0;

            col = oneHot(X, i, col, generoVocab, u.getGenero());
            col = oneHot(X, i, col, personalidadVocab, u.getPersonalidad());
            col = oneHot(X, i, col, limpiezaVocab, u.getNivelLimpieza());
            col = oneHot(X, i, col, horarioVocab, u.getHorarioEstudio());

            X[i][col++] = bool(u.getCompartirHabitacion());
            X[i][col++] = bool(u.getFumador());
            X[i][col++] = bool(u.getMascotasPermitidas());
            X[i][col++] = bool(u.getFiestas());

            X[i][col++] = safe(u.getEdad());
            X[i][col++] = safe(u.getPresupuestoMax());
            X[i][col++] = safe(u.getDistanciaMaxima());

            userIds.add(u.getUserId());
        }

        normalizeRows(X);
        this.Xnorm = X;
    }

    // ========= RECOMMEND =========
    public List<Map.Entry<Long, Double>> recommend(Long userId, int topN) {
        int idx = userIds.indexOf(userId);
        if (idx == -1) return List.of();

        double[] q = Xnorm[idx];
        Map<Long, Double> sims = new HashMap<>();

        for (int i = 0; i < Xnorm.length; i++) {
            if (i == idx) continue;
            sims.put(userIds.get(i), cosine(q, Xnorm[i]));
        }

        return sims.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(topN)
                .collect(Collectors.toList());
    }

    // ========= HELPERS =========

    private List<String> vocab(java.util.stream.Stream<String> stream) {
        return stream
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    private int oneHot(double[][] X, int row, int col, List<String> vocab, String value) {
        for (String v : vocab) {
            X[row][col++] = v.equals(value) ? 1.0 : 0.0;
        }
        return col;
    }

    private double bool(Boolean b) {
        return Boolean.TRUE.equals(b) ? 1.0 : 0.0;
    }

    private double safe(Number n) {
        return n == null ? 0.0 : n.doubleValue();
    }

    private void normalizeRows(double[][] X) {
        for (int i = 0; i < X.length; i++) {
            double norm = Math.sqrt(dot(X[i], X[i]));
            if (norm == 0) norm = 1;
            for (int j = 0; j < X[i].length; j++) {
                X[i][j] /= norm;
            }
        }
    }

    private double cosine(double[] a, double[] b) {
        return dot(a, b);
    }

    private double dot(double[] a, double[] b) {
        double s = 0;
        for (int i = 0; i < a.length; i++) {
            s += a[i] * b[i];
        }
        return s;
    }
}
