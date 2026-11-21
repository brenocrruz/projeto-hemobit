package com.hemobit.hemobit.service;

import com.hemobit.hemobit.domain.Doacao;
import com.hemobit.hemobit.domain.Hemocomponente;
import com.hemobit.hemobit.domain.Localidade;
import com.hemobit.hemobit.domain.StatusHemocomponente;
import com.hemobit.hemobit.domain.TipoHemocomponente;
import com.hemobit.hemobit.domain.TipoSanguineo;
import com.hemobit.hemobit.dto.HemocomponenteRequestDTO;
import com.hemobit.hemobit.dto.HemocomponenteResponseDTO;
import com.hemobit.hemobit.dto.TipoHemocomponenteDTO;
import com.hemobit.hemobit.repository.DoacaoRepository;
import com.hemobit.hemobit.repository.HemocomponenteRepository;
import com.hemobit.hemobit.repository.LocalidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
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

    public List<HemocomponenteResponseDTO> listar(TipoSanguineo tipoSanguineo, StatusHemocomponente status,
                                                  TipoHemocomponente tipo) {
        return hemocomponenteRepository.findAll().stream()
                .filter(h -> tipoSanguineo == null || h.getTipoSanguineo() == tipoSanguineo)
                .filter(h -> status == null || h.getStatus() == status)
                .filter(h -> tipo == null || h.getTipo() == tipo)
                .sorted(Comparator.comparing(Hemocomponente::getDataValidade).thenComparing(Hemocomponente::getId))
                .map(HemocomponenteResponseDTO::new)
                .collect(Collectors.toList());
    }

    public List<TipoHemocomponenteDTO> listarTipos() {
        return Arrays.stream(TipoHemocomponente.values())
                .map(TipoHemocomponenteDTO::new)
                .collect(Collectors.toList());
    }

    public Map<TipoSanguineo, Long> resumoEstoque() {
        LocalDate hoje = LocalDate.now();
        Map<TipoSanguineo, Long> resumo = new EnumMap<>(TipoSanguineo.class);
        for (TipoSanguineo tipoSanguineo : TipoSanguineo.values()) {
            resumo.put(tipoSanguineo, 0L);
        }
        for (Hemocomponente h : hemocomponenteRepository.findAll()) {
            if (h.estaDisponivel(hoje) && h.getTipoSanguineo() != null) {
                resumo.merge(h.getTipoSanguineo(), 1L, Long::sum);
            }
        }
        return resumo;
    }

    public HemocomponenteResponseDTO criar(HemocomponenteRequestDTO dto) {
        String codigo = Hemocomponente.normalizarCodigo(dto.getCodigo());
        if (dto.getDoacaoId() == null) {
            throw new IllegalArgumentException("Informe a doação de origem da bolsa.");
        }

        Doacao doacao = doacaoRepository.findById(dto.getDoacaoId())
                .orElseThrow(() -> new IllegalArgumentException("Doação não encontrada."));

        Localidade localizacaoAtual = null;
        if (dto.getLocalizacaoAtualId() != null) {
            localizacaoAtual = localidadeRepository.findById(dto.getLocalizacaoAtualId())
                    .orElseThrow(() -> new IllegalArgumentException("Localidade não encontrada."));
        }

        if (dto.getTipoSanguineo() != null
                && (doacao.getDoador() == null || dto.getTipoSanguineo() != doacao.getDoador().getTipoSanguineo())) {
            throw new IllegalArgumentException("O tipo sanguíneo informado não confere com o da doação selecionada.");
        }

        Hemocomponente hemocomponente = Hemocomponente.registrar(codigo, dto.getTipo(), dto.getQuantidade(),
                dto.getDataProducao(), dto.getDataValidade(), doacao, localizacaoAtual, LocalDate.now());

        if (hemocomponenteRepository.existsByCodigo(hemocomponente.getCodigo())) {
            throw new IllegalStateException(
                    "Já existe uma bolsa cadastrada com o código " + hemocomponente.getCodigo() + ".");
        }

        return new HemocomponenteResponseDTO(hemocomponenteRepository.save(hemocomponente));
    }

    public void deletar(Long id) {
        hemocomponenteRepository.deleteById(id);
    }
    
    public HemocomponenteResponseDTO atualizar(Long id, HemocomponenteRequestDTO dto) {
        Hemocomponente existente = hemocomponenteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Hemocomponente não encontrado."));

        existente.corrigirDados(dto.getTipo(), dto.getQuantidade(), dto.getDataProducao(),
                dto.getDataValidade(), LocalDate.now());

        return new HemocomponenteResponseDTO(hemocomponenteRepository.save(existente));
    }
}
