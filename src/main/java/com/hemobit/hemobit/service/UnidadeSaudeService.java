package com.hemobit.hemobit.service;

import com.hemobit.hemobit.domain.UnidadeSaude;
import com.hemobit.hemobit.repository.UnidadeSaudeRepository;
import com.hemobit.hemobit.util.ValidadorCnpj;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hemobit.hemobit.util.ValidadorCnpj;

import java.util.List;
import java.util.Optional;

@Service
public class UnidadeSaudeService {

    private final UnidadeSaudeRepository unidadeSaudeRepository;

    @Autowired
    public UnidadeSaudeService(UnidadeSaudeRepository unidadeSaudeRepository) {
        this.unidadeSaudeRepository = unidadeSaudeRepository;
    }

    public List<UnidadeSaude> listarTodas() {
        return unidadeSaudeRepository.findAll();
    }

    public Optional<UnidadeSaude> buscarPorId(Long id) {
        return unidadeSaudeRepository.findById(id);
    }

    public UnidadeSaude salvar(UnidadeSaude unidadeSaude) {
        validar(unidadeSaude);
        return unidadeSaudeRepository.save(unidadeSaude);
    }

    public UnidadeSaude atualizar(Long id, UnidadeSaude dadosAtualizados) {

        UnidadeSaude existente = unidadeSaudeRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Unidade de saúde não encontrada."
                        ));

        existente.setNome(dadosAtualizados.getNome());
        existente.setEndereco(dadosAtualizados.getEndereco());
        existente.setCidade(dadosAtualizados.getCidade());
        existente.setTelefone(dadosAtualizados.getTelefone());
        existente.setCnpj(dadosAtualizados.getCnpj());

        validar(existente);

        return unidadeSaudeRepository.save(existente);
    }

    public void deletar(Long id) {
        unidadeSaudeRepository.deleteById(id);
    }

    private void validar(UnidadeSaude unidadeSaude) {

        if (unidadeSaude.getNome() == null ||
                unidadeSaude.getNome().isBlank()) {

            throw new IllegalArgumentException(
                    "Nome da unidade de saúde é obrigatório."
            );
        }

        if (unidadeSaude.getCidade() == null ||
                unidadeSaude.getCidade().isBlank()) {

            throw new IllegalArgumentException(
                    "Cidade é obrigatória."
            );
        }

        if (!ValidadorCnpj.isValid(unidadeSaude.getCnpj())) {

            throw new IllegalArgumentException(
                    "CNPJ inválido. Digite um número válido para prosseguir."
            );
        }
        if (!ValidadorCnpj.isValid(unidadeSaude.getCnpj())) {
            throw new IllegalArgumentException(
                    "CNPJ inválido. Digite um número válido para prosseguir."
            );
        }
    }
}