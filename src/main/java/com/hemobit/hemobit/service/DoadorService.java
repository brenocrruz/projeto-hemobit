package com.hemobit.hemobit.service;

import com.hemobit.hemobit.domain.Doador;
import com.hemobit.hemobit.repository.DoadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoadorService {

    private final DoadorRepository doadorRepository;

    @Autowired
    public DoadorService(DoadorRepository doadorRepository) {
        this.doadorRepository = doadorRepository;
    }

    public List<Doador> listarTodos() {
        return doadorRepository.findAll();
    }

    public Optional<Doador> buscarPorId(Long id) {
        return doadorRepository.findById(id);
    }

    public Doador salvar(Doador doador) {
        validar(doador);
        return doadorRepository.save(doador);
    }

    public void deletar(Long id) {
        doadorRepository.deleteById(id);
    }

    public Doador atualizar(Long id, Doador dadosAtualizados) {
    Doador existente = doadorRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Doador não encontrado."));

    existente.setNome(dadosAtualizados.getNome());
    existente.setTelefone(dadosAtualizados.getTelefone());
    existente.setDataNascimento(dadosAtualizados.getDataNascimento());
    existente.setTipoSanguineo(dadosAtualizados.getTipoSanguineo());

    validar(existente);
    return doadorRepository.save(existente);
}

private void validar(Doador doador) {
    if (doador.getNome() == null || doador.getNome().isBlank()) {
        throw new IllegalArgumentException("Nome do doador é obrigatório.");
    }
    if (doador.getDataNascimento() == null || doador.getDataNascimento().isAfter(java.time.LocalDate.now())) {
        throw new IllegalArgumentException("Data de nascimento inválida.");
    }
    if (doador.getTipoSanguineo() == null) {
        throw new IllegalArgumentException("Tipo sanguíneo é obrigatório.");
    }
}
}