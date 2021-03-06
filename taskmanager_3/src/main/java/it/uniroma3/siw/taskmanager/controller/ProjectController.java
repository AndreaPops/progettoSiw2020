package it.uniroma3.siw.taskmanager.controller;

import java.util.List;

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
import it.uniroma3.siw.taskmanager.controller.validation.ProjectValidator;
import it.uniroma3.siw.taskmanager.model.Credentials;
import it.uniroma3.siw.taskmanager.model.Project;
import it.uniroma3.siw.taskmanager.model.User;
import it.uniroma3.siw.taskmanager.service.CredentialsService;
import it.uniroma3.siw.taskmanager.service.ProjectService;
import it.uniroma3.siw.taskmanager.service.UserService;

@Controller
public class ProjectController {

	@Autowired
	CredentialsService credentialsService;

	@Autowired
	ProjectService projectService;

	@Autowired
	UserService userService;

	@Autowired
	ProjectValidator projectValidator;

	@Autowired
	SessionData sessionData;

	@RequestMapping(value= {"/projects"}, method = RequestMethod.GET)
	public String myOwnedProjects(Model model) {
		User loggedUser = sessionData.getLoggedUser();
		List<Project> projectsList = projectService.retrieveProjectsOwnedBy(loggedUser);
		model.addAttribute("loggedUser", loggedUser);
		model.addAttribute("projectsList", projectsList);
		return "myOwnedProjects";
	}
	@RequestMapping(value= {"/projects/{projectId}"}, method = RequestMethod.GET)
	public String project(Model model, @PathVariable Long projectId) {

		User loggedUser = sessionData.getLoggedUser();
		Project project = projectService.getProject(projectId);
		if(project == null)
			return "redirect:/projects";

		List<User> members = userService.getMembers(project);
		if(!project.getOwner().equals(loggedUser) && !members.contains(loggedUser))
			return "redirect:/projects";

		model.addAttribute("loggedUser", loggedUser);
		model.addAttribute("project", project);
		model.addAttribute("members", members);
		return "project";


	}

	@RequestMapping(value= { "/projects/add" }, method = RequestMethod.GET)
	public String createProjectForm(Model model) {
		User loggedUser = sessionData.getLoggedUser();
		model.addAttribute("loggedUser", loggedUser);
		model.addAttribute("projectForm", new Project());
		return "addProject";
	}

	@RequestMapping(value= { "/projects/add" }, method = RequestMethod.POST)
	public String createProject(@Valid @ModelAttribute("projectForm") Project project,
			BindingResult projectBindingResult, Model model) {
		User loggedUser = sessionData.getLoggedUser();
		projectValidator.validate(project, projectBindingResult);
		if(!projectBindingResult.hasErrors()) {
			project.setOwner(loggedUser);
			this.projectService.saveProject(project);
			return "redirect:/projects/" + project.getId();
		}
		model.addAttribute("loggedUser", loggedUser);
		return "addProject";
	}

	@RequestMapping(value= {"/delete/{id}"},method=RequestMethod.POST)
	public String deleteProject(@PathVariable("id")Long id,Model model) {
		User loggedUser=sessionData.getLoggedUser();
		model.addAttribute("user", loggedUser);
		this.projectService.deleteProject(id);
		return "redirect:/projects/";
	}

	@RequestMapping(value= {"/projects/share/{idProject}"},method=RequestMethod.GET)
	public String shareWith(@PathVariable("idProject") Long idProject,Model model) {
		Project project =projectService.getProject(idProject);
		User loggedUser=this.sessionData.getLoggedUser();
		if(loggedUser.equals(project.getOwner())) {
		model.addAttribute("project", project);
		model.addAttribute("userName",new String());
		return "shareWith";
		}
		return"redirect:/projects/{idProject}";
	}


	@RequestMapping(value= {"/projects/share/{idProject}"},method=RequestMethod.POST)
	public String shareWith(@ModelAttribute("userName") String userName,@PathVariable("idProject") Long idProject,Model model) {
		Project projectShare=projectService.getProject(idProject);
		Credentials credentials= this.credentialsService.getCredentials(userName);
		if(credentials==null) {
			return "redirect:/projects/share/{idProject}";
		}else {
			User newUser=credentials.getUser();
			projectService.shareProjectWithUser(projectShare, newUser);
			this.userService.UserWithshareProject(projectShare, newUser);
			User loggedUser= this.sessionData.getLoggedUser();
			model.addAttribute("user", loggedUser);
			return "home";
		}
	}


	@RequestMapping(value= {"/projects/shared/with/me"},method=RequestMethod.GET)
	public String shareProject(Model model) {
		User loggedUser = sessionData.getLoggedUser();
		List<Project> projectsList = this.projectService.retriveProjectsShared(loggedUser);
		model.addAttribute("loggedUser", loggedUser);
		model.addAttribute("projectsList", projectsList);
		return "mySharedProjects";
	}

	@RequestMapping(value= {"/updateProject/{idProject}"}, method=RequestMethod.GET)
	public String updateProject(@PathVariable("idProject") Long idProject, Model model) {
		Project project=projectService.getProject(idProject);
		model.addAttribute("projectForm", project);
		return "updateProject";
	}

	@RequestMapping(value= {"/updateProject/{idProject}"}, method=RequestMethod.POST)
	public String updateProject(@PathVariable("idProject") Long idProject, Model model,
			@Valid @ModelAttribute("projectForm") Project newProject,
			BindingResult projectBindingResult) {
		Project project=projectService.getProject(idProject);
		this.projectValidator.validate(newProject, projectBindingResult);
		if(!projectBindingResult.hasErrors()) {
			project.setName(newProject.getName());
			project.setDescription(newProject.getDescription());
			this.projectService.saveProject(project);
			model.addAttribute("projectForm", project);
			return "redirect:/projects";
		}
		else {
			newProject.setId(project.getId());
			model.addAttribute("projectForm", newProject);
			return "updateProject";
		}
	}
	

}
