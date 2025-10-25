package com.ejemplo.holamundo.controller;
import com.ejemplo.holamundo.model.Mensaje;
import com.ejemplo.holamundo.service.MensajeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hola")
public class MensajeController {
    private final MensajeService service;
    
    public MensajeController(MensajeService service) {
        this.service = service;
    }
    
    @GetMapping
    public ResponseEntity<Mensaje> getHola() {
        Mensaje m = service.findFirst();
        if (m == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(m);
    }
    
    @PostMapping
    public ResponseEntity<Mensaje> createHola(@RequestBody Mensaje nuevo) {
        Mensaje saved = service.save(nuevo);
        return ResponseEntity.status(201).body(saved);
    }
}