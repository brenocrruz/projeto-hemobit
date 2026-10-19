package com.hemobit.hemobit.dto;

public record AnaliseTipoDTO(
        String tipo,
        long estoqueValido,
        double demandaDiaria,
        double lambdaHorizonte,
        double probDesabastecimentoPct,
        String nivelRisco,
        long totalBolsas,
        long bolsasVencidas,
        double taxaDescartePct) {
}