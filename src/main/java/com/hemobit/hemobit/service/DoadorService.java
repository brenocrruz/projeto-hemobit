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
        return doadorRepository.save(doador);
    }

    public void deletar(Long id) {
        doadorRepository.deleteById(id);
    }
}