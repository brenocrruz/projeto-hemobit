package com.hemobit.hemobit.service;

import com.hemobit.hemobit.domain.Hemocomponente;
import com.hemobit.hemobit.domain.HistoricoSolicitacao;
import com.hemobit.hemobit.domain.Solicitacao;
import com.hemobit.hemobit.domain.StatusHemocomponente;
import com.hemobit.hemobit.domain.StatusSolicitacao;
import com.hemobit.hemobit.domain.UnidadeSaude;
import com.hemobit.hemobit.dto.SolicitacaoRequestDTO;
import com.hemobit.hemobit.repository.HemocomponenteRepository;
import com.hemobit.hemobit.repository.HistoricoSolicitacaoRepository;
import com.hemobit.hemobit.repository.SolicitacaoRepository;
import com.hemobit.hemobit.repository.UnidadeSaudeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

// Resumo: regras de negócio das solicitações de hemocomponentes.
//   HU06 - criar solicitação (validações + código de identificação + status PENDENTE).
//   HU07 - mudar status com transições válidas, histórico (quando e quem) e atendimento com
//          compatibilidade ABO/Rh (AED) e FEFO.
// Explicando fácil: o controller só recebe e devolve dados; toda a decisão "pode ou não pode"
// fica aqui. Erros de dado inválido viram IllegalArgumentException (400), pedido que não existe vira
// NoSuchElementException (404) e ação que o estado atual não permite vira IllegalStateException (409).
@Service
public class SolicitacaoService {

    private static final String OPERADOR_PADRAO = "não informado";

    private final SolicitacaoRepository solicitacaoRepository;
    private final HemocomponenteRepository hemocomponenteRepository;
    private final UnidadeSaudeRepository unidadeSaudeRepository;
    private final HistoricoSolicitacaoRepository historicoRepository;
    private final CompatibilidadeService compatibilidadeService;

    @Autowired
    public SolicitacaoService(SolicitacaoRepository solicitacaoRepository,
                              HemocomponenteRepository hemocomponenteRepository,
                              UnidadeSaudeRepository unidadeSaudeRepository,
                              HistoricoSolicitacaoRepository historicoRepository,
                              CompatibilidadeService compatibilidadeService) {
        this.solicitacaoRepository = solicitacaoRepository;
        this.hemocomponenteRepository = hemocomponenteRepository;
        this.unidadeSaudeRepository = unidadeSaudeRepository;
        this.historicoRepository = historicoRepository;
        this.compatibilidadeService = compatibilidadeService;
    }

    // ---------------------------------------------------------------- consultas

    public List<Solicitacao> listarTodas() {
        return solicitacaoRepository.findAll();
    }

    public Optional<Solicitacao> buscarPorId(Long id) {
        return solicitacaoRepository.findById(id);
    }

    public List<HistoricoSolicitacao> listarHistorico(Long id) {
        buscarOuFalhar(id);
        return historicoRepository.findBySolicitacaoIdOrderByDataHoraAscIdAsc(id);
    }

    public List<Hemocomponente> listarBolsasReservadas(Long id) {
        buscarOuFalhar(id);
        return hemocomponenteRepository.findBySolicitacaoIdOrderByDataValidadeAscIdAsc(id);
    }

    // ---------------------------------------------------------------- HU06

    // Resumo: cria a solicitação já com status PENDENTE e registra a primeira linha do histórico.
    // Explicando fácil: valida tudo antes de gravar. Se algo estiver errado, nada vai para o banco.
    @Transactional
    public Solicitacao criar(SolicitacaoRequestDTO dto) {
        validar(dto);
        if (dto.getUnidadeSaudeId() == null) {
            throw new IllegalArgumentException("Solicitação precisa estar vinculada a uma unidade de saúde.");
        }
        UnidadeSaude unidade = unidadeSaudeRepository.findById(dto.getUnidadeSaudeId())
                .orElseThrow(() -> new IllegalArgumentException("Unidade de saúde não encontrada."));

        Solicitacao solicitacao = new Solicitacao(unidade, dto.getTipoHemocomponente(),
                dto.getTipoSanguineo(), dto.getQuantidade(), dto.getPrioridade());
        Solicitacao salva = solicitacaoRepository.save(solicitacao);
        historicoRepository.save(new HistoricoSolicitacao(salva, null, StatusSolicitacao.PENDENTE, "sistema"));
        return salva;
    }

    @Transactional
    public Solicitacao atualizar(Long id, SolicitacaoRequestDTO dto) {
        Solicitacao existente = buscarOuFalhar(id);
        if (existente.getStatus() != StatusSolicitacao.PENDENTE) {
            throw new IllegalStateException("Só é possível editar solicitações com status PENDENTE.");
        }
        validar(dto);

        existente.setTipoHemocomponente(dto.getTipoHemocomponente());
        existente.setTipoSanguineo(dto.getTipoSanguineo());
        existente.setQuantidade(dto.getQuantidade());
        existente.setPrioridade(dto.getPrioridade());
        return solicitacaoRepository.save(existente);
    }

    @Transactional
    public void deletar(Long id) {
        Solicitacao solicitacao = buscarOuFalhar(id);
        if (solicitacao.getStatus() == StatusSolicitacao.ATENDIDA) {
            throw new IllegalStateException("Não é possível excluir uma solicitação já atendida.");
        }
        historicoRepository.deleteBySolicitacaoId(id);
        solicitacaoRepository.deleteById(id);
    }

    // ---------------------------------------------------------------- HU07

    // Resumo: muda o status respeitando as transições permitidas e grava quem fez e quando.
    //   PENDENTE     -> EM_SEPARACAO, CANCELADA, NEGADA
    //   EM_SEPARACAO -> ATENDIDA (via atender), CANCELADA, NEGADA
    //   ATENDIDA, CANCELADA, NEGADA -> nenhuma (pedido finalizado)
    // Explicando fácil: o pedido só anda para frente. Quem tenta mexer em um pedido já encerrado
    // recebe a mensagem "Não é permitido alterar o status de solicitações já finalizadas.".
    @Transactional
    public Solicitacao alterarStatus(Long id, StatusSolicitacao novoStatus, String operador) {
        if (novoStatus == null) {
            throw new IllegalArgumentException("Informe o novo status da solicitação.");
        }
        if (operador == null || operador.isBlank()) {
            throw new IllegalArgumentException("Informe o operador responsável pela alteração.");
        }

        Solicitacao solicitacao = buscarOuFalhar(id);
        StatusSolicitacao atual = solicitacao.getStatus();

        if (atual.isFinalizada()) {
            throw new IllegalStateException("Não é permitido alterar o status de solicitações já finalizadas.");
        }
        if (novoStatus == atual) {
            throw new IllegalStateException("A solicitação já está com o status " + atual + ".");
        }
        if (novoStatus == StatusSolicitacao.ATENDIDA) {
            return atender(id, operador);
        }
        if (!transicaoPermitida(atual, novoStatus)) {
            throw new IllegalStateException("Transição de status inválida: " + atual + " -> " + novoStatus + ".");
        }

        registrarMudanca(solicitacao, novoStatus, operador.trim());
        return solicitacao;
    }

    // Resumo: atende a solicitação reservando as bolsas certas.
    //   1) pega só bolsas DISPONIVEL, do tipo pedido e dentro da validade;
    //   2) vem já ordenado por validade (FEFO: a que vence primeiro sai primeiro);
    //   3) fica só com as compatíveis (ABO/Rh) com o paciente, usando a matriz do AED;
    //   4) se faltar bolsa, responde 409 e NADA é alterado; se tiver, reserva todas e marca ATENDIDA.
    // Explicando fácil: a regra é "tudo ou nada". O @Transactional garante que, se der erro no meio,
    // o banco volta ao que era antes (nenhuma bolsa fica reservada pela metade).
    @Transactional
    public Solicitacao atender(Long id, String operador) {
        Solicitacao solicitacao = buscarOuFalhar(id);
        if (solicitacao.getStatus().isFinalizada()) {
            throw new IllegalStateException("Não é permitido alterar o status de solicitações já finalizadas.");
        }
        if (solicitacao.getTipoSanguineo() == null) {
            throw new IllegalStateException(
                    "Solicitação sem tipo sanguíneo do paciente: edite o pedido antes de atender.");
        }

        int necessarias = (int) solicitacao.getQuantidade();
        List<Hemocomponente> candidatas = hemocomponenteRepository.buscarParaReserva(
                solicitacao.getTipoHemocomponente(), StatusHemocomponente.DISPONIVEL, LocalDate.now());

        List<Hemocomponente> escolhidas = candidatas.stream()
                .filter(h -> compatibilidadeService.compativel(
                        solicitacao.getTipoSanguineo(), h.getDoacao().getDoador().getTipoSanguineo()))
                .limit(necessarias)
                .toList();

        if (escolhidas.size() < necessarias) {
            throw new IllegalStateException("Estoque compatível insuficiente: solicitadas " + necessarias
                    + " bolsa(s) de " + solicitacao.getTipoHemocomponente()
                    + " para paciente " + solicitacao.getTipoSanguineo()
                    + ", mas há apenas " + escolhidas.size() + " compatível(is) e dentro da validade.");
        }

        for (Hemocomponente bolsa : escolhidas) {
            bolsa.setStatus(StatusHemocomponente.RESERVADO);
            bolsa.setSolicitacao(solicitacao);
        }
        hemocomponenteRepository.saveAll(escolhidas);

        solicitacao.setDataAtendimento(LocalDateTime.now());
        registrarMudanca(solicitacao, StatusSolicitacao.ATENDIDA, nomeOperador(operador));
        return solicitacao;
    }

    // ---------------------------------------------------------------- apoio

    private boolean transicaoPermitida(StatusSolicitacao atual, StatusSolicitacao novo) {
        return switch (atual) {
            case PENDENTE -> novo == StatusSolicitacao.EM_SEPARACAO
                    || novo == StatusSolicitacao.CANCELADA
                    || novo == StatusSolicitacao.NEGADA;
            case EM_SEPARACAO -> novo == StatusSolicitacao.CANCELADA
                    || novo == StatusSolicitacao.NEGADA;
            default -> false;
        };
    }

    private void registrarMudanca(Solicitacao solicitacao, StatusSolicitacao novo, String operador) {
        StatusSolicitacao anterior = solicitacao.getStatus();
        solicitacao.setStatus(novo);
        solicitacaoRepository.save(solicitacao);
        historicoRepository.save(new HistoricoSolicitacao(solicitacao, anterior, novo, operador));
    }

    private String nomeOperador(String operador) {
        return (operador == null || operador.isBlank()) ? OPERADOR_PADRAO : operador.trim();
    }

    private Solicitacao buscarOuFalhar(Long id) {
        return solicitacaoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Solicitação não encontrada."));
    }

    private void validar(SolicitacaoRequestDTO dto) {
        if (dto.getQuantidade() < 1) {
            throw new IllegalArgumentException("A quantidade solicitada deve ser de no mínimo 1 bolsa.");
        }
        if (dto.getQuantidade() != Math.floor(dto.getQuantidade())) {
            throw new IllegalArgumentException("A quantidade solicitada deve ser um número inteiro de bolsas.");
        }
        if (dto.getTipoHemocomponente() == null) {
            throw new IllegalArgumentException("Informe o tipo de hemocomponente.");
        }
        if (dto.getTipoSanguineo() == null) {
            throw new IllegalArgumentException("Informe o tipo sanguíneo do paciente.");
        }
        if (dto.getPrioridade() == null) {
            throw new IllegalArgumentException("Informe a prioridade da solicitação.");
        }
    }
}
