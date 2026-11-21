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

    @Transactional
    public Solicitacao criar(SolicitacaoRequestDTO dto) {
        if (dto.getUnidadeSaudeId() == null) {
            throw new IllegalArgumentException("Solicitação precisa estar vinculada a uma unidade de saúde.");
        }
        UnidadeSaude unidade = unidadeSaudeRepository.findById(dto.getUnidadeSaudeId())
                .orElseThrow(() -> new IllegalArgumentException("Unidade de saúde não encontrada."));

        Solicitacao solicitacao = Solicitacao.abrir(unidade, dto.getTipoHemocomponente(),
                dto.getTipoSanguineo(), dto.getQuantidade(), dto.getPrioridade());
        Solicitacao salva = solicitacaoRepository.save(solicitacao);
        historicoRepository.save(new HistoricoSolicitacao(salva, null, StatusSolicitacao.PENDENTE, "sistema"));
        return salva;
    }

    @Transactional
    public Solicitacao atualizar(Long id, SolicitacaoRequestDTO dto) {
        Solicitacao existente = buscarOuFalhar(id);
        existente.alterarPedido(dto.getTipoHemocomponente(), dto.getTipoSanguineo(),
                dto.getQuantidade(), dto.getPrioridade());
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

    @Transactional
    public Solicitacao alterarStatus(Long id, StatusSolicitacao novoStatus, String operador) {
        if (novoStatus == null) {
            throw new IllegalArgumentException("Informe o novo status da solicitação.");
        }
        if (operador == null || operador.isBlank()) {
            throw new IllegalArgumentException("Informe o operador responsável pela alteração.");
        }
        if (novoStatus == StatusSolicitacao.ATENDIDA) {
            return atender(id, operador);
        }

        Solicitacao solicitacao = buscarOuFalhar(id);
        StatusSolicitacao anterior = solicitacao.mudarStatus(novoStatus);
        registrarMudanca(solicitacao, anterior, operador.trim());
        return solicitacao;
    }

    @Transactional
    public Solicitacao atender(Long id, String operador) {
        Solicitacao solicitacao = buscarOuFalhar(id);
        StatusSolicitacao anterior = solicitacao.getStatus();
        List<Hemocomponente> candidatas = hemocomponenteRepository.buscarParaReserva(
                solicitacao.getTipoHemocomponente(), StatusHemocomponente.DISPONIVEL, LocalDate.now());

        List<Hemocomponente> reservadas = solicitacao.atender(candidatas,
                compatibilidadeService::compativel, LocalDate.now(), LocalDateTime.now());

        hemocomponenteRepository.saveAll(reservadas);
        registrarMudanca(solicitacao, anterior, nomeOperador(operador));
        return solicitacao;
    }

    private void registrarMudanca(Solicitacao solicitacao, StatusSolicitacao anterior, String operador) {
        solicitacaoRepository.save(solicitacao);
        historicoRepository.save(new HistoricoSolicitacao(solicitacao, anterior, solicitacao.getStatus(), operador));
    }

    private String nomeOperador(String operador) {
        return (operador == null || operador.isBlank()) ? OPERADOR_PADRAO : operador.trim();
    }

    private Solicitacao buscarOuFalhar(Long id) {
        return solicitacaoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Solicitação não encontrada."));
    }
}
