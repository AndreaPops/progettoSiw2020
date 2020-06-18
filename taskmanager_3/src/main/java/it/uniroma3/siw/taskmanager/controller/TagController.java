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

import it.uniroma3.siw.taskmanager.controller.validation.TagValidator;
import it.uniroma3.siw.taskmanager.model.Project;
import it.uniroma3.siw.taskmanager.model.Tag;
import it.uniroma3.siw.taskmanager.model.Task;
import it.uniroma3.siw.taskmanager.service.ProjectService;
import it.uniroma3.siw.taskmanager.service.TagService;
import it.uniroma3.siw.taskmanager.service.TaskService;

@Controller
public class TagController {
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
		model.addAttribute("project", project);
		model.addAttribute("tag", new Tag());
		return "addTag";
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
		model.addAttribute("task",task );
		model.addAttribute("tag", new Tag());
		model.addAttribute("project", this.projectService.getProject(idProject));
		return "addTagTask";
	}

	@RequestMapping(value= {"/task/addTag/{idTask}/{idProject}"},method=RequestMethod.POST)
	public String addTagTask(@PathVariable("idTask") Long idTask,Model model,@PathVariable("idProject") Long idProject,
			@Valid@ModelAttribute("tag") Tag tag,BindingResult tagBindingResult) {
		Task task=this.taskService.getTask(idTask);
		this.tagValidator.validate(tag, tagBindingResult);
		System.out.println("1111111111111111111111"+tag.toString());
		model.addAttribute("project", this.projectService.getProject(idProject));
		if(!tagBindingResult.hasErrors()) {
			System.out.println("2222222222222222222222222"+tag.toString());
			this.tagService.saveTag(tag);
			System.out.println("ciaoooooooooooooooooooooooooooooo"+tag.toString());
			this.tagService.addTaskToTag(tag, task);
			System.out.println("33333333333333333333333333333"+tag.toString());
			this.taskService.addTagtoTask(task, tag);
			model.addAttribute("task", task);
			System.out.println("4444444444444444444444"+tag.toString());
			return"task";
		}else {
			model.addAttribute("task",task);
			model.addAttribute("tag", tag);
			System.out.println("555555555555555555555555555"+tag.toString());
			return"addTagTask";
		}
	}

}
