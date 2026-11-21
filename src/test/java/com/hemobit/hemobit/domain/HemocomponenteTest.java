package com.hemobit.hemobit.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HemocomponenteTest {
    private static final LocalDate HOJE = LocalDate.of(2026, 11, 18);
    private static final String MENSAGEM_VENCIDA = "Não é permitido cadastrar bolsas com data de validade vencida!";

    @Test
    void registrarNormalizaOCodigoEIniciaComoDisponivel() {
        Hemocomponente bolsa = bolsa("  b-001 ", HOJE.plusDays(10));

        assertEquals("B-001", bolsa.getCodigo());
        assertEquals(StatusHemocomponente.DISPONIVEL, bolsa.getStatus());
        assertEquals(TipoSanguineo.O_NEGATIVO, bolsa.getTipoSanguineo());
        assertNull(bolsa.getSolicitacao());
    }

    @Test
    void registrarAceitaValidadeNoProprioDia() {
        Hemocomponente bolsa = bolsa("B1", HOJE);

        assertFalse(bolsa.estaVencida(HOJE));
        assertTrue(bolsa.estaDisponivel(HOJE));
    }

    @Test
    void registrarRejeitaCodigoVazioOuNulo() {
        assertEquals("Informe o código da bolsa.", erroRegistro(null, TipoHemocomponente.CONCENTRADO_HEMACIAS, 300,
                HOJE.minusDays(1), HOJE.plusDays(5), doacao()));
        assertEquals("Informe o código da bolsa.", erroRegistro("   ", TipoHemocomponente.CONCENTRADO_HEMACIAS, 300,
                HOJE.minusDays(1), HOJE.plusDays(5), doacao()));
    }

    @Test
    void registrarRejeitaTipoEDatasAusentes() {
        assertEquals("Informe o tipo do hemocomponente.",
                erroRegistro("B1", null, 300, HOJE.minusDays(1), HOJE.plusDays(5), doacao()));
        assertEquals("Informe as datas de produção e de validade.",
                erroRegistro("B1", TipoHemocomponente.CONCENTRADO_HEMACIAS, 300, null, HOJE.plusDays(5), doacao()));
        assertEquals("Informe as datas de produção e de validade.",
                erroRegistro("B1", TipoHemocomponente.CONCENTRADO_HEMACIAS, 300, HOJE.minusDays(1), null, doacao()));
    }

    @Test
    void registrarRejeitaValidadeAnteriorAProducao() {
        assertEquals("Data de validade não pode ser anterior à data de produção.",
                erroRegistro("B1", TipoHemocomponente.CONCENTRADO_HEMACIAS, 300, HOJE.plusDays(3), HOJE.plusDays(2),
                        doacao()));
    }

    @Test
    void registrarRejeitaBolsaJaVencida() {
        assertEquals(MENSAGEM_VENCIDA, erroRegistro("B1", TipoHemocomponente.CONCENTRADO_HEMACIAS, 300,
                HOJE.minusDays(10), HOJE.minusDays(1), doacao()));
    }

    @Test
    void registrarRejeitaQuantidadeNaoPositivaEDoacaoAusente() {
        assertEquals("Quantidade deve ser maior que zero.", erroRegistro("B1",
                TipoHemocomponente.CONCENTRADO_HEMACIAS, 0, HOJE.minusDays(1), HOJE.plusDays(5), doacao()));
        assertEquals("Informe a doação de origem da bolsa.", erroRegistro("B1",
                TipoHemocomponente.CONCENTRADO_HEMACIAS, 300, HOJE.minusDays(1), HOJE.plusDays(5), null));
    }

    @Test
    void corrigirDadosAtualizaTodosOsCamposInformados() {
        Hemocomponente bolsa = bolsa("B1", HOJE.plusDays(10));

        bolsa.corrigirDados(TipoHemocomponente.CONCENTRADO_PLAQUETAS, 250, HOJE.minusDays(2), HOJE.plusDays(30), HOJE);

        assertEquals(TipoHemocomponente.CONCENTRADO_PLAQUETAS, bolsa.getTipo());
        assertEquals(250.0, bolsa.getQuantidade());
        assertEquals(HOJE.minusDays(2), bolsa.getDataProducao());
        assertEquals(HOJE.plusDays(30), bolsa.getDataValidade());
    }

    @Test
    void corrigirDadosRejeitaNovaValidadeVencidaSemAlterarABolsa() {
        Hemocomponente bolsa = bolsa("B1", HOJE.plusDays(10));

        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class, () -> bolsa.corrigirDados(
                TipoHemocomponente.CONCENTRADO_PLAQUETAS, 250, HOJE.minusDays(20), HOJE.minusDays(1), HOJE));

        assertEquals(MENSAGEM_VENCIDA, erro.getMessage());
        assertEquals(TipoHemocomponente.CONCENTRADO_HEMACIAS, bolsa.getTipo());
        assertEquals(HOJE.plusDays(10), bolsa.getDataValidade());
    }

    @Test
    void corrigirDadosPermiteManterAValidadeDeUmaBolsaJaVencida() {
        Hemocomponente vencida = new Hemocomponente(TipoHemocomponente.CONCENTRADO_HEMACIAS, 300,
                HOJE.minusDays(30), HOJE.minusDays(2), doacao(), null);

        vencida.corrigirDados(TipoHemocomponente.CONCENTRADO_HEMACIAS, 150, HOJE.minusDays(30), HOJE.minusDays(2), HOJE);

        assertEquals(150.0, vencida.getQuantidade());
    }

    @Test
    void corrigirDadosValidaOsCamposObrigatorios() {
        Hemocomponente bolsa = bolsa("B1", HOJE.plusDays(10));

        assertEquals("Informe o tipo do hemocomponente.", assertThrows(IllegalArgumentException.class,
                () -> bolsa.corrigirDados(null, 300, HOJE.minusDays(1), HOJE.plusDays(5), HOJE)).getMessage());
        assertEquals("Informe as datas de produção e de validade.", assertThrows(IllegalArgumentException.class,
                () -> bolsa.corrigirDados(TipoHemocomponente.CONCENTRADO_HEMACIAS, 300, HOJE.minusDays(1), null, HOJE))
                .getMessage());
        assertEquals("Quantidade deve ser maior que zero.", assertThrows(IllegalArgumentException.class,
                () -> bolsa.corrigirDados(TipoHemocomponente.CONCENTRADO_HEMACIAS, -1, HOJE.minusDays(1),
                        HOJE.plusDays(5), HOJE)).getMessage());
    }

    @Test
    void estaVencidaSoQuandoAValidadeJaPassou() {
        assertFalse(bolsa("B1", HOJE).estaVencida(HOJE));
        assertTrue(bolsa("B2", HOJE).estaVencida(HOJE.plusDays(1)));
    }

    @Test
    void reservarParaMarcaComoReservadaEVinculaASolicitacao() {
        Hemocomponente bolsa = bolsa("B1", HOJE.plusDays(10));
        Solicitacao solicitacao = solicitacao();

        bolsa.reservarPara(solicitacao, HOJE);

        assertEquals(StatusHemocomponente.RESERVADO, bolsa.getStatus());
        assertSame(solicitacao, bolsa.getSolicitacao());
        assertFalse(bolsa.estaDisponivel(HOJE));
    }

    @Test
    void reservarParaRejeitaBolsaJaReservada() {
        Hemocomponente bolsa = bolsa("B1", HOJE.plusDays(10));
        bolsa.reservarPara(solicitacao(), HOJE);

        IllegalStateException erro = assertThrows(IllegalStateException.class,
                () -> bolsa.reservarPara(solicitacao(), HOJE));

        assertEquals("A bolsa B1 não está disponível para reserva.", erro.getMessage());
    }

    @Test
    void reservarParaRejeitaBolsaVencidaESolicitacaoAusente() {
        Hemocomponente bolsa = bolsa("B1", HOJE);

        assertThrows(IllegalStateException.class, () -> bolsa.reservarPara(solicitacao(), HOJE.plusDays(1)));
        assertThrows(IllegalArgumentException.class, () -> bolsa.reservarPara(null, HOJE));
        assertEquals(StatusHemocomponente.DISPONIVEL, bolsa.getStatus());
    }

    @Test
    void marcarComoVencidaMudaOStatusQuandoAValidadeJaPassou() {
        Hemocomponente bolsa = bolsa("B1", HOJE);

        bolsa.marcarComoVencida(HOJE.plusDays(1));

        assertEquals(StatusHemocomponente.VENCIDO, bolsa.getStatus());
    }

    @Test
    void marcarComoVencidaRejeitaBolsaDentroDaValidadeOuNaoDisponivel() {
        Hemocomponente dentroDaValidade = bolsa("B1", HOJE.plusDays(5));
        Hemocomponente reservada = bolsa("B2", HOJE);
        reservada.reservarPara(solicitacao(), HOJE);

        assertEquals("A bolsa B1 ainda está dentro da validade.", assertThrows(IllegalStateException.class,
                () -> dentroDaValidade.marcarComoVencida(HOJE)).getMessage());
        assertEquals("Só bolsas disponíveis podem ser marcadas como vencidas.", assertThrows(IllegalStateException.class,
                () -> reservada.marcarComoVencida(HOJE.plusDays(1))).getMessage());
    }

    @Test
    void tipoSanguineoVemDoDoadorDaDoacao() {
        Hemocomponente semDoador = new Hemocomponente(TipoHemocomponente.CONCENTRADO_HEMACIAS, 300,
                HOJE.minusDays(1), HOJE.plusDays(5), new Doacao(), null);

        assertNull(semDoador.getTipoSanguineo());
    }

    private String erroRegistro(String codigo, TipoHemocomponente tipo, double quantidade, LocalDate producao,
                                LocalDate validade, Doacao doacao) {
        return assertThrows(IllegalArgumentException.class,
                () -> Hemocomponente.registrar(codigo, tipo, quantidade, producao, validade, doacao, null, HOJE))
                .getMessage();
    }

    private Hemocomponente bolsa(String codigo, LocalDate validade) {
        return Hemocomponente.registrar(codigo, TipoHemocomponente.CONCENTRADO_HEMACIAS, 300, HOJE.minusDays(5),
                validade, doacao(), null, HOJE);
    }

    private Doacao doacao() {
        Doador doador = new Doador();
        doador.setTipoSanguineo(TipoSanguineo.O_NEGATIVO);
        Doacao doacao = new Doacao();
        doacao.setDoador(doador);
        return doacao;
    }

    private Solicitacao solicitacao() {
        return Solicitacao.abrir(new UnidadeSaude("Hospital São Marcos", "Rua A", "Recife", "8130000000"),
                TipoHemocomponente.CONCENTRADO_HEMACIAS, TipoSanguineo.O_POSITIVO, 1, Prioridade.URGENTE);
    }
}
