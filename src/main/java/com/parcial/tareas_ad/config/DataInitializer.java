package com.parcial.tareas_ad.config;

import com.parcial.tareas_ad.service.UserPermissionService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserPermissionService userPermissionService;

    public DataInitializer(UserPermissionService userPermissionService) {
        this.userPermissionService = userPermissionService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Crear usuario administrador inicial si no existe
        initializeAdminUser();
        
        // Crear usuarios de ejemplo con permisos específicos
        initializeExampleUsers();
    }

    private void initializeAdminUser() {
        // Crear usuario "admin" como administrador
        if (!userPermissionService.findByUsername("admin").isPresent()) {
            userPermissionService.makeAdmin("admin", "system");
            System.out.println("Usuario 'admin' creado como administrador");
        }
    }

    private void initializeExampleUsers() {
        // Crear User01 con permisos para crear y eliminar tareas
        if (!userPermissionService.findByUsername("User01").isPresent()) {
            userPermissionService.createOrUpdatePermissions("User01", "system");
            var user01 = userPermissionService.findByUsername("User01").get();
            user01.setCanCreateTasks(true);
            user01.setCanDeleteTasks(true);
            user01.setCanEditTasks(false);
            user01.setCanChangeStatus(false);
            userPermissionService.save(user01);
            System.out.println("Usuario 'User01' creado con permisos para crear y eliminar tareas");
        }

        // Crear User02 con permisos para editar y cambiar estado
        if (!userPermissionService.findByUsername("User02").isPresent()) {
            userPermissionService.createOrUpdatePermissions("User02", "system");
            var user02 = userPermissionService.findByUsername("User02").get();
            user02.setCanCreateTasks(false);
            user02.setCanDeleteTasks(false);
            user02.setCanEditTasks(true);
            user02.setCanChangeStatus(true);
            userPermissionService.save(user02);
            System.out.println("Usuario 'User02' creado con permisos para editar y cambiar estado");
        }

        // Crear User03 con permisos limitados (solo ver)
        if (!userPermissionService.findByUsername("User03").isPresent()) {
            userPermissionService.createOrUpdatePermissions("User03", "system");
            var user03 = userPermissionService.findByUsername("User03").get();
            user03.setCanCreateTasks(false);
            user03.setCanDeleteTasks(false);
            user03.setCanEditTasks(false);
            user03.setCanChangeStatus(false);
            userPermissionService.save(user03);
            System.out.println("Usuario 'User03' creado con permisos de solo lectura");
        }
    }
}
