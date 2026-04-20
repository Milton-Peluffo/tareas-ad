package com.parcial.tareas_ad.service;

import com.parcial.tareas_ad.model.task;
import com.parcial.tareas_ad.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public List<task> listAll() {
        return repository.findAll();
    }

    public task save(task task) {
        return repository.save(task);
    }

    public task findById(Long id) {
        return repository.findById(id).orElse(null);
    }
}