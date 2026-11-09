package com.hemobit.hemobit.service;

import com.hemobit.hemobit.domain.Doacao;
import com.hemobit.hemobit.domain.Doador;
import com.hemobit.hemobit.domain.Hemocomponente;
import com.hemobit.hemobit.domain.StatusHemocomponente;
import com.hemobit.hemobit.domain.TipoHemocomponente;
import com.hemobit.hemobit.domain.TipoSanguineo;
import com.hemobit.hemobit.dto.HemocomponenteRequestDTO;
import com.hemobit.hemobit.dto.HemocomponenteResponseDTO;
import com.hemobit.hemobit.repository.DoacaoRepository;
import com.hemobit.hemobit.repository.HemocomponenteRepository;
import com.hemobit.hemobit.repository.LocalidadeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HemocomponenteEstoqueServiceTest {
    private static final String MENSAGEM_VENCIDA = "Não é permitido cadastrar bolsas com data de validade vencida!";

    private Map<Long, Object> bolsas;
    private Map<Long, Object> doacoes;
    private HemocomponenteService service;

    @BeforeEach
    void preparar() {
        bolsas = new LinkedHashMap<>();
        doacoes = new LinkedHashMap<>();
        doacao(1L, TipoSanguineo.O_POSITIVO);
        doacao(2L, TipoSanguineo.O_NEGATIVO);
        doacao(3L, TipoSanguineo.A_POSITIVO);

        HemocomponenteRepository hemocomponenteRepo = RepositoriosEmMemoria.criar(
                HemocomponenteRepository.class, bolsas, Map.of(
                        "existsByCodigo", args -> bolsas.values().stream()
                                .map(o -> (Hemocomponente) o)
                                .anyMatch(h -> args[0].equals(h.getCodigo()))));
        DoacaoRepository doacaoRepo = RepositoriosEmMemoria.criar(DoacaoRepository.class, doacoes, Map.of());
        LocalidadeRepository localidadeRepo = RepositoriosEmMemoria.criar(
                LocalidadeRepository.class, new LinkedHashMap<>(), Map.of());

        service = new HemocomponenteService(hemocomponenteRepo, doacaoRepo, localidadeRepo);
    }

    @Test
    void entradaValidaDeveSalvarDisponivelComCodigoNormalizadoETipoSanguineoDaDoacao() {
        HemocomponenteRequestDTO dto = entrada(" b102030 ", 1L, 30);
        dto.setTipo(TipoHemocomponente.PLASMA_FRESCO_CONGELADO);
        dto.setTipoSanguineo(TipoSanguineo.O_POSITIVO);

        HemocomponenteResponseDTO salvo = service.criar(dto);

        assertEquals("B102030", salvo.getCodigo());
        assertEquals(StatusHemocomponente.DISPONIVEL, salvo.getStatus());
        assertEquals(TipoHemocomponente.PLASMA_FRESCO_CONGELADO, salvo.getTipo());
        assertEquals(TipoSanguineo.O_POSITIVO, salvo.getTipoSanguineo());
        assertEquals(1, bolsas.size());
    }

    @Test
    void entradaComValidadeVencidaDeveSerRejeitadaSemGravar() {
        HemocomponenteRequestDTO dto = entrada("B1", 1L, -1);
        dto.setDataProducao(LocalDate.now().minusDays(10));

        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class, () -> service.criar(dto));

        assertEquals(MENSAGEM_VENCIDA, erro.getMessage());
        assertTrue(bolsas.isEmpty());
    }

    @Test
    void entradaComValidadeNoDiaDeHojeDeveSerAceita() {
        HemocomponenteRequestDTO dto = entrada("B2", 1L, 0);

        HemocomponenteResponseDTO salvo = service.criar(dto);

        assertEquals(LocalDate.now(), salvo.getDataValidade());
        assertEquals(1, bolsas.size());
    }

    @Test
    void entradaDeveExigirCodigoTipoDatasDoacaoEQuantidade() {
        HemocomponenteRequestDTO semCodigo = entrada("  ", 1L, 30);
        assertEquals("Informe o código da bolsa.",
                assertThrows(IllegalArgumentException.class, () -> service.criar(semCodigo)).getMessage());

        HemocomponenteRequestDTO semTipo = entrada("B3", 1L, 30);
        semTipo.setTipo(null);
        assertEquals("Informe o tipo do hemocomponente.",
                assertThrows(IllegalArgumentException.class, () -> service.criar(semTipo)).getMessage());

        HemocomponenteRequestDTO semData = entrada("B4", 1L, 30);
        semData.setDataValidade(null);
        assertEquals("Informe as datas de produção e de validade.",
                assertThrows(IllegalArgumentException.class, () -> service.criar(semData)).getMessage());

        HemocomponenteRequestDTO semDoacao = entrada("B5", null, 30);
        assertEquals("Informe a doação de origem da bolsa.",
                assertThrows(IllegalArgumentException.class, () -> service.criar(semDoacao)).getMessage());

        HemocomponenteRequestDTO quantidadeZero = entrada("B6", 1L, 30);
        quantidadeZero.setQuantidade(0);
        assertEquals("Quantidade deve ser maior que zero.",
                assertThrows(IllegalArgumentException.class, () -> service.criar(quantidadeZero)).getMessage());

        assertTrue(bolsas.isEmpty());
    }

    @Test
    void entradaComDoacaoInexistenteDeveSerRejeitada() {
        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class,
                () -> service.criar(entrada("B7", 99L, 30)));

        assertEquals("Doação não encontrada.", erro.getMessage());
        assertTrue(bolsas.isEmpty());
    }

    @Test
    void entradaComTipoSanguineoDiferenteDoDaDoacaoDeveSerRejeitada() {
        HemocomponenteRequestDTO dto = entrada("B8", 1L, 30);
        dto.setTipoSanguineo(TipoSanguineo.A_POSITIVO);

        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class, () -> service.criar(dto));

        assertEquals("O tipo sanguíneo informado não confere com o da doação selecionada.", erro.getMessage());
        assertTrue(bolsas.isEmpty());
    }

    @Test
    void entradaComCodigoRepetidoDeveSerBloqueadaIgnorandoMaiusculas() {
        service.criar(entrada("B9", 1L, 30));

        IllegalStateException erro = assertThrows(IllegalStateException.class,
                () -> service.criar(entrada("b9", 2L, 30)));

        assertEquals("Já existe uma bolsa cadastrada com o código B9.", erro.getMessage());
        assertEquals(1, bolsas.size());
    }

    @Test
    void resumoDoEstoqueDeveIncrementarSoComBolsasDisponiveisEDentroDaValidade() {
        assertEquals(0L, service.resumoEstoque().get(TipoSanguineo.O_POSITIVO));
        assertEquals(TipoSanguineo.values().length, service.resumoEstoque().size());

        service.criar(entrada("B10", 1L, 30));
        service.criar(entrada("B11", 1L, 10));
        assertEquals(2L, service.resumoEstoque().get(TipoSanguineo.O_POSITIVO));

        bolsa(10L, "B12", 1L, TipoHemocomponente.CONCENTRADO_HEMACIAS, StatusHemocomponente.RESERVADO, 20);
        bolsa(11L, "B13", 1L, TipoHemocomponente.CONCENTRADO_HEMACIAS, StatusHemocomponente.DISPONIVEL, -2);

        assertEquals(2L, service.resumoEstoque().get(TipoSanguineo.O_POSITIVO));
        assertEquals(0L, service.resumoEstoque().get(TipoSanguineo.AB_NEGATIVO));
    }

    @Test
    void consultaDeveFiltrarPorTipoSanguineoEStatusAoMesmoTempo() {
        bolsa(1L, "C1", 2L, TipoHemocomponente.CONCENTRADO_HEMACIAS, StatusHemocomponente.DISPONIVEL, 12);
        bolsa(2L, "C2", 2L, TipoHemocomponente.CONCENTRADO_HEMACIAS, StatusHemocomponente.RESERVADO, 5);
        bolsa(3L, "C3", 3L, TipoHemocomponente.CONCENTRADO_HEMACIAS, StatusHemocomponente.DISPONIVEL, 8);
        bolsa(4L, "C4", 2L, TipoHemocomponente.PLASMA_FRESCO_CONGELADO, StatusHemocomponente.DISPONIVEL, 3);

        List<HemocomponenteResponseDTO> resultado = service.listar(
                TipoSanguineo.O_NEGATIVO, StatusHemocomponente.DISPONIVEL, null);

        assertEquals(2, resultado.size());
        for (HemocomponenteResponseDTO item : resultado) {
            assertEquals(TipoSanguineo.O_NEGATIVO, item.getTipoSanguineo());
            assertEquals(StatusHemocomponente.DISPONIVEL, item.getStatus());
        }
    }

    @Test
    void consultaPorTipoDeHemocomponenteDeveConsiderarApenasOTipoPedido() {
        bolsa(1L, "D1", 2L, TipoHemocomponente.CONCENTRADO_HEMACIAS, StatusHemocomponente.DISPONIVEL, 12);
        bolsa(2L, "D2", 2L, TipoHemocomponente.PLASMA_FRESCO_CONGELADO, StatusHemocomponente.DISPONIVEL, 5);

        List<HemocomponenteResponseDTO> resultado = service.listar(
                null, null, TipoHemocomponente.PLASMA_FRESCO_CONGELADO);

        assertEquals(1, resultado.size());
        assertEquals("D2", resultado.get(0).getCodigo());
    }

    @Test
    void consultaSemResultadoDeveRetornarListaVazia() {
        bolsa(1L, "E1", 2L, TipoHemocomponente.CONCENTRADO_HEMACIAS, StatusHemocomponente.DISPONIVEL, 12);

        List<HemocomponenteResponseDTO> resultado = service.listar(
                TipoSanguineo.AB_NEGATIVO, StatusHemocomponente.DISPONIVEL, null);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void consultaSemFiltrosDeveRetornarTudoOrdenadoPelaValidade() {
        bolsa(1L, "F1", 2L, TipoHemocomponente.CONCENTRADO_HEMACIAS, StatusHemocomponente.DISPONIVEL, 20);
        bolsa(2L, "F2", 1L, TipoHemocomponente.CONCENTRADO_HEMACIAS, StatusHemocomponente.RESERVADO, 3);
        bolsa(3L, "F3", 3L, TipoHemocomponente.PLASMA_FRESCO_CONGELADO, StatusHemocomponente.DISPONIVEL, 9);

        List<HemocomponenteResponseDTO> resultado = service.listar(null, null, null);

        assertEquals(List.of("F2", "F3", "F1"),
                resultado.stream().map(HemocomponenteResponseDTO::getCodigo).toList());
    }

    private void doacao(long id, TipoSanguineo tipo) {
        Doador doador = new Doador("Doador " + id, "81999999999", LocalDate.of(1990, 1, 1), tipo);
        Doacao doacao = new Doacao(LocalDateTime.now().minusDays(2), 450, doador, null);
        RepositoriosEmMemoria.definirId(doacao, id);
        doacoes.put(id, doacao);
    }

    private HemocomponenteRequestDTO entrada(String codigo, Long doacaoId, int diasParaVencer) {
        HemocomponenteRequestDTO dto = new HemocomponenteRequestDTO();
        dto.setCodigo(codigo);
        dto.setTipo(TipoHemocomponente.CONCENTRADO_HEMACIAS);
        dto.setQuantidade(300);
        dto.setDataProducao(LocalDate.now().minusDays(1));
        dto.setDataValidade(LocalDate.now().plusDays(diasParaVencer));
        dto.setDoacaoId(doacaoId);
        return dto;
    }

    private void bolsa(long id, String codigo, long doacaoId, TipoHemocomponente tipo,
                       StatusHemocomponente status, int diasParaVencer) {
        Hemocomponente bolsa = new Hemocomponente(tipo, 300, LocalDate.now().minusDays(20),
                LocalDate.now().plusDays(diasParaVencer), (Doacao) doacoes.get(doacaoId), null);
        bolsa.setCodigo(codigo);
        bolsa.setStatus(status);
        RepositoriosEmMemoria.definirId(bolsa, id);
        bolsas.put(id, bolsa);
    }
}
