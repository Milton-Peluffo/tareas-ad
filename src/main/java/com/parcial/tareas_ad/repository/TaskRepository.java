package com.parcial.tareas_ad.repository;

import com.parcial.tareas_ad.model.task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<task, Long> {
}