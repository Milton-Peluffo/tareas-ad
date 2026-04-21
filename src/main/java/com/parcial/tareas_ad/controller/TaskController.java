package com.parcial.tareas_ad.controller;

import com.parcial.tareas_ad.model.Task;
import com.parcial.tareas_ad.service.TaskService;
import com.parcial.tareas_ad.service.UserPermissionService;
import com.parcial.tareas_ad.util.SecurityUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService service;
    private final UserPermissionService userPermissionService;

    public TaskController(TaskService service, UserPermissionService userPermissionService) {
        this.service = service;
        this.userPermissionService = userPermissionService;
    }

    // Listar todas las tareas
    @GetMapping
    public String listTasks(Model model, RedirectAttributes redirectAttributes) {
        String currentUser = SecurityUtil.getCurrentUsername();
        
        // Los usuarios pueden ver la lista de tareas (permiso básico)
        model.addAttribute("tasks", service.findAll());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("pendingCount", service.countByEstado("PENDIENTE"));
        model.addAttribute("completedCount", service.countByEstado("FINALIZADO"));
        
        // Agregar información de permisos para mostrar/ocultar botones
        model.addAttribute("canCreateTasks", userPermissionService.hasPermission(currentUser, "CREATE_TASKS"));
        model.addAttribute("canEditTasks", userPermissionService.hasPermission(currentUser, "EDIT_TASKS"));
        model.addAttribute("canDeleteTasks", userPermissionService.hasPermission(currentUser, "DELETE_TASKS"));
        model.addAttribute("canChangeStatus", userPermissionService.hasPermission(currentUser, "CHANGE_STATUS"));
        model.addAttribute("isAdmin", userPermissionService.isCurrentUserAdmin());
        
        return "tareas";
    }

    // Formulario para crear nueva tarea
    @GetMapping("/new")
    public String createTaskForm(Model model, RedirectAttributes redirectAttributes) {
        String currentUser = SecurityUtil.getCurrentUsername();
        
        // Verificar permiso para crear tareas
        if (!userPermissionService.hasPermission(currentUser, "CREATE_TASKS")) {
            redirectAttributes.addFlashAttribute("error", "No tienes permisos para crear tareas");
            return "redirect:/tasks";
        }
        
        model.addAttribute("task", new Task());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("isEdit", false);
        return "task-form";
    }

    // Guardar nueva tarea
    @PostMapping("/save")
    public String saveTask(@ModelAttribute Task task, RedirectAttributes redirectAttributes) {
        String currentUser = SecurityUtil.getCurrentUsername();
        
        // Verificar permiso para crear tareas
        if (!userPermissionService.hasPermission(currentUser, "CREATE_TASKS")) {
            redirectAttributes.addFlashAttribute("error", "No tienes permisos para crear tareas");
            return "redirect:/tasks";
        }
        
        try {
            task.setCreadoPor(currentUser);
            service.save(task);
            redirectAttributes.addFlashAttribute("success", "Tarea creada exitosamente");
            return "redirect:/tasks";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear la tarea: " + e.getMessage());
            return "redirect:/tasks/new";
        }
    }

    // Formulario para editar tarea
    @GetMapping("/edit/{id}")
    public String editTaskForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        String currentUser = SecurityUtil.getCurrentUsername();
        
        // Verificar permiso para editar tareas
        if (!userPermissionService.hasPermission(currentUser, "EDIT_TASKS")) {
            redirectAttributes.addFlashAttribute("error", "No tienes permisos para editar tareas");
            return "redirect:/tasks";
        }
        
        try {
            Task task = service.findById(id);
            model.addAttribute("task", task);
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("isEdit", true);
            return "task-form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Tarea no encontrada");
            return "redirect:/tasks";
        }
    }

    // Actualizar tarea
    @PostMapping("/update/{id}")
    public String updateTask(@PathVariable Long id, @ModelAttribute Task task, RedirectAttributes redirectAttributes) {
        String currentUser = SecurityUtil.getCurrentUsername();
        
        // Verificar permiso para editar tareas
        if (!userPermissionService.hasPermission(currentUser, "EDIT_TASKS")) {
            redirectAttributes.addFlashAttribute("error", "No tienes permisos para editar tareas");
            return "redirect:/tasks";
        }
        
        try {
            Task existingTask = service.findById(id);
            existingTask.setNombre(task.getNombre());
            existingTask.setDescripcion(task.getDescripcion());
            existingTask.setEstado(task.getEstado());
            service.save(existingTask);
            redirectAttributes.addFlashAttribute("success", "Tarea actualizada exitosamente");
            return "redirect:/tasks";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la tarea: " + e.getMessage());
            return "redirect:/tasks/edit/" + id;
        }
    }

    // Eliminar tarea
    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        String currentUser = SecurityUtil.getCurrentUsername();
        
        // Verificar permiso para eliminar tareas
        if (!userPermissionService.hasPermission(currentUser, "DELETE_TASKS")) {
            redirectAttributes.addFlashAttribute("error", "No tienes permisos para eliminar tareas");
            return "redirect:/tasks";
        }
        
        try {
            service.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Tarea eliminada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la tarea: " + e.getMessage());
        }
        return "redirect:/tasks";
    }

    // Cambiar estado de tarea (PENDIENTE/FINALIZADO)
    @GetMapping("/toggle-status/{id}")
    public String toggleTaskStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        String currentUser = SecurityUtil.getCurrentUsername();
        
        // Verificar permiso para cambiar estado
        if (!userPermissionService.hasPermission(currentUser, "CHANGE_STATUS")) {
            redirectAttributes.addFlashAttribute("error", "No tienes permisos para cambiar el estado de las tareas");
            return "redirect:/tasks";
        }
        
        try {
            Task task = service.findById(id);
            if ("PENDIENTE".equals(task.getEstado())) {
                task.setEstado("FINALIZADO");
            } else {
                task.setEstado("PENDIENTE");
            }
            service.save(task);
            redirectAttributes.addFlashAttribute("success", "Estado de la tarea actualizado");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cambiar el estado: " + e.getMessage());
        }
        return "redirect:/tasks";
    }

    // Ver detalles de una tarea
    @GetMapping("/view/{id}")
    public String viewTask(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        String currentUser = SecurityUtil.getCurrentUsername();
        
        try {
            Task task = service.findById(id);
            model.addAttribute("task", task);
            model.addAttribute("currentUser", currentUser);
            
            // Agregar información de permisos para mostrar/ocultar botones
            model.addAttribute("canEditTasks", userPermissionService.hasPermission(currentUser, "EDIT_TASKS"));
            model.addAttribute("canDeleteTasks", userPermissionService.hasPermission(currentUser, "DELETE_TASKS"));
            model.addAttribute("canChangeStatus", userPermissionService.hasPermission(currentUser, "CHANGE_STATUS"));
            model.addAttribute("isAdmin", userPermissionService.isCurrentUserAdmin());
            
            return "task-view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Tarea no encontrada");
            return "redirect:/tasks";
        }
    }
}