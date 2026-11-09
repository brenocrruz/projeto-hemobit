package com.hemobit.hemobit.service;

import com.hemobit.hemobit.domain.Doacao;
import com.hemobit.hemobit.domain.Hemocomponente;
import com.hemobit.hemobit.domain.Localidade;
import com.hemobit.hemobit.domain.StatusHemocomponente;
import com.hemobit.hemobit.domain.TipoHemocomponente;
import com.hemobit.hemobit.domain.TipoSanguineo;
import com.hemobit.hemobit.dto.HemocomponenteRequestDTO;
import com.hemobit.hemobit.dto.HemocomponenteResponseDTO;
import com.hemobit.hemobit.repository.DoacaoRepository;
import com.hemobit.hemobit.repository.HemocomponenteRepository;
import com.hemobit.hemobit.repository.LocalidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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

    public Map<TipoSanguineo, Long> resumoEstoque() {
        LocalDate hoje = LocalDate.now();
        Map<TipoSanguineo, Long> resumo = new EnumMap<>(TipoSanguineo.class);
        for (TipoSanguineo tipoSanguineo : TipoSanguineo.values()) {
            resumo.put(tipoSanguineo, 0L);
        }
        for (Hemocomponente h : hemocomponenteRepository.findAll()) {
            if (h.getStatus() == StatusHemocomponente.DISPONIVEL
                    && !h.getDataValidade().isBefore(hoje)
                    && h.getTipoSanguineo() != null) {
                resumo.merge(h.getTipoSanguineo(), 1L, Long::sum);
            }
        }
        return resumo;
    }

    public HemocomponenteResponseDTO criar(HemocomponenteRequestDTO dto) {
        if (dto.getCodigo() == null || dto.getCodigo().isBlank()) {
            throw new IllegalArgumentException("Informe o código da bolsa.");
        }
        if (dto.getTipo() == null) {
            throw new IllegalArgumentException("Informe o tipo do hemocomponente.");
        }
        if (dto.getDataProducao() == null || dto.getDataValidade() == null) {
            throw new IllegalArgumentException("Informe as datas de produção e de validade.");
        }
        if (dto.getDataValidade().isBefore(dto.getDataProducao())) {
            throw new IllegalArgumentException("Data de validade não pode ser anterior à data de produção.");
        }
        if (dto.getDataValidade().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Não é permitido cadastrar bolsas com data de validade vencida!");
        }
        if (dto.getQuantidade() <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
        }
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

        String codigo = dto.getCodigo().trim().toUpperCase(Locale.ROOT);
        if (hemocomponenteRepository.existsByCodigo(codigo)) {
            throw new IllegalStateException("Já existe uma bolsa cadastrada com o código " + codigo + ".");
        }

        Hemocomponente hemocomponente = new Hemocomponente(
                dto.getTipo(), dto.getQuantidade(), dto.getDataProducao(),
                dto.getDataValidade(), doacao, localizacaoAtual
        );
        hemocomponente.setCodigo(codigo);

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