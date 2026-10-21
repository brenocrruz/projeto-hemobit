package com.hemobit.hemobit.service;

import com.hemobit.hemobit.domain.Doacao;
import com.hemobit.hemobit.domain.Doador;
import com.hemobit.hemobit.domain.UnidadeColeta;
import com.hemobit.hemobit.dto.CadastrosDTO.*;
import com.hemobit.hemobit.repository.DoacaoRepository;
import com.hemobit.hemobit.repository.UnidadeColetaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CadastroService {
    private final DoadorService doadores;
    private final UnidadeColetaRepository unidades;
    private final DoacaoRepository doacoes;

    public CadastroService(DoadorService doadores, UnidadeColetaRepository unidades, DoacaoRepository doacoes) {
        this.doadores = doadores;
        this.unidades = unidades;
        this.doacoes = doacoes;
    }

    public List<DoadorResumo> listarDoadores() {
        return doadores.listarTodos().stream().map(this::resumo).toList();
    }

    @Transactional
    public DoadorResumo criarDoador(NovoDoador dados) {
        String nome = texto(dados.nome(), "Nome do doador", true);
        // Reutiliza integralmente as validações existentes de nascimento e tipo sanguíneo.
        return resumo(doadores.salvar(new Doador(nome, null, dados.dataNascimento(), dados.tipoSanguineo())));
    }

    public List<UnidadeResumo> listarUnidades() {
        return unidades.findAll(Sort.by("nome", "id")).stream().map(this::resumo).toList();
    }

    @Transactional
    public UnidadeResumo criarUnidade(NovaUnidade dados) {
        var unidade = new UnidadeColeta(texto(dados.nome(), "Nome da unidade", true),
                texto(dados.endereco(), "Endereço", false), texto(dados.cidade(), "Cidade", false), null);
        return resumo(unidades.save(unidade));
    }

    public OpcoesDoacao opcoesDoacao() {
        return new OpcoesDoacao(doadores.listarTodos().stream()
                .map(d -> new DoadorOpcao(d.getId(), d.getNome(), d.getTipoSanguineo())).toList(),
                unidades.findAll(Sort.by("nome", "id")).stream()
                        .map(u -> new UnidadeOpcao(u.getId(), u.getNome())).toList());
    }

    public List<DoacaoResumo> listarDoacoes() {
        return doacoes.findAll(Sort.by(Sort.Direction.DESC, "id")).stream().map(this::resumo).toList();
    }

    @Transactional
    public DoacaoResumo criarDoacao(NovaDoacao dados) {
        if (dados.dataHora() == null) throw new IllegalArgumentException("Data e hora da doação são obrigatórias.");
        if (dados.quantidade() == null || !Double.isFinite(dados.quantidade()) || dados.quantidade() <= 0)
            throw new IllegalArgumentException("Quantidade deve ser finita e maior que zero.");
        if (dados.doadorId() == null || dados.doadorId() <= 0)
            throw new IllegalArgumentException("Selecione um doador existente.");
        if (dados.unidadeColetaId() == null || dados.unidadeColetaId() <= 0)
            throw new IllegalArgumentException("Selecione uma unidade de coleta existente.");
        Doador doador = doadores.buscarPorId(dados.doadorId())
                .orElseThrow(() -> new IllegalArgumentException("Doador não encontrado. Atualize as opções."));
        UnidadeColeta unidade = unidades.findById(dados.unidadeColetaId())
                .orElseThrow(() -> new IllegalArgumentException("Unidade de coleta não encontrada. Atualize as opções."));
        return resumo(doacoes.save(new Doacao(dados.dataHora(), dados.quantidade(), doador, unidade)));
    }

    private String texto(String valor, String campo, boolean obrigatorio) {
        if (valor == null || valor.isBlank()) {
            if (obrigatorio) throw new IllegalArgumentException(campo + " é obrigatório.");
            return null;
        }
        String limpo = valor.strip();
        if (limpo.length() > 255) throw new IllegalArgumentException(campo + " deve ter até 255 caracteres.");
        return limpo;
    }

    private DoadorResumo resumo(Doador d) {
        return new DoadorResumo(d.getId(), d.getNome(), d.getTipoSanguineo());
    }
    private UnidadeResumo resumo(UnidadeColeta u) {
        return new UnidadeResumo(u.getId(), u.getNome(), u.getEndereco(), u.getCidade());
    }
    private DoacaoResumo resumo(Doacao d) {
        return new DoacaoResumo(d.getId(), d.getDataHora(), d.getQuantidade(),
                d.getDoador().getId(), d.getUnidadeColeta().getId());
    }
}
