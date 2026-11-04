package com.hemobit.hemobit.telemetria;

import java.util.Map;

/** Evento publicado quando um veículo sai da faixa de temperatura da cadeia fria. */
public record AlertaTemperatura(Map<String, Object> leitura) {
}
