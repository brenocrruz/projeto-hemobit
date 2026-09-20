package com.hemobit.hemobit.service;

import com.hemobit.hemobit.domain.Solicitacao;
import com.hemobit.hemobit.domain.StatusHemocomponente;
import com.hemobit.hemobit.domain.StatusSolicitacao;
import com.hemobit.hemobit.repository.HemocomponenteRepository;
import com.hemobit.hemobit.repository.SolicitacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SolicitacaoService {

    private final SolicitacaoRepository solicitacaoRepository;
    private final HemocomponenteRepository hemocomponenteRepository;

    @Autowired
    public SolicitacaoService(SolicitacaoRepository solicitacaoRepository,
                               HemocomponenteRepository hemocomponenteRepository) {
        this.solicitacaoRepository = solicitacaoRepository;
        this.hemocomponenteRepository = hemocomponenteRepository;
    }

    public List<Solicitacao> listarTodas() {
        return solicitacaoRepository.findAll();
    }

    public Optional<Solicitacao> buscarPorId(Long id) {
        return solicitacaoRepository.findById(id);
    }

    public Solicitacao criar(Solicitacao solicitacao) {
        if (solicitacao.getQuantidade() <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
        }
        if (solicitacao.getUnidadeSaude() == null) {
            throw new IllegalArgumentException("Solicitação precisa estar vinculada a uma unidade de saúde.");
        }
        return solicitacaoRepository.save(solicitacao);
    }

    public Solicitacao atender(Long id) {
        Solicitacao solicitacao = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada."));

        boolean temEstoque = hemocomponenteRepository.findAll().stream()
                .anyMatch(h -> h.getTipo() == solicitacao.getTipoHemocomponente()
                        && h.getStatus() == StatusHemocomponente.DISPONIVEL);

        if (!temEstoque) {
            throw new IllegalStateException("Sem estoque disponível para o tipo solicitado.");
        }

        solicitacao.setStatus(StatusSolicitacao.ATENDIDA);
        return solicitacaoRepository.save(solicitacao);
    }

    public void deletar(Long id) {
        solicitacaoRepository.deleteById(id);
    }
    public Solicitacao atualizar(Long id, Solicitacao dadosAtualizados) {
    Solicitacao existente = solicitacaoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada."));

    if (existente.getStatus() != StatusSolicitacao.PENDENTE) {
        throw new IllegalStateException("Só é possível editar solicitações com status PENDENTE.");
    }
    if (dadosAtualizados.getQuantidade() <= 0) {
        throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
    }

    existente.setTipoHemocomponente(dadosAtualizados.getTipoHemocomponente());
    existente.setQuantidade(dadosAtualizados.getQuantidade());

    return solicitacaoRepository.save(existente);
    }
}