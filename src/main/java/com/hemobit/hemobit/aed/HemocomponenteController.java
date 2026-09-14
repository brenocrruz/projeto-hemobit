package com.hemobit.hemobit.aed;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hemobit.hemobit.aed.estruturas.Pilha;

@RestController
@RequestMapping("/hemocomponentes")
public class HemocomponenteController {

    private Pilha<String> estoqueBolsas = new Pilha<>();

    @GetMapping
    public ResponseEntity<Object> listarEstoque() {
        return ResponseEntity.status(HttpStatus.OK).body("Estoque de hemocomponentes.");
    }

    @PostMapping
    public ResponseEntity<Object> cadastrarBolsa(@RequestBody String dadosBolsa) {
        if (dadosBolsa == null || dadosBolsa.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dados da bolsa incompletos.");
        }
        estoqueBolsas.empilhar(dadosBolsa);
        return ResponseEntity.status(HttpStatus.CREATED).body("Bolsa registrada no estoque.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> darBaixaBolsa(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
