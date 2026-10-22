package com.hemobit.hemobit.service;

import com.hemobit.hemobit.domain.TipoHemocomponente;
import com.hemobit.hemobit.dto.HemocomponenteRequestDTO;
import com.hemobit.hemobit.repository.DoacaoRepository;
import com.hemobit.hemobit.repository.HemocomponenteRepository;
import com.hemobit.hemobit.repository.LocalidadeRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class HemocomponenteServiceTest {
    @Test
    void naoCadastraHemocomponenteComDoacaoInexistente() {
        var hemocomponentes = mock(HemocomponenteRepository.class);
        var doacoes = mock(DoacaoRepository.class);
        var localidades = mock(LocalidadeRepository.class);
        var service = new HemocomponenteService(hemocomponentes, doacoes, localidades);
        var dto = new HemocomponenteRequestDTO();
        dto.setTipo(TipoHemocomponente.CONCENTRADO_HEMACIAS);
        dto.setQuantidade(100);
        dto.setDataProducao(LocalDate.of(2026, 10, 20));
        dto.setDataValidade(LocalDate.of(2026, 11, 20));
        dto.setDoacaoId(999L);
        when(doacoes.findById(999L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.criar(dto));
        verifyNoInteractions(hemocomponentes, localidades);
    }
}
