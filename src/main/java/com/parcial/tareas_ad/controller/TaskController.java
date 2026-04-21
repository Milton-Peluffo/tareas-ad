package com.parcial.tareas_ad.controller;

import com.parcial.tareas_ad.model.Task;
import com.parcial.tareas_ad.service.TaskService;
import com.parcial.tareas_ad.util.SecurityUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    // Listar todas las tareas
    @GetMapping
    public String listTasks(Model model) {
        String currentUser = SecurityUtil.getCurrentUsername();
        model.addAttribute("tasks", service.findAll());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("pendingCount", service.countByEstado("PENDIENTE"));
        model.addAttribute("completedCount", service.countByEstado("FINALIZADO"));
        return "tareas";
    }

    // Formulario para crear nueva tarea
    @GetMapping("/new")
    public String createTaskForm(Model model) {
        String currentUser = SecurityUtil.getCurrentUsername();
        model.addAttribute("task", new Task());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("isEdit", false);
        return "task-form";
    }

    // Guardar nueva tarea
    @PostMapping("/save")
    public String saveTask(@ModelAttribute Task task, RedirectAttributes redirectAttributes) {
        try {
            String currentUser = SecurityUtil.getCurrentUsername();
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
        try {
            Task task = service.findById(id);
            model.addAttribute("task", task);
            model.addAttribute("currentUser", SecurityUtil.getCurrentUsername());
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
        try {
            Task task = service.findById(id);
            model.addAttribute("task", task);
            model.addAttribute("currentUser", SecurityUtil.getCurrentUsername());
            return "task-view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Tarea no encontrada");
            return "redirect:/tasks";
        }
    }
}