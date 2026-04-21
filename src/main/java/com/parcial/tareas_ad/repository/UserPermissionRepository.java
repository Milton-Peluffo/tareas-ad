package com.parcial.tareas_ad.repository;

import com.parcial.tareas_ad.model.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPermissionRepository extends JpaRepository<UserPermission, Long> {
    
    // Encontrar permisos por nombre de usuario
    Optional<UserPermission> findByUsername(String username);
    
    // Verificar si existe un usuario con permisos
    boolean existsByUsername(String username);
    
    // Encontrar todos los usuarios que son administradores
    List<UserPermission> findByIsAdminTrue();
    
    // Encontrar todos los usuarios con un permiso específico
    @Query("SELECT up FROM UserPermission up WHERE up.canCreateTasks = true")
    List<UserPermission> findUsersWithCreatePermission();
    
    @Query("SELECT up FROM UserPermission up WHERE up.canEditTasks = true")
    List<UserPermission> findUsersWithEditPermission();
    
    @Query("SELECT up FROM UserPermission up WHERE up.canDeleteTasks = true")
    List<UserPermission> findUsersWithDeletePermission();
    
    @Query("SELECT up FROM UserPermission up WHERE up.canChangeStatus = true")
    List<UserPermission> findUsersWithChangeStatusPermission();
    
    // Encontrar usuarios con cualquier permiso (excepto admin)
    @Query("SELECT up FROM UserPermission up WHERE " +
           "(up.canCreateTasks = true OR up.canEditTasks = true OR " +
           "up.canDeleteTasks = true OR up.canChangeStatus = true) AND up.isAdmin = false")
    List<UserPermission> findUsersWithAnyPermission();
    
    // Contar usuarios por tipo de permiso
    long countByIsAdminTrue();
    
    @Query("SELECT COUNT(up) FROM UserPermission up WHERE up.canCreateTasks = true")
    long countByCreatePermission();
    
    @Query("SELECT COUNT(up) FROM UserPermission up WHERE up.canEditTasks = true")
    long countByEditPermission();
    
    @Query("SELECT COUNT(up) FROM UserPermission up WHERE up.canDeleteTasks = true")
    long countByDeletePermission();
    
    @Query("SELECT COUNT(up) FROM UserPermission up WHERE up.canChangeStatus = true")
    long countByChangeStatusPermission();
    
    // Buscar usuarios por nombre (contiene)
    @Query("SELECT up FROM UserPermission up WHERE up.username LIKE %:username%")
    List<UserPermission> findByUsernameContaining(@Param("username") String username);
    
    // Encontrar todos los usuarios ordenados por nombre
    @Query("SELECT up FROM UserPermission up ORDER BY up.username")
    List<UserPermission> findAllOrderedByUsername();
}
