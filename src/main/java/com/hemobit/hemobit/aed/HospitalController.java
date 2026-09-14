package com.hemobit.hemobit.aed;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hemobit.hemobit.aed.estruturas.ListaEncadeada;

@RestController
@RequestMapping("/hospitais")
public class HospitalController {

    private GerenciadorHemocentro gerenciador = new GerenciadorHemocentro();
  
    @GetMapping
    public ResponseEntity<Object> listarHospitais() {
        return ResponseEntity.status(HttpStatus.OK).body(gerenciador);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarPorId(@PathVariable Long id) {
        if (id.equals(999L)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Hospital não encontrado.");
        }
        return ResponseEntity.status(HttpStatus.OK).body("Hospital ID: " + id);
    }

    @PostMapping
    public ResponseEntity<Object> cadastrarHospital(@RequestBody Object hospital) {
        if (hospital == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dados inválidos.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(hospital);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> atualizarHospital(@PathVariable Long id, @RequestBody Object hospital) {
        return ResponseEntity.status(HttpStatus.OK).body(hospital);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerHospital(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
