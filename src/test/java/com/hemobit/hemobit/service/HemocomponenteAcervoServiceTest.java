package com.hemobit.hemobit.service;

import com.hemobit.hemobit.domain.Doacao;
import com.hemobit.hemobit.domain.Doador;
import com.hemobit.hemobit.domain.Hemocomponente;
import com.hemobit.hemobit.domain.TipoHemocomponente;
import com.hemobit.hemobit.domain.TipoSanguineo;
import com.hemobit.hemobit.dto.HemocomponenteRequestDTO;
import com.hemobit.hemobit.dto.HemocomponenteResponseDTO;
import com.hemobit.hemobit.dto.TipoHemocomponenteDTO;
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

class HemocomponenteAcervoServiceTest {
    private static final String MENSAGEM_VENCIDA = "Não é permitido cadastrar bolsas com data de validade vencida!";

    private Map<Long, Object> bolsas;
    private HemocomponenteService service;

    @BeforeEach
    void preparar() {
        bolsas = new LinkedHashMap<>();
        Map<Long, Object> doacoes = new LinkedHashMap<>();
        Doador doador = new Doador();
        doador.setTipoSanguineo(TipoSanguineo.O_POSITIVO);
        Doacao doacao = new Doacao();
        doacao.setDoador(doador);
        doacao.setDataHora(LocalDateTime.now().minusDays(3));
        RepositoriosEmMemoria.definirId(doacao, 1L);
        doacoes.put(1L, doacao);

        HemocomponenteRepository hemocomponenteRepo = RepositoriosEmMemoria.criar(
                HemocomponenteRepository.class, bolsas, Map.of());
        DoacaoRepository doacaoRepo = RepositoriosEmMemoria.criar(DoacaoRepository.class, doacoes, Map.of());
        LocalidadeRepository localidadeRepo = RepositoriosEmMemoria.criar(
                LocalidadeRepository.class, new LinkedHashMap<>(), Map.of());
        service = new HemocomponenteService(hemocomponenteRepo, doacaoRepo, localidadeRepo);
    }

    @Test
    void catalogoListaOsTresTiposComNomeEPrazoDeReferencia() {
        List<TipoHemocomponenteDTO> tipos = service.listarTipos();

        assertEquals(3, tipos.size());
        assertEquals(TipoHemocomponente.CONCENTRADO_HEMACIAS, tipos.get(0).codigo());
        assertEquals("Concentrado de hemácias", tipos.get(0).nome());
        assertEquals(42, tipos.get(0).validadeReferenciaDias());
        assertEquals(5, tipos.get(1).validadeReferenciaDias());
        assertTrue(tipos.stream().allMatch(t -> t.nome() != null && !t.nome().isBlank()));
    }

    @Test
    void edicaoComNovaValidadeVencidaDeveSerRejeitadaSemAlterarABolsa() {
        bolsa(1L, 20);
        HemocomponenteRequestDTO dto = edicao(-1);

        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class, () -> service.atualizar(1L, dto));

        assertEquals(MENSAGEM_VENCIDA, erro.getMessage());
        assertEquals(LocalDate.now().plusDays(20), ((Hemocomponente) bolsas.get(1L)).getDataValidade());
    }

    @Test
    void edicaoComValidadeAnteriorAProducaoDeveSerRejeitada() {
        bolsa(1L, 20);
        HemocomponenteRequestDTO dto = edicao(5);
        dto.setDataProducao(LocalDate.now().plusDays(10));

        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class, () -> service.atualizar(1L, dto));

        assertEquals("Data de validade não pode ser anterior à data de produção.", erro.getMessage());
    }

    @Test
    void edicaoSemTipoOuSemDatasDeveSerRejeitada() {
        bolsa(1L, 20);
        HemocomponenteRequestDTO semTipo = edicao(5);
        semTipo.setTipo(null);
        HemocomponenteRequestDTO semDatas = edicao(5);
        semDatas.setDataValidade(null);

        assertEquals("Informe o tipo do hemocomponente.",
                assertThrows(IllegalArgumentException.class, () -> service.atualizar(1L, semTipo)).getMessage());
        assertEquals("Informe as datas de produção e de validade.",
                assertThrows(IllegalArgumentException.class, () -> service.atualizar(1L, semDatas)).getMessage());
    }

    @Test
    void edicaoDeBolsaJaVencidaSemMudarAValidadeDeveSerPermitida() {
        bolsa(1L, -2);
        HemocomponenteRequestDTO dto = edicao(-2);
        dto.setQuantidade(250);

        HemocomponenteResponseDTO atualizada = service.atualizar(1L, dto);

        assertEquals(250.0, atualizada.getQuantidade());
        assertEquals(LocalDate.now().minusDays(2), atualizada.getDataValidade());
    }

    @Test
    void edicaoComValidadeFuturaDeveAtualizarOsDados() {
        bolsa(1L, 20);
        HemocomponenteRequestDTO dto = edicao(30);
        dto.setTipo(TipoHemocomponente.CONCENTRADO_PLAQUETAS);

        HemocomponenteResponseDTO atualizada = service.atualizar(1L, dto);

        assertEquals(TipoHemocomponente.CONCENTRADO_PLAQUETAS, atualizada.getTipo());
        assertEquals(LocalDate.now().plusDays(30), atualizada.getDataValidade());
    }

    private void bolsa(long id, int diasParaVencer) {
        Hemocomponente bolsa = new Hemocomponente(TipoHemocomponente.CONCENTRADO_HEMACIAS, 300,
                LocalDate.now().minusDays(30), LocalDate.now().plusDays(diasParaVencer), null, null);
        bolsa.atribuirCodigo("B" + id);
        RepositoriosEmMemoria.definirId(bolsa, id);
        bolsas.put(id, bolsa);
    }

    private HemocomponenteRequestDTO edicao(int diasParaVencer) {
        HemocomponenteRequestDTO dto = new HemocomponenteRequestDTO();
        dto.setTipo(TipoHemocomponente.CONCENTRADO_HEMACIAS);
        dto.setQuantidade(300);
        dto.setDataProducao(LocalDate.now().minusDays(30));
        dto.setDataValidade(LocalDate.now().plusDays(diasParaVencer));
        return dto;
    }
}
