package com.parcial.tareas_ad.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_permissions")
public class UserPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "can_create_tasks")
    private Boolean canCreateTasks = false;

    @Column(name = "can_edit_tasks")
    private Boolean canEditTasks = false;

    @Column(name = "can_delete_tasks")
    private Boolean canDeleteTasks = false;

    @Column(name = "can_change_status")
    private Boolean canChangeStatus = false;

    @Column(name = "is_admin")
    private Boolean isAdmin = false;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "creado_por")
    private String creadoPor;

    // Constructor por defecto
    public UserPermission() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    // Constructor con parámetros
    public UserPermission(String username, String creadoPor) {
        this();
        this.username = username;
        this.creadoPor = creadoPor;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public Boolean getCanCreateTasks() {
        return canCreateTasks;
    }

    public void setCanCreateTasks(Boolean canCreateTasks) {
        this.canCreateTasks = canCreateTasks;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public Boolean getCanEditTasks() {
        return canEditTasks;
    }

    public void setCanEditTasks(Boolean canEditTasks) {
        this.canEditTasks = canEditTasks;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public Boolean getCanDeleteTasks() {
        return canDeleteTasks;
    }

    public void setCanDeleteTasks(Boolean canDeleteTasks) {
        this.canDeleteTasks = canDeleteTasks;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public Boolean getCanChangeStatus() {
        return canChangeStatus;
    }

    public void setCanChangeStatus(Boolean canChangeStatus) {
        this.canChangeStatus = canChangeStatus;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public String getCreadoPor() {
        return creadoPor;
    }

    public void setCreadoPor(String creadoPor) {
        this.creadoPor = creadoPor;
    }

    // Método utilitario para verificar si tiene algún permiso
    public boolean hasAnyPermission() {
        return canCreateTasks || canEditTasks || canDeleteTasks || canChangeStatus || isAdmin;
    }

    // Método para obtener descripción de permisos
    public String getPermissionsDescription() {
        StringBuilder sb = new StringBuilder();
        if (isAdmin) {
            sb.append("Administrador");
        } else {
            if (canCreateTasks) sb.append("Crear");
            if (canEditTasks) sb.append(sb.length() > 0 ? ", Editar" : "Editar");
            if (canDeleteTasks) sb.append(sb.length() > 0 ? ", Eliminar" : "Eliminar");
            if (canChangeStatus) sb.append(sb.length() > 0 ? ", Cambiar Estado" : "Cambiar Estado");
        }
        return sb.length() > 0 ? sb.toString() : "Sin permisos";
    }
}
