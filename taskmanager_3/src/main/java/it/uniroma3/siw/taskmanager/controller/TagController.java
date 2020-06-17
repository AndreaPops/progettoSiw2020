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
import it.uniroma3.siw.taskmanager.service.ProjectService;
import it.uniroma3.siw.taskmanager.service.TagService;

@Controller
public class TagController {
	@Autowired
	TagService tagService;
	@Autowired
	TagValidator tagValidator;
	@Autowired
	ProjectService projectService;

	@RequestMapping(value= {"/project/addTag/{idProject}"}, method=RequestMethod.GET)
	public String addTag(Model model, @PathVariable("idProject") Long idProject) {
		Project project=this.projectService.getProject(idProject);
		model.addAttribute("project", project);
		model.addAttribute("tag", new Tag());
		return "addTag";
	}

	@RequestMapping(value= {"/project/addTag/{idProject}"}, method=RequestMethod.POST)
	public String addTag(Model model, @PathVariable("idProject") Long idProject, 
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
}
