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
import it.uniroma3.siw.taskmanager.model.Commento;
import it.uniroma3.siw.taskmanager.model.Credentials;
import it.uniroma3.siw.taskmanager.model.Project;
import it.uniroma3.siw.taskmanager.model.Task;
import it.uniroma3.siw.taskmanager.model.User;
import it.uniroma3.siw.taskmanager.service.CommentoService;
import it.uniroma3.siw.taskmanager.service.CredentialsService;
import it.uniroma3.siw.taskmanager.service.ProjectService;
import it.uniroma3.siw.taskmanager.service.TaskService;

@Controller
public class TaskController {

	@Autowired
	CommentoService	commentoService;
	
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
		User loggedUser=sessionData.getLoggedUser();
		model.addAttribute("user", loggedUser);
		Project project=this.projectService.getProject(idProject);
		if(!taskBindingResult.hasErrors()) {
			this.projectService.addTaskProject(project,this.taskService.saveTask(task));
			model.addAttribute("project", project);
			model.addAttribute("task",task);
			return "task";
		}
		model.addAttribute("task", task);
		model.addAttribute("project",project);
		return "addTask";
	}

	@RequestMapping(value= {"/task/{idTask}/project/{projectId}"},method=RequestMethod.GET)
	public String task(@PathVariable("idTask") Long idTask,Model model, @PathVariable("projectId") Long projectId) {
		Task task=this.taskService.getTask(idTask);
		Project project= this.projectService.getProject(projectId);
		model.addAttribute("project", project);
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

	@RequestMapping(value= {"/updateTask/{taskId}/project/{projectId}"}, method= RequestMethod.GET)
	public String updateTask(@PathVariable("taskId") Long taskId,Model model, @PathVariable("projectId") Long projectId) {
		Task task=	this.taskService.getTask(taskId);
		Project project=this.projectService.getProject(projectId);
		model.addAttribute("project", project);
		model.addAttribute("task", task);
		return "updateTask";
	}


	@RequestMapping(value= {"/updateTask/task/{taskId}/project/{projectId}"}, method= RequestMethod.POST)
	public String updateTask(@PathVariable("taskId") Long taskId, Model model,
			@PathVariable("projectId") Long projectId,
			@Valid @ModelAttribute("task") Task newTask,
			BindingResult taskBindingResult) {
		User loggedUser=sessionData.getLoggedUser();
		Project project=this.projectService.getProject(projectId);
		model.addAttribute("user", loggedUser);
		model.addAttribute("project", project);
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
			newTask.setId(task.getId());
			model.addAttribute("task", newTask);
			return "updateTask";
		}
	}



	@RequestMapping(value= {"/projects/task/{idTask}/{idProject}"},method=RequestMethod.GET)
	public String shareTask(@PathVariable("idTask") Long idTask,@PathVariable("idProject") Long idProject,Model model) {
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
		User loggedUser=sessionData.getLoggedUser();
		model.addAttribute("user", loggedUser);
		model.addAttribute("project", project);
		if(credentials==null) {
			return "redirect:/projects/task/{idTask}/{idProject}";
		}else {
			this.taskService.shareTaskWithUser(task, credentials.getUser());
			model.addAttribute("task", task);
			return "task";
		}
	}

	@RequestMapping( value = {"/projects/{idProject}/task/{idTask}/addComment"}, method = RequestMethod.GET)
	public String addComment(Model model, @PathVariable("idProject") Long idProject,
			@PathVariable("idTask") Long idTask) {
		Project project = projectService.getProject(idProject);
		User loggedUser = sessionData.getLoggedUser();
		if((project.getMembers().contains(loggedUser))||(project.getOwner().equals(loggedUser))) {
			Task task = taskService.getTask(idTask);
			String commento = new String();
			model.addAttribute("project", project);
			model.addAttribute("task", task);
			model.addAttribute("commento", commento);
			return "addCommento";
		}
		return "redirect:/task/{idTask}/project/{idProject}";
	}



	@RequestMapping( value = {"/projects/{idProject}/task/{idTask}/addComment"}, method = RequestMethod.POST)
	public String addComment(@ModelAttribute("commento") String commento, Model model, @PathVariable("idProject") Long idProject,
			@PathVariable("idTask") Long idTask) {
		Task task = taskService.getTask(idTask);
		Commento comment = new Commento();
		comment.setCommento(commento);
		comment.setUser(sessionData.getLoggedUser());
		task.addCommento(comment);
		this.commentoService.saveCommento(comment);
		taskService.saveTask(task);
		model.addAttribute("project", this.projectService.getProject(idProject));
		return "redirect:/task/{idTask}/project/{idProject}";

	}

}


