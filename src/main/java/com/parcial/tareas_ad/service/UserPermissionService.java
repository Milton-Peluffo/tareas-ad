package com.parcial.tareas_ad.service;

import com.parcial.tareas_ad.model.UserPermission;
import com.parcial.tareas_ad.repository.UserPermissionRepository;
import com.parcial.tareas_ad.util.SecurityUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserPermissionService {

    private final UserPermissionRepository repository;

    public UserPermissionService(UserPermissionRepository repository) {
        this.repository = repository;
    }

    // Obtener permisos de un usuario
    public Optional<UserPermission> findByUsername(String username) {
        return repository.findByUsername(username);
    }

    // Obtener todos los permisos
    public List<UserPermission> findAll() {
        return repository.findAllOrderedByUsername();
    }

    // Guardar permisos de usuario
    public UserPermission save(UserPermission userPermission) {
        return repository.save(userPermission);
    }

    // Crear o actualizar permisos de usuario
    public UserPermission createOrUpdatePermissions(String username, String creadoPor) {
        Optional<UserPermission> existing = repository.findByUsername(username);
        
        if (existing.isPresent()) {
            return existing.get();
        } else {
            UserPermission newPermission = new UserPermission(username, creadoPor);
            return repository.save(newPermission);
        }
    }

    // Eliminar permisos de usuario
    public void deleteByUsername(String username) {
        repository.findByUsername(username).ifPresent(repository::delete);
    }

    // Verificar si un usuario tiene permiso específico
    public boolean hasPermission(String username, String permissionType) {
        Optional<UserPermission> userPermission = repository.findByUsername(username);
        if (userPermission.isEmpty()) {
            return false;
        }

        UserPermission permission = userPermission.get();
        if (permission.getIsAdmin()) {
            return true; // Admin tiene todos los permisos
        }

        if ("CREATE_TASKS".equals(permissionType)) {
            return permission.getCanCreateTasks();
        } else if ("EDIT_TASKS".equals(permissionType)) {
            return permission.getCanEditTasks();
        } else if ("DELETE_TASKS".equals(permissionType)) {
            return permission.getCanDeleteTasks();
        } else if ("CHANGE_STATUS".equals(permissionType)) {
            return permission.getCanChangeStatus();
        } else {
            return false;
        }
    }

    // Verificar si el usuario actual tiene permiso
    public boolean currentUserHasPermission(String permission) {
        String currentUser = SecurityUtil.getCurrentUsername();
        return hasPermission(currentUser, permission);
    }

    // Verificar si el usuario actual es administrador
    public boolean isCurrentUserAdmin() {
        String currentUser = SecurityUtil.getCurrentUsername();
        Optional<UserPermission> userPermission = repository.findByUsername(currentUser);
        return userPermission.map(UserPermission::getIsAdmin).orElse(false);
    }

    // Obtener usuarios por tipo de permiso
    public List<UserPermission> getUsersWithCreatePermission() {
        return repository.findUsersWithCreatePermission();
    }

    public List<UserPermission> getUsersWithEditPermission() {
        return repository.findUsersWithEditPermission();
    }

    public List<UserPermission> getUsersWithDeletePermission() {
        return repository.findUsersWithDeletePermission();
    }

    public List<UserPermission> getUsersWithChangeStatusPermission() {
        return repository.findUsersWithChangeStatusPermission();
    }

    // Obtener administradores
    public List<UserPermission> getAdministrators() {
        return repository.findByIsAdminTrue();
    }

    // Estadísticas
    public long getTotalUsers() {
        return repository.count();
    }

    public long getAdminCount() {
        return repository.countByIsAdminTrue();
    }

    public long getCreatePermissionCount() {
        return repository.countByCreatePermission();
    }

    public long getEditPermissionCount() {
        return repository.countByEditPermission();
    }

    public long getDeletePermissionCount() {
        return repository.countByDeletePermission();
    }

    public long getChangeStatusPermissionCount() {
        return repository.countByChangeStatusPermission();
    }

    // Buscar usuarios
    public List<UserPermission> searchUsers(String username) {
        return repository.findByUsernameContaining(username);
    }

    // Inicializar permisos para usuarios del AD si no existen
    public void initializeUserPermissions(String username, String createdBy) {
        if (!repository.existsByUsername(username)) {
            UserPermission userPermission = new UserPermission(username, createdBy);
            repository.save(userPermission);
        }
    }

    // Dar todos los permisos (hacer administrador)
    public void makeAdmin(String username, String createdBy) {
        UserPermission userPermission = createOrUpdatePermissions(username, createdBy);
        userPermission.setIsAdmin(true);
        userPermission.setCanCreateTasks(true);
        userPermission.setCanEditTasks(true);
        userPermission.setCanDeleteTasks(true);
        userPermission.setCanChangeStatus(true);
        repository.save(userPermission);
    }

    // Quitar todos los permisos
    public void removeAllPermissions(String username) {
        Optional<UserPermission> userPermission = repository.findByUsername(username);
        if (userPermission.isPresent()) {
            UserPermission permission = userPermission.get();
            permission.setIsAdmin(false);
            permission.setCanCreateTasks(false);
            permission.setCanEditTasks(false);
            permission.setCanDeleteTasks(false);
            permission.setCanChangeStatus(false);
            repository.save(permission);
        }
    }
}
