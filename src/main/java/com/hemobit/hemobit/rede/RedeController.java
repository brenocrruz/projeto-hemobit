package com.hemobit.hemobit.rede;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;

/** Endpoints do painel de rede e sondas usadas pelo script de benchmarking. */
@RestController
@RequestMapping("/api/v1/rede")
public class RedeController {

    private static final int CARGA_MAX_KB = 4096;

    private final PainelRedeService painel;

    public RedeController(PainelRedeService painel) {
        this.painel = painel;
    }

    @GetMapping("/metricas")
    public ResponseEntity<Map<String, Object>> metricas() {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(painel.estado());
    }

    /** Zera a janela de métricas. Só existe no ambiente local (sem autenticação ainda, risco R3 do plano). */
    @PostMapping("/metricas/reiniciar")
    public ResponseEntity<Void> reiniciar() {
        return painel.reiniciar() ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /** Sonda leve para medir RTT de ponta a ponta. */
    @GetMapping("/ping")
    public ResponseEntity<Map<String, Object>> ping() {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(Map.of("pong", true, "servidorEm", System.currentTimeMillis()));
    }

    /** Devolve um corpo de tamanho conhecido para medir vazão (throughput) de download. */
    @GetMapping(value = "/carga", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> carga(@RequestParam(defaultValue = "256") int kb) {
        int tamanho = Math.max(1, Math.min(kb, CARGA_MAX_KB)) * 1024;
        byte[] corpo = new byte[tamanho];
        Arrays.fill(corpo, (byte) 'x');
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(corpo);
    }
}
