package com.parcial.tareas_ad.controller;

import com.parcial.tareas_ad.model.UserPermission;
import com.parcial.tareas_ad.service.UserPermissionService;
import com.parcial.tareas_ad.util.SecurityUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserPermissionService userPermissionService;

    public AdminController(UserPermissionService userPermissionService) {
        this.userPermissionService = userPermissionService;
    }

    // Página principal de administración
    @GetMapping
    public String adminDashboard(Model model, RedirectAttributes redirectAttributes) {
        // Verificar si el usuario actual es administrador
        if (!userPermissionService.isCurrentUserAdmin()) {
            redirectAttributes.addFlashAttribute("error", "No tienes permisos de administrador para acceder a esta página");
            return "redirect:/dashboard";
        }

        String currentUser = SecurityUtil.getCurrentUsername();
        
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("users", userPermissionService.findAll());
        
        // Estadísticas
        model.addAttribute("totalUsers", userPermissionService.getTotalUsers());
        model.addAttribute("adminCount", userPermissionService.getAdminCount());
        model.addAttribute("createCount", userPermissionService.getCreatePermissionCount());
        model.addAttribute("editCount", userPermissionService.getEditPermissionCount());
        model.addAttribute("deleteCount", userPermissionService.getDeletePermissionCount());
        model.addAttribute("changeStatusCount", userPermissionService.getChangeStatusPermissionCount());
        
        return "admin";
    }

    // Formulario para agregar nuevo usuario
    @GetMapping("/add-user")
    public String addUserForm(Model model, RedirectAttributes redirectAttributes) {
        if (!userPermissionService.isCurrentUserAdmin()) {
            redirectAttributes.addFlashAttribute("error", "No tienes permisos de administrador");
            return "redirect:/admin";
        }

        model.addAttribute("userPermission", new UserPermission());
        model.addAttribute("currentUser", SecurityUtil.getCurrentUsername());
        model.addAttribute("isEdit", false);
        return "user-permission-form";
    }

    // Guardar nuevo usuario con permisos
    @PostMapping("/save-user")
    public String saveUser(@ModelAttribute UserPermission userPermission, RedirectAttributes redirectAttributes) {
        if (!userPermissionService.isCurrentUserAdmin()) {
            redirectAttributes.addFlashAttribute("error", "No tienes permisos de administrador");
            return "redirect:/admin";
        }

        try {
            String currentUser = SecurityUtil.getCurrentUsername();
            userPermission.setCreadoPor(currentUser);
            
            // Si se marca como administrador, dar todos los permisos
            if (userPermission.getIsAdmin() != null && userPermission.getIsAdmin()) {
                userPermission.setCanCreateTasks(true);
                userPermission.setCanEditTasks(true);
                userPermission.setCanDeleteTasks(true);
                userPermission.setCanChangeStatus(true);
            }
            
            userPermissionService.save(userPermission);
            redirectAttributes.addFlashAttribute("success", "Usuario y permisos guardados exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar usuario: " + e.getMessage());
            return "redirect:/admin/add-user";
        }
        
        return "redirect:/admin";
    }

    // Formulario para editar usuario existente
    @GetMapping("/edit/{username}")
    public String editUserForm(@PathVariable String username, Model model, RedirectAttributes redirectAttributes) {
        if (!userPermissionService.isCurrentUserAdmin()) {
            redirectAttributes.addFlashAttribute("error", "No tienes permisos de administrador");
            return "redirect:/admin";
        }

        try {
            UserPermission userPermission = userPermissionService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            model.addAttribute("userPermission", userPermission);
            model.addAttribute("currentUser", SecurityUtil.getCurrentUsername());
            model.addAttribute("isEdit", true);
            return "user-permission-form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
            return "redirect:/admin";
        }
    }

    // Actualizar permisos de usuario
    @PostMapping("/update/{username}")
    public String updateUser(@PathVariable String username, @ModelAttribute UserPermission userPermission, 
                           RedirectAttributes redirectAttributes) {
        if (!userPermissionService.isCurrentUserAdmin()) {
            redirectAttributes.addFlashAttribute("error", "No tienes permisos de administrador");
            return "redirect:/admin";
        }

        try {
            UserPermission existing = userPermissionService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            // Actualizar permisos
            existing.setCanCreateTasks(userPermission.getCanCreateTasks());
            existing.setCanEditTasks(userPermission.getCanEditTasks());
            existing.setCanDeleteTasks(userPermission.getCanDeleteTasks());
            existing.setCanChangeStatus(userPermission.getCanChangeStatus());
            
            // Si se marca como administrador, dar todos los permisos
            if (userPermission.getIsAdmin() != null && userPermission.getIsAdmin()) {
                existing.setIsAdmin(true);
                existing.setCanCreateTasks(true);
                existing.setCanEditTasks(true);
                existing.setCanDeleteTasks(true);
                existing.setCanChangeStatus(true);
            } else {
                existing.setIsAdmin(false);
            }
            
            userPermissionService.save(existing);
            redirectAttributes.addFlashAttribute("success", "Permisos actualizados exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar permisos: " + e.getMessage());
            return "redirect:/admin/edit/" + username;
        }
        
        return "redirect:/admin";
    }

    // Eliminar usuario
    @GetMapping("/delete/{username}")
    public String deleteUser(@PathVariable String username, RedirectAttributes redirectAttributes) {
        if (!userPermissionService.isCurrentUserAdmin()) {
            redirectAttributes.addFlashAttribute("error", "No tienes permisos de administrador");
            return "redirect:/admin";
        }

        try {
            // No permitir eliminar al propio usuario
            String currentUser = SecurityUtil.getCurrentUsername();
            if (currentUser.equals(username)) {
                redirectAttributes.addFlashAttribute("error", "No puedes eliminar tu propio usuario");
                return "redirect:/admin";
            }
            
            userPermissionService.deleteByUsername(username);
            redirectAttributes.addFlashAttribute("success", "Usuario eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar usuario: " + e.getMessage());
        }
        
        return "redirect:/admin";
    }

    // Hacer administrador
    @GetMapping("/make-admin/{username}")
    public String makeAdmin(@PathVariable String username, RedirectAttributes redirectAttributes) {
        if (!userPermissionService.isCurrentUserAdmin()) {
            redirectAttributes.addFlashAttribute("error", "No tienes permisos de administrador");
            return "redirect:/admin";
        }

        try {
            String currentUser = SecurityUtil.getCurrentUsername();
            userPermissionService.makeAdmin(username, currentUser);
            redirectAttributes.addFlashAttribute("success", "Usuario promovido a administrador");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al promover usuario: " + e.getMessage());
        }
        
        return "redirect:/admin";
    }

    // Quitar todos los permisos
    @GetMapping("/remove-permissions/{username}")
    public String removeAllPermissions(@PathVariable String username, RedirectAttributes redirectAttributes) {
        if (!userPermissionService.isCurrentUserAdmin()) {
            redirectAttributes.addFlashAttribute("error", "No tienes permisos de administrador");
            return "redirect:/admin";
        }

        try {
            // No permitir quitar permisos al propio usuario si es admin
            String currentUser = SecurityUtil.getCurrentUsername();
            if (currentUser.equals(username) && userPermissionService.isCurrentUserAdmin()) {
                redirectAttributes.addFlashAttribute("error", "No puedes quitar tus propios permisos de administrador");
                return "redirect:/admin";
            }
            
            userPermissionService.removeAllPermissions(username);
            redirectAttributes.addFlashAttribute("success", "Todos los permisos removidos del usuario");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al remover permisos: " + e.getMessage());
        }
        
        return "redirect:/admin";
    }

    // Buscar usuarios
    @GetMapping("/search")
    public String searchUsers(@RequestParam String username, Model model, RedirectAttributes redirectAttributes) {
        if (!userPermissionService.isCurrentUserAdmin()) {
            redirectAttributes.addFlashAttribute("error", "No tienes permisos de administrador");
            return "redirect:/admin";
        }

        model.addAttribute("currentUser", SecurityUtil.getCurrentUsername());
        model.addAttribute("users", userPermissionService.searchUsers(username));
        model.addAttribute("searchTerm", username);
        
        // Mantener estadísticas
        model.addAttribute("totalUsers", userPermissionService.getTotalUsers());
        model.addAttribute("adminCount", userPermissionService.getAdminCount());
        model.addAttribute("createCount", userPermissionService.getCreatePermissionCount());
        model.addAttribute("editCount", userPermissionService.getEditPermissionCount());
        model.addAttribute("deleteCount", userPermissionService.getDeletePermissionCount());
        model.addAttribute("changeStatusCount", userPermissionService.getChangeStatusPermissionCount());
        
        return "admin";
    }
}
