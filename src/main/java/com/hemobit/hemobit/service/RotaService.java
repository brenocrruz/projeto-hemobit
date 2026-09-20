package com.hemobit.hemobit.service;

import com.hemobit.hemobit.domain.Rota;
import com.hemobit.hemobit.repository.RotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RotaService {

    private final RotaRepository rotaRepository;

    @Autowired
    public RotaService(RotaRepository rotaRepository) {
        this.rotaRepository = rotaRepository;
    }

    public List<Rota> listarTodas() {
        return rotaRepository.findAll();
    }

    public Optional<Rota> buscarPorId(Long id) {
        return rotaRepository.findById(id);
    }

    public Rota salvar(Rota rota) {
        validar(rota);
        return rotaRepository.save(rota);
    }

    public Rota atualizar(Long id, Rota dadosAtualizados) {
        Rota existente = rotaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rota não encontrada."));

        existente.setOrigem(dadosAtualizados.getOrigem());
        existente.setDestino(dadosAtualizados.getDestino());
        existente.setDistancia(dadosAtualizados.getDistancia());
        existente.setTempoEstimado(dadosAtualizados.getTempoEstimado());

        validar(existente);
        return rotaRepository.save(existente);
    }

    public void deletar(Long id) {
        rotaRepository.deleteById(id);
    }

    private void validar(Rota rota) {
        if (rota.getOrigem() == null || rota.getDestino() == null) {
            throw new IllegalArgumentException("Origem e destino são obrigatórios.");
        }
        if (rota.getOrigem().getId() != null && rota.getOrigem().getId().equals(
                rota.getDestino().getId())) {
            throw new IllegalArgumentException("Origem e destino não podem ser o mesmo local.");
        }
        if (rota.getDistancia() <= 0) {
            throw new IllegalArgumentException("Distância deve ser maior que zero.");
        }
    }
}