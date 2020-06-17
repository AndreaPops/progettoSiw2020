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
import it.uniroma3.siw.taskmanager.service.CredentialsService;
import it.uniroma3.siw.taskmanager.service.ProjectService;
import it.uniroma3.siw.taskmanager.service.TaskService;
import it.uniroma3.siw.taskmanager.service.UserService;

@Controller
public class TaskController {

	@Autowired
	CredentialsService credentialsService;

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

	@RequestMapping(value= {"/task/{idTask}"},method=RequestMethod.GET)
	public String task(@PathVariable("idTask") Long idTask,Model model) {
		Task task=this.taskService.getTask(idTask);
		model.addAttribute("task", task);
		return "task";
	}

	@RequestMapping(value = {"/projects/{projectId}/deleteTask/{taskId}"}, method = RequestMethod.POST)
	public String deleteTask(Model model, @PathVariable("projectId") Long projectId, @PathVariable("taskId") Long taskId) {
		User loggedUser = sessionData.getLoggedUser();
		Project project = projectService.getProject(projectId);
		User owner = project.getOwner();
		if (loggedUser.equals(owner)) {
			project.removeTaskWithId(taskId);
			projectService.saveProject(project);
			taskService.deleteTask(this.taskService.getTask(taskId));
			return "redirect:/projects/{projectId}";
		}
		else
			return "redirect:/projects/{projectId}";
	}

	@RequestMapping(value= {"/updateTask/{taskId}"}, method= RequestMethod.GET)
	public String updateTask(@PathVariable("taskId") Long taskId,Model model) {
		Task taskForm=	this.taskService.getTask(taskId);
		model.addAttribute("taskForm", taskForm);
		return "updateTask";
	}


	@RequestMapping(value= {"/updateTask/{taskId}"}, method= RequestMethod.POST)
	public String updateTask(@PathVariable("taskId") Long taskId, Model model,
			@Valid @ModelAttribute("taskForm") Task newTask,
			BindingResult taskBindingResult, @PathVariable("projectId") Long idProject) {
		Task task=this.taskService.getTask(taskId);
		this.taskValidator.validate(newTask, taskBindingResult);
		if(!taskBindingResult.hasErrors()){
			
			task.setName(newTask.getName());
			task.setDescription(newTask.getDescription());
			this.taskService.saveTask(task);
			model.addAttribute("task", task);
			return "task";
		}
		else
		{
			model.addAttribute("task", task);
			return "updateTask/{taskId}";
		}
	}



	@RequestMapping(value= {"/projects/task/{idTask}/{idProject}"},method=RequestMethod.GET)
	public String shareTask(@PathVariable("idTask") Long idTask,@PathVariable("idProject") Long idProject,Model model) {
		User loggedUser = sessionData.getLoggedUser();
		Project project=this.projectService.getProject(idProject);
		Task task= this.taskService.getTask(idTask);
		String userName= new String();
		model.addAttribute("project",project);
		model.addAttribute("task",task);
		model.addAttribute("userName", userName);
		return "shareTaskWith";

	}

	@RequestMapping(value={"/task/{idTask}/{idProject}"},method=RequestMethod.POST)
	public String shareTask(@PathVariable("idProject") Long idProject,@PathVariable("idTask") Long idTask,
			Model model,@ModelAttribute("userName") String userName) {
		Task task=this.taskService.getTask(idTask);
		Project project=this.projectService.getProject(idProject);
		Credentials credentials=this.credentialsService.getCredentialsVisibleOfProject(userName, project);
		if(credentials==null) {
			return "redirect:/projects/task/{idTask}/{idProject}";
		}else {
			this.taskService.shareTaskWithUser(task, credentials.getUser());
			model.addAttribute("task", task);
			return "task";
		}
	}



}
