package com.parcial.tareas_ad.controller;

import com.parcial.tareas_ad.model.task;
import com.parcial.tareas_ad.service.TaskService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }


    @GetMapping
    public String list(Model model) {
        model.addAttribute("tasks", service.listAll());
        return "tasks";
    }


    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("task", new task());
        return "task-form";
    }


    @PostMapping
    public String save(@ModelAttribute task task) {
        service.save(task);
        return "redirect:/tasks";
    }


    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("task", service.findById(id));
        return "task-form";
    }
}