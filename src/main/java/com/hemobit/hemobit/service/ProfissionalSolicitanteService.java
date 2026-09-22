package com.hemobit.hemobit.service;

import com.hemobit.hemobit.domain.ProfissionalSolicitante;
import com.hemobit.hemobit.repository.ProfissionalSolicitanteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfissionalSolicitanteService {

    private final ProfissionalSolicitanteRepository repository;

    public ProfissionalSolicitanteService(ProfissionalSolicitanteRepository repository) {
        this.repository = repository;
    }

    public ProfissionalSolicitante cadastrar(ProfissionalSolicitante profissional) {

        if (repository.existsByCrmMatricula(profissional.getCrmMatricula())) {
            throw new RuntimeException("CRM já cadastrado no sistema!");
        }

        return repository.save(profissional);
    }

    public List<ProfissionalSolicitante> listarTodos(){
        return repository.findAll();
    }

    public Optional<ProfissionalSolicitante> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public ProfissionalSolicitante atualizar(Long id, ProfissionalSolicitante profissional) {

        ProfissionalSolicitante existente = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado!"));

        if (!existente.getCrmMatricula().equals(profissional.getCrmMatricula())
                && repository.existsByCrmMatricula(profissional.getCrmMatricula())) {

            throw new RuntimeException("CRM já cadastrado no sistema!");
        }

        existente.setNome(profissional.getNome());
        existente.setCrmMatricula(profissional.getCrmMatricula());
        existente.setEmail(profissional.getEmail());
        existente.setUnidadeSaude(profissional.getUnidadeSaude());

        return repository.save(existente);
    }

    public void deletar(Long id) {

        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Profissional não encontrado!");
        }

        repository.deleteById(id);
    }
}