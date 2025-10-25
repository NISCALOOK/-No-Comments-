package com.ejemplo.holamundo.service;

import com.ejemplo.holamundo.model.Mensaje;
import com.ejemplo.holamundo.repository.MensajeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MensajeService {

    private final MensajeRepository repository;

    public MensajeService(MensajeRepository repository) {
        this.repository = repository; 
    }

    public List<Mensaje> findAll() {
        return repository.findAll();
    }

    public Mensaje save(Mensaje m) {
        return repository.save(m);
    }

    public Mensaje findFirst() {
        return repository.findAll().stream().findFirst().orElse(null);
    }
}
