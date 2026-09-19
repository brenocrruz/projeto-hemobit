package com.hemobit.hemobit.aed;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hemobit.hemobit.aed.estruturas.ListaEncadeada;

@RestController
@RequestMapping("/aed/hospitais")
public class HospitalDemoController {

    private ListaEncadeada<String> listaHospitais = new ListaEncadeada<>();

    @GetMapping
    public ResponseEntity<Object> listarHospitais() {
        return ResponseEntity.status(HttpStatus.OK).body("Lista de hospitais cadastrados.");
    }
    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarPorId(@PathVariable Long id) {
        if (id <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Identificador invalido.");
        }
        if (id.equals(999L)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Hospital nao localizado.");
        }
        return ResponseEntity.status(HttpStatus.OK).body("Hospital id: " + id);
    }

    @PostMapping
    public ResponseEntity<Object> cadastrarHospital(@RequestBody String dadosHospital) {
        if (dadosHospital == null || dadosHospital.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dados de cadastro incompletos.");
        }
        
        listaHospitais.adicionar(dadosHospital);
        return ResponseEntity.status(HttpStatus.CREATED).body("Hospital cadastrado com sucesso.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> atualizarHospital(@PathVariable Long id, @RequestBody String dadosAtualizados) {
        return ResponseEntity.status(HttpStatus.OK).body("Cadastro atualizado.");
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerHospital(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
