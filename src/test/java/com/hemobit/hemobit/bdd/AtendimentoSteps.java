package com.hemobit.hemobit.bdd;

import com.hemobit.hemobit.domain.Doacao;
import com.hemobit.hemobit.domain.Doador;
import com.hemobit.hemobit.domain.Hemocomponente;
import com.hemobit.hemobit.domain.Prioridade;
import com.hemobit.hemobit.domain.Solicitacao;
import com.hemobit.hemobit.domain.StatusHemocomponente;
import com.hemobit.hemobit.domain.TipoHemocomponente;
import com.hemobit.hemobit.domain.TipoSanguineo;
import com.hemobit.hemobit.domain.UnidadeSaude;
import com.hemobit.hemobit.service.CompatibilidadeService;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AtendimentoSteps {
    private static final LocalDate HOJE = LocalDate.of(2026, 11, 18);
    private static final LocalDateTime AGORA = LocalDateTime.of(2026, 11, 18, 10, 30);

    private final CompatibilidadeService compatibilidade = new CompatibilidadeService();
    private final Map<String, Hemocomponente> estoque = new LinkedHashMap<>();
    private Solicitacao solicitacao;
    private String erro;

    @Given("uma solicitação de {int} bolsas de concentrado de hemácias para paciente {string}")
    public void umaSolicitacaoDeBolsas(int quantidade, String tipoSanguineo) {
        solicitacao = abrir(quantidade, tipoSanguineo);
    }

    @Given("as seguintes bolsas disponíveis em ordem de validade:")
    public void asSeguintesBolsasDisponiveis(DataTable tabela) {
        int dias = 1;
        for (Map<String, String> linha : tabela.asMaps()) {
            Hemocomponente bolsa = bolsa(linha.get("codigo"), TipoSanguineo.valueOf(linha.get("tipoSanguineo")), dias++);
            estoque.put(bolsa.getCodigo(), bolsa);
        }
    }

    @When("a solicitação é atendida")
    public void aSolicitacaoEAtendida() {
        try {
            solicitacao.atender(new ArrayList<>(estoque.values()), compatibilidade::compativel, HOJE, AGORA);
        } catch (IllegalStateException e) {
            erro = e.getMessage();
        }
    }

    @When("tento abrir uma solicitação de {int} bolsas de concentrado de hemácias para paciente {string}")
    public void tentoAbrirUmaSolicitacao(int quantidade, String tipoSanguineo) {
        try {
            solicitacao = abrir(quantidade, tipoSanguineo);
        } catch (IllegalArgumentException e) {
            erro = e.getMessage();
        }
    }

    @Then("as bolsas {string} ficam reservadas")
    public void asBolsasFicamReservadas(String codigos) {
        Arrays.stream(codigos.split(",\\s*")).forEach(codigo -> {
            assertEquals(StatusHemocomponente.RESERVADO, estoque.get(codigo).getStatus());
            assertEquals(solicitacao, estoque.get(codigo).getSolicitacao());
        });
    }

    @Then("a bolsa {string} continua disponível")
    public void aBolsaContinuaDisponivel(String codigo) {
        assertEquals(StatusHemocomponente.DISPONIVEL, estoque.get(codigo).getStatus());
    }

    @Then("nenhuma bolsa fica reservada")
    public void nenhumaBolsaFicaReservada() {
        assertTrue(estoque.values().stream().allMatch(b -> b.getStatus() == StatusHemocomponente.DISPONIVEL));
    }

    @Then("a solicitação fica com status {string}")
    public void aSolicitacaoFicaComStatus(String status) {
        assertEquals(status, solicitacao.getStatus().name());
    }

    @Then("o atendimento é recusado com a mensagem {string}")
    public void oAtendimentoERecusado(String mensagem) {
        assertEquals(mensagem, erro);
    }

    @Then("o pedido é recusado com a mensagem {string}")
    public void oPedidoERecusado(String mensagem) {
        assertEquals(mensagem, erro);
    }

    private Solicitacao abrir(int quantidade, String tipoSanguineo) {
        UnidadeSaude hospital = new UnidadeSaude("Hospital São Marcos", "Rua A", "Recife", "8130000000");
        return Solicitacao.abrir(hospital, TipoHemocomponente.CONCENTRADO_HEMACIAS,
                TipoSanguineo.valueOf(tipoSanguineo), quantidade, Prioridade.EMERGENCIA);
    }

    private Hemocomponente bolsa(String codigo, TipoSanguineo sangue, int diasParaVencer) {
        Doador doador = new Doador();
        doador.setTipoSanguineo(sangue);
        Doacao doacao = new Doacao();
        doacao.setDoador(doador);
        return Hemocomponente.registrar(codigo, TipoHemocomponente.CONCENTRADO_HEMACIAS, 300, HOJE.minusDays(5),
                HOJE.plusDays(diasParaVencer), doacao, null, HOJE);
    }
}
