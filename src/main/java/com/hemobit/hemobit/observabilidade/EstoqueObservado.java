package com.hemobit.hemobit.observabilidade;

import com.hemobit.hemobit.domain.StatusHemocomponente;
import com.hemobit.hemobit.repository.HemocomponenteRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.function.Consumer;

@Component
public class EstoqueObservado {
    private static final Logger LOG = LoggerFactory.getLogger(EstoqueObservado.class);

    private final HemocomponenteRepository repository;
    private final ObservationRegistry observations;
    private final Counter consultas;
    private final Timer duracao;

    public EstoqueObservado(HemocomponenteRepository repository, ObservationRegistry observations,
                           MeterRegistry meters) {
        this.repository = repository;
        this.observations = observations;
        consultas = Counter.builder("hemobit.estoque.consultas")
                .description("Consultas concluidas ao estoque disponivel")
                .register(meters);
        duracao = Timer.builder("hemobit.estoque.consulta.duracao")
                .description("Tempo da agregacao de estoque disponivel no banco")
                .register(meters);
    }

    public void consultar(LocalDate data, Consumer<HemocomponenteRepository.EstoquePorTipo> receber) {
        Observation.createNotStarted("hemobit.estoque.consultar", observations).observe(() -> {
            var linhas = Observation.createNotStarted("hemobit.estoque.agregar", observations)
                    .observe(() -> duracao.record(
                            () -> repository.contarEstoquePorTipo(StatusHemocomponente.DISPONIVEL, data)));
            linhas.forEach(receber);
            consultas.increment();
            LOG.info("Consulta ao estoque disponivel concluida");
        });
    }
}
