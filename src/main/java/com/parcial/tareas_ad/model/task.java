package com.parcial.tareas_ad.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tasks")
public class task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    private String status; // PENDIENTE, COMPLETADA

    // Getters y Setters
}