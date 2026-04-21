package com.parcial.tareas_ad.repository;

import com.parcial.tareas_ad.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    
    // Encontrar tareas por estado
    List<Task> findByEstado(String estado);
    
    // Encontrar tareas por creador
    List<Task> findByCreadoPor(String creadoPor);
    
    // Encontrar tareas por estado y creador
    List<Task> findByEstadoAndCreadoPor(String estado, String creadoPor);
    
    // Contar tareas por estado
    long countByEstado(String estado);
    
    // Contar tareas por creador
    long countByCreadoPor(String creadoPor);
    
    // Buscar tareas por nombre (contiene)
    @Query("SELECT t FROM Task t WHERE t.nombre LIKE %:nombre%")
    List<Task> findByNombreContaining(@Param("nombre") String nombre);
    
    // Encontrar tareas más recientes
    @Query("SELECT t FROM Task t ORDER BY t.fechaCreacion DESC")
    List<Task> findMostRecent();
}