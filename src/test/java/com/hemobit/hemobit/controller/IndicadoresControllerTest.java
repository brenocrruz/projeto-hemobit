package com.hemobit.hemobit.controller;

import com.hemobit.hemobit.domain.StatusHemocomponente;
import com.hemobit.hemobit.domain.TipoSanguineo;
import com.hemobit.hemobit.repository.HemocomponenteRepository;
import com.hemobit.hemobit.service.EstatisticaService;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class IndicadoresControllerTest {
    private final HemocomponenteRepository repository = mock(HemocomponenteRepository.class);
    private final IndicadoresController controller =
            new IndicadoresController(new EstatisticaService(), repository);

    @Test
    void retornaOitoGruposZeradosQuandoEstoqueVazio() {
        when(repository.contarEstoquePorTipo(any(), any())).thenReturn(List.of());
        Map<String, Object> resposta = controller.getEstoqueReal().getBody();
        assertNotNull(resposta);
        assertEquals(Map.of("A+", 0L, "A-", 0L, "B+", 0L, "B-", 0L,
                "AB+", 0L, "AB-", 0L, "O+", 0L, "O-", 0L), resposta.get("dados"));
        assertEquals("registros", ((Map<?, ?>) resposta.get("metadados")).get("unidade"));
    }

    @Test
    void preencheContagensReaisEConsultaDisponiveisNaDataAtual() {
        var grupo = mock(HemocomponenteRepository.EstoquePorTipo.class);
        when(grupo.getTipoSanguineo()).thenReturn(TipoSanguineo.O_NEGATIVO);
        when(grupo.getTotal()).thenReturn(3L);
        when(repository.contarEstoquePorTipo(any(), any())).thenReturn(List.of(grupo));
        LocalDate antes = LocalDate.now();
        Map<String, Object> resposta = controller.getEstoqueReal().getBody();
        assertNotNull(resposta);
        Map<?, ?> dados = (Map<?, ?>) resposta.get("dados");
        assertEquals(8, dados.size());
        assertEquals(3L, dados.get("O-"));
        assertEquals(0L, dados.get("A+"));
        LocalDate depois = LocalDate.now();
        verify(repository).contarEstoquePorTipo(eq(StatusHemocomponente.DISPONIVEL),
                argThat(data -> !data.isBefore(antes) && !data.isAfter(depois)));
    }

    @Test
    void preservaEstoqueDemonstrativoSemConsultarBanco() {
        assertNotNull(controller.getEstoquePorTipo().getBody());
        verifyNoInteractions(repository);
    }
}
