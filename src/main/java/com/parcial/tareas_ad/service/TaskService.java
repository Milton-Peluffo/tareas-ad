package com.parcial.tareas_ad.service;

import com.parcial.tareas_ad.model.Task;
import com.parcial.tareas_ad.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public List<Task> findAll() {
        return repository.findAll();
    }

    public Task save(Task task) {
        return repository.save(task);
    }

    public Task findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public List<Task> findByEstado(String estado) {
        return repository.findByEstado(estado);
    }

    public List<Task> findByCreadoPor(String creadoPor) {
        return repository.findByCreadoPor(creadoPor);
    }

    public long countByEstado(String estado) {
        return repository.countByEstado(estado);
    }

    public long countByCreadoPor(String creadoPor) {
        return repository.countByCreadoPor(creadoPor);
    }

    public List<Task> findByNombreContaining(String nombre) {
        return repository.findByNombreContaining(nombre);
    }

    public List<Task> findMostRecent() {
        return repository.findMostRecent();
    }

    // Métodos legacy para compatibilidad
    public List<Task> listAll() {
        return findAll();
    }
}