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
import it.uniroma3.siw.taskmanager.controller.validation.TagValidator;
import it.uniroma3.siw.taskmanager.model.Project;
import it.uniroma3.siw.taskmanager.model.Tag;
import it.uniroma3.siw.taskmanager.model.Task;
import it.uniroma3.siw.taskmanager.model.User;
import it.uniroma3.siw.taskmanager.service.ProjectService;
import it.uniroma3.siw.taskmanager.service.TagService;
import it.uniroma3.siw.taskmanager.service.TaskService;

@Controller
public class TagController {
	@Autowired
	SessionData sessionData;

	@Autowired
	TaskService taskService;

	@Autowired
	TagService tagService;

	@Autowired
	TagValidator tagValidator;

	@Autowired
	ProjectService projectService;

	@RequestMapping(value= {"/project/addTag/{idProject}"}, method=RequestMethod.GET)
	public String addTagProject(Model model, @PathVariable("idProject") Long idProject) {
		Project project=this.projectService.getProject(idProject);
		User loggedUser=this.sessionData.getLoggedUser();
		if(loggedUser.equals(project.getOwner())) {
			model.addAttribute("project", project);
			model.addAttribute("tag", new Tag());
			return "addTag";
		}
		return"redirect:/projects/{idProject}";
	}

	@RequestMapping(value= {"/project/addTag/{idProject}"}, method=RequestMethod.POST)
	public String addTagProject(Model model, @PathVariable("idProject") Long idProject, 
			@Valid @ModelAttribute("tag") Tag tag, BindingResult tagBindingResult) {
		Project project=this.projectService.getProject(idProject);
		this.tagValidator.validate(tag, tagBindingResult);
		if(!tagBindingResult.hasErrors()) {
			this.tagService.saveTag(tag);
			this.projectService.addTagProject(project, tag);
			model.addAttribute("project", project);
			model.addAttribute("members", project.getMembers());
			return "project";
		}
		else {
			model.addAttribute("project", project);
			model.addAttribute("tag", tag);
			return "addTag";
		}
	}

	@RequestMapping(value= {"/task/addTag/{idTask}/{idProject}"},method=RequestMethod.GET)
	public String addTagTask(Model model,@PathVariable("idTask") Long idTask,@PathVariable("idProject") Long idProject) {
		Task task=this.taskService.getTask(idTask);
		User loggedUser=this.sessionData.getLoggedUser();
		Project project=this.projectService.getProject(idProject);
		if(loggedUser.equals(project.getOwner())) {
			model.addAttribute("task",task );
			model.addAttribute("project", this.projectService.getProject(idProject));
			return "addTagTask";
		}
		return "redirect:/task/{idTask}/project/{idProject}";
	}

	@RequestMapping(value= {"/task/{idTask}/{idProject}/{idTag}"},method=RequestMethod.GET)
	public String addTagTask(@PathVariable("idTask") Long idTask,Model model,@PathVariable("idProject") Long idProject,
			@PathVariable("idTag") Long idTag) {
		Task task=this.taskService.getTask(idTask);
		Tag tag=this.tagService.getTag(idTag);
		Project project=this.projectService.getProject(idProject);
		model.addAttribute("project", project);
		this.tagService.addTaskToTag(tag, task);
		this.taskService.addTagtoTask(task, tag);
		model.addAttribute("task", task);
		return "task";
	}
}


