package com.hemobit.hemobit.service;

import com.hemobit.hemobit.domain.Doacao;
import com.hemobit.hemobit.domain.Hemocomponente;
import com.hemobit.hemobit.domain.Localidade;
import com.hemobit.hemobit.dto.HemocomponenteRequestDTO;
import com.hemobit.hemobit.dto.HemocomponenteResponseDTO;
import com.hemobit.hemobit.repository.DoacaoRepository;
import com.hemobit.hemobit.repository.HemocomponenteRepository;
import com.hemobit.hemobit.repository.LocalidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HemocomponenteService {

    private final HemocomponenteRepository hemocomponenteRepository;
    private final DoacaoRepository doacaoRepository;
    private final LocalidadeRepository localidadeRepository;

    @Autowired
    public HemocomponenteService(HemocomponenteRepository hemocomponenteRepository,
                                  DoacaoRepository doacaoRepository,
                                  LocalidadeRepository localidadeRepository) {
        this.hemocomponenteRepository = hemocomponenteRepository;
        this.doacaoRepository = doacaoRepository;
        this.localidadeRepository = localidadeRepository;
    }

    public List<HemocomponenteResponseDTO> listarTodos() {
        return hemocomponenteRepository.findAll().stream()
                .map(HemocomponenteResponseDTO::new)
                .collect(Collectors.toList());
    }

    public Optional<HemocomponenteResponseDTO> buscarPorId(Long id) {
        return hemocomponenteRepository.findById(id).map(HemocomponenteResponseDTO::new);
    }

    public HemocomponenteResponseDTO criar(HemocomponenteRequestDTO dto) {
        if (dto.getDataValidade() == null || dto.getDataProducao() == null
                || dto.getDataValidade().isBefore(dto.getDataProducao())) {
            throw new IllegalArgumentException("Data de validade não pode ser anterior à data de produção.");
        }
        if (dto.getQuantidade() <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
        }

        Doacao doacao = doacaoRepository.findById(dto.getDoacaoId())
                .orElseThrow(() -> new IllegalArgumentException("Doação não encontrada."));

        Localidade localizacaoAtual = null;
        if (dto.getLocalizacaoAtualId() != null) {
            localizacaoAtual = localidadeRepository.findById(dto.getLocalizacaoAtualId())
                    .orElseThrow(() -> new IllegalArgumentException("Localidade não encontrada."));
        }

        Hemocomponente hemocomponente = new Hemocomponente(
                dto.getTipo(), dto.getQuantidade(), dto.getDataProducao(),
                dto.getDataValidade(), doacao, localizacaoAtual
        );

        return new HemocomponenteResponseDTO(hemocomponenteRepository.save(hemocomponente));
    }

    public void deletar(Long id) {
        hemocomponenteRepository.deleteById(id);
    }
    
    public HemocomponenteResponseDTO atualizar(Long id, HemocomponenteRequestDTO dto) {
    Hemocomponente existente = hemocomponenteRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Hemocomponente não encontrado."));

    if (dto.getDataValidade() == null || dto.getDataProducao() == null
            || dto.getDataValidade().isBefore(dto.getDataProducao())) {
        throw new IllegalArgumentException("Data de validade não pode ser anterior à data de produção.");
    }
    if (dto.getQuantidade() <= 0) {
        throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
    }

    existente.setTipo(dto.getTipo());
    existente.setQuantidade(dto.getQuantidade());
    existente.setDataProducao(dto.getDataProducao());
    existente.setDataValidade(dto.getDataValidade());

    return new HemocomponenteResponseDTO(hemocomponenteRepository.save(existente));
}
}