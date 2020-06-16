package it.uniroma3.siw.taskmanager.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import it.uniroma3.siw.taskmanager.controller.session.SessionData;
import it.uniroma3.siw.taskmanager.controller.validation.TaskValidator;
import it.uniroma3.siw.taskmanager.model.Credentials;
import it.uniroma3.siw.taskmanager.model.Project;
import it.uniroma3.siw.taskmanager.model.Task;
import it.uniroma3.siw.taskmanager.model.User;
import it.uniroma3.siw.taskmanager.service.ProjectService;
import it.uniroma3.siw.taskmanager.service.TaskService;

@Controller
public class TaskController {

	@Autowired
	TaskValidator taskValidator;
	
	@Autowired
	ProjectService projectService;
	
	@Autowired
	TaskService taskService;
	
	@Autowired
	SessionData sessionData;
	
	@RequestMapping(value= {"/task/add/{idProject}"},method=RequestMethod.GET)
	public String addTask(Model model,@PathVariable("idProject") Long idProject) {
		User loggedUser= this.sessionData.getLoggedUser();
		Credentials credentials=this.sessionData.getLoggedCredentials();
		Project project=this.projectService.getProject(idProject);
		model.addAttribute("loggedUser",loggedUser);
		model.addAttribute("credentials",credentials);
		model.addAttribute("project",project);
		model.addAttribute("task", new Task());
		return "addTask";
	}
	
	@RequestMapping(value= {"/task/add/{idProject}"},method=RequestMethod.POST)
	public String addTask(@PathVariable("idProject") Long idProject,Model model,@Valid@ModelAttribute("task") Task task,BindingResult taskBindingResult) {
		
		this.taskValidator.validate(task, taskBindingResult);
		if(!taskBindingResult.hasErrors()) {
			Project project=this.projectService.getProject(idProject);
			this.projectService.addTaskProject(project,this.taskService.saveTask(task));
			model.addAttribute("task",task);
			return "task";
		}
		return "redirect:/task/add/{idProject}";
	}
}
