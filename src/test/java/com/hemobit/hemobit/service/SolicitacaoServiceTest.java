package com.hemobit.hemobit.service;

import com.hemobit.hemobit.domain.Doacao;
import com.hemobit.hemobit.domain.Doador;
import com.hemobit.hemobit.domain.Hemocomponente;
import com.hemobit.hemobit.domain.HistoricoSolicitacao;
import com.hemobit.hemobit.domain.Prioridade;
import com.hemobit.hemobit.domain.Solicitacao;
import com.hemobit.hemobit.domain.StatusHemocomponente;
import com.hemobit.hemobit.domain.StatusSolicitacao;
import com.hemobit.hemobit.domain.TipoHemocomponente;
import com.hemobit.hemobit.domain.TipoSanguineo;
import com.hemobit.hemobit.domain.UnidadeSaude;
import com.hemobit.hemobit.dto.SolicitacaoRequestDTO;
import com.hemobit.hemobit.repository.HemocomponenteRepository;
import com.hemobit.hemobit.repository.HistoricoSolicitacaoRepository;
import com.hemobit.hemobit.repository.SolicitacaoRepository;
import com.hemobit.hemobit.repository.UnidadeSaudeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SolicitacaoServiceTest {
    private Map<Long, Object> solicitacoes;
    private Map<Long, Object> bolsas;
    private Map<Long, Object> historico;
    private SolicitacaoService service;

    @BeforeEach
    void preparar() {
        solicitacoes = new LinkedHashMap<>();
        bolsas = new LinkedHashMap<>();
        historico = new LinkedHashMap<>();
        Map<Long, Object> unidades = new LinkedHashMap<>();

        UnidadeSaude hospital = new UnidadeSaude("Hospital São Marcos", "Rua A", "Recife", "8130000000");
        RepositoriosEmMemoria.definirId(hospital, 1L);
        unidades.put(1L, hospital);

        SolicitacaoRepository solicitacaoRepo = RepositoriosEmMemoria.criar(
                SolicitacaoRepository.class, solicitacoes, Map.of());

        HemocomponenteRepository hemocomponenteRepo = RepositoriosEmMemoria.criar(
                HemocomponenteRepository.class, bolsas, Map.of(
                        "buscarParaReserva", args -> bolsas.values().stream()
                                .map(o -> (Hemocomponente) o)
                                .filter(h -> h.getTipo() == args[0]
                                        && h.getStatus() == args[1]
                                        && !h.getDataValidade().isBefore((LocalDate) args[2]))
                                .sorted(Comparator.comparing(Hemocomponente::getDataValidade)
                                        .thenComparing(Hemocomponente::getId))
                                .toList(),
                        "findBySolicitacaoIdOrderByDataValidadeAscIdAsc", args -> bolsas.values().stream()
                                .map(o -> (Hemocomponente) o)
                                .filter(h -> h.getSolicitacao() != null
                                        && h.getSolicitacao().getId().equals(args[0]))
                                .sorted(Comparator.comparing(Hemocomponente::getDataValidade))
                                .toList()));

        UnidadeSaudeRepository unidadeRepo = RepositoriosEmMemoria.criar(
                UnidadeSaudeRepository.class, unidades, Map.of());

        HistoricoSolicitacaoRepository historicoRepo = RepositoriosEmMemoria.criar(
                HistoricoSolicitacaoRepository.class, historico, Map.of(
                        "findBySolicitacaoIdOrderByDataHoraAscIdAsc", args -> historico.values().stream()
                                .map(o -> (HistoricoSolicitacao) o)
                                .filter(h -> h.getSolicitacao().getId().equals(args[0]))
                                .toList(),
                        "deleteBySolicitacaoId", args -> {
                            historico.values().removeIf(
                                    o -> ((HistoricoSolicitacao) o).getSolicitacao().getId().equals(args[0]));
                            return null;
                        }));

        service = new SolicitacaoService(solicitacaoRepo, hemocomponenteRepo, unidadeRepo,
                historicoRepo, new CompatibilidadeService());
    }

    @Test
    void criarDeveSalvarPendenteComCodigoEHistoricoInicial() {
        Solicitacao criada = service.criar(pedido(2, TipoSanguineo.A_POSITIVO));

        assertNotNull(criada.getId());
        assertEquals(StatusSolicitacao.PENDENTE, criada.getStatus());
        assertEquals(Prioridade.URGENTE, criada.getPrioridade());
        assertEquals(TipoSanguineo.A_POSITIVO, criada.getTipoSanguineo());

        List<HistoricoSolicitacao> linhas = service.listarHistorico(criada.getId());
        assertEquals(1, linhas.size());
        assertEquals(StatusSolicitacao.PENDENTE, linhas.get(0).getStatusNovo());
    }

    @Test
    void criarComQuantidadeZeroOuNegativaDeveSerRejeitadoSemGravar() {
        for (double quantidade : new double[]{0, -3}) {
            IllegalArgumentException erro = assertThrows(IllegalArgumentException.class,
                    () -> service.criar(pedido(quantidade, TipoSanguineo.A_POSITIVO)));
            assertEquals("A quantidade solicitada deve ser de no mínimo 1 bolsa.", erro.getMessage());
        }
        assertTrue(solicitacoes.isEmpty());
        assertTrue(historico.isEmpty());
    }

    @Test
    void criarDeveRejeitarQuantidadeFracionadaCamposObrigatoriosEHospitalInexistente() {
        assertThrows(IllegalArgumentException.class, () -> service.criar(pedido(1.5, TipoSanguineo.A_POSITIVO)));

        SolicitacaoRequestDTO semTipoSanguineo = pedido(1, null);
        assertThrows(IllegalArgumentException.class, () -> service.criar(semTipoSanguineo));

        SolicitacaoRequestDTO semPrioridade = pedido(1, TipoSanguineo.A_POSITIVO);
        semPrioridade.setPrioridade(null);
        assertThrows(IllegalArgumentException.class, () -> service.criar(semPrioridade));

        SolicitacaoRequestDTO hospitalInexistente = pedido(1, TipoSanguineo.A_POSITIVO);
        hospitalInexistente.setUnidadeSaudeId(99L);
        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class,
                () -> service.criar(hospitalInexistente));
        assertEquals("Unidade de saúde não encontrada.", erro.getMessage());
        assertTrue(solicitacoes.isEmpty());
    }

    @Test
    void iniciarSeparacaoDeveAtualizarStatusEGravarOperadorEHorario() {
        Solicitacao criada = service.criar(pedido(1, TipoSanguineo.A_POSITIVO));

        Solicitacao atualizada = service.alterarStatus(criada.getId(), StatusSolicitacao.EM_SEPARACAO, "Maria");

        assertEquals(StatusSolicitacao.EM_SEPARACAO, atualizada.getStatus());
        List<HistoricoSolicitacao> linhas = service.listarHistorico(criada.getId());
        assertEquals(2, linhas.size());
        HistoricoSolicitacao ultima = linhas.get(1);
        assertEquals(StatusSolicitacao.PENDENTE, ultima.getStatusAnterior());
        assertEquals(StatusSolicitacao.EM_SEPARACAO, ultima.getStatusNovo());
        assertEquals("Maria", ultima.getOperador());
        assertNotNull(ultima.getDataHora());
    }

    @Test
    void solicitacaoFinalizadaNaoPodeMudarDeStatus() {
        Solicitacao criada = service.criar(pedido(1, TipoSanguineo.A_POSITIVO));
        service.alterarStatus(criada.getId(), StatusSolicitacao.CANCELADA, "Maria");

        IllegalStateException erro = assertThrows(IllegalStateException.class,
                () -> service.alterarStatus(criada.getId(), StatusSolicitacao.PENDENTE, "Maria"));

        assertEquals("Não é permitido alterar o status de solicitações já finalizadas.", erro.getMessage());
        assertEquals(StatusSolicitacao.CANCELADA, criada.getStatus());
    }

    @Test
    void deveRecusarTransicaoInvalidaEOperadorEmBranco() {
        Solicitacao criada = service.criar(pedido(1, TipoSanguineo.A_POSITIVO));
        service.alterarStatus(criada.getId(), StatusSolicitacao.EM_SEPARACAO, "Maria");

        assertThrows(IllegalStateException.class,
                () -> service.alterarStatus(criada.getId(), StatusSolicitacao.PENDENTE, "Maria"));
        assertThrows(IllegalArgumentException.class,
                () -> service.alterarStatus(criada.getId(), StatusSolicitacao.CANCELADA, "  "));
        assertThrows(NoSuchElementException.class,
                () -> service.alterarStatus(999L, StatusSolicitacao.CANCELADA, "Maria"));
    }

    @Test
    void atenderDeveReservarBolsasCompativeisDentroDaValidadeNaOrdemFefo() {
        Hemocomponente incompativel = bolsa(TipoSanguineo.B_POSITIVO, 2);
        Hemocomponente venceLonge = bolsa(TipoSanguineo.A_POSITIVO, 20);
        Hemocomponente venceCerto = bolsa(TipoSanguineo.O_NEGATIVO, 5);
        Hemocomponente jaVencida = bolsa(TipoSanguineo.A_NEGATIVO, -1);
        Hemocomponente outraIncompativel = bolsa(TipoSanguineo.AB_POSITIVO, 1);
        Solicitacao pedido = service.criar(pedido(2, TipoSanguineo.A_POSITIVO));

        Solicitacao atendida = service.atender(pedido.getId(), "João");

        assertEquals(StatusSolicitacao.ATENDIDA, atendida.getStatus());
        assertNotNull(atendida.getDataAtendimento());
        assertEquals(StatusHemocomponente.RESERVADO, venceCerto.getStatus());
        assertEquals(StatusHemocomponente.RESERVADO, venceLonge.getStatus());
        assertEquals(StatusHemocomponente.DISPONIVEL, incompativel.getStatus());
        assertEquals(StatusHemocomponente.DISPONIVEL, jaVencida.getStatus());
        assertEquals(StatusHemocomponente.DISPONIVEL, outraIncompativel.getStatus());

        List<Hemocomponente> reservadas = service.listarBolsasReservadas(pedido.getId());
        assertEquals(2, reservadas.size());
        assertEquals(venceCerto, reservadas.get(0));
        assertEquals("João", service.listarHistorico(pedido.getId()).get(1).getOperador());
    }

    @Test
    void atenderSemEstoqueCompativelDeveFalharSemAlterarNada() {
        Hemocomponente incompativel = bolsa(TipoSanguineo.B_POSITIVO, 10);
        Hemocomponente compativel = bolsa(TipoSanguineo.O_NEGATIVO, 10);
        Solicitacao pedido = service.criar(pedido(2, TipoSanguineo.A_POSITIVO));

        IllegalStateException erro = assertThrows(IllegalStateException.class,
                () -> service.atender(pedido.getId(), "João"));

        assertTrue(erro.getMessage().startsWith("Estoque compatível insuficiente"));
        assertEquals(StatusSolicitacao.PENDENTE, pedido.getStatus());
        assertEquals(StatusHemocomponente.DISPONIVEL, compativel.getStatus());
        assertEquals(StatusHemocomponente.DISPONIVEL, incompativel.getStatus());
        assertEquals(1, service.listarHistorico(pedido.getId()).size());
    }

    @Test
    void atenderViaAlterarStatusDeveUsarAMesmaRegra() {
        Hemocomponente compativel = bolsa(TipoSanguineo.AB_POSITIVO, 3);
        Solicitacao pedido = service.criar(pedido(1, TipoSanguineo.AB_POSITIVO));

        Solicitacao atendida = service.alterarStatus(pedido.getId(), StatusSolicitacao.ATENDIDA, "Ana");

        assertEquals(StatusSolicitacao.ATENDIDA, atendida.getStatus());
        assertEquals(StatusHemocomponente.RESERVADO, compativel.getStatus());
    }

    @Test
    void naoDeveAtenderDuasVezesNemExcluirSolicitacaoAtendida() {
        bolsa(TipoSanguineo.O_NEGATIVO, 10);
        Solicitacao pedido = service.criar(pedido(1, TipoSanguineo.O_NEGATIVO));
        service.atender(pedido.getId(), "João");

        assertThrows(IllegalStateException.class, () -> service.atender(pedido.getId(), "João"));
        assertThrows(IllegalStateException.class, () -> service.deletar(pedido.getId()));
    }

    @Test
    void excluirSolicitacaoPendenteDeveRemoverTambemOHistorico() {
        Solicitacao pedido = service.criar(pedido(1, TipoSanguineo.A_POSITIVO));

        service.deletar(pedido.getId());

        assertTrue(solicitacoes.isEmpty());
        assertTrue(historico.isEmpty());
    }

    private SolicitacaoRequestDTO pedido(double quantidade, TipoSanguineo tipoSanguineo) {
        SolicitacaoRequestDTO dto = new SolicitacaoRequestDTO();
        dto.setUnidadeSaudeId(1L);
        dto.setTipoHemocomponente(TipoHemocomponente.CONCENTRADO_HEMACIAS);
        dto.setTipoSanguineo(tipoSanguineo);
        dto.setQuantidade(quantidade);
        dto.setPrioridade(Prioridade.URGENTE);
        return dto;
    }

    private Hemocomponente bolsa(TipoSanguineo tipoDoador, int diasParaVencer) {
        Doador doador = new Doador("Doador", "81999999999", LocalDate.of(1990, 1, 1), tipoDoador);
        Doacao doacao = new Doacao(LocalDateTime.now().minusDays(1), 450, doador, null);
        Hemocomponente bolsa = new Hemocomponente(TipoHemocomponente.CONCENTRADO_HEMACIAS, 300,
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(diasParaVencer), doacao, null);
        RepositoriosEmMemoria.definirId(bolsa, bolsas.size() + 1L);
        bolsas.put(bolsa.getId(), bolsa);
        return bolsa;
    }
    
    @Test
    void deveLancarErroSeQuantidadeForMaiorQueCinquenta() {
        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class,
                () -> service.criar(pedido(51, TipoSanguineo.A_POSITIVO)));
        
        assertEquals("A quantidade máxima por solicitação é", erro.getMessage());
    }

    
}
