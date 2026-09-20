package com.hemobit.hemobit.service;

import com.hemobit.hemobit.domain.Veiculo;
import com.hemobit.hemobit.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VeiculoService {

    private final VeiculoRepository veiculoRepository;

    @Autowired
    public VeiculoService(VeiculoRepository veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    public List<Veiculo> listarTodos() {
        return veiculoRepository.findAll();
    }

    public Optional<Veiculo> buscarPorId(Long id) {
        return veiculoRepository.findById(id);
    }

    public Veiculo salvar(Veiculo veiculo) {
        validar(veiculo);
        return veiculoRepository.save(veiculo);
    }

    public Veiculo atualizar(Long id, Veiculo dadosAtualizados) {
        Veiculo existente = veiculoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado."));

        existente.setPlaca(dadosAtualizados.getPlaca());
        existente.setTipo(dadosAtualizados.getTipo());
        existente.setCapacidade(dadosAtualizados.getCapacidade());
        existente.setStatus(dadosAtualizados.getStatus());

        validar(existente);
        return veiculoRepository.save(existente);
    }

    public void deletar(Long id) {
        veiculoRepository.deleteById(id);
    }

    private void validar(Veiculo veiculo) {
        if (veiculo.getPlaca() == null || veiculo.getPlaca().isBlank()) {
            throw new IllegalArgumentException("Placa é obrigatória.");
        }
        if (veiculo.getCapacidade() <= 0) {
            throw new IllegalArgumentException("Capacidade deve ser maior que zero.");
        }
    }
}