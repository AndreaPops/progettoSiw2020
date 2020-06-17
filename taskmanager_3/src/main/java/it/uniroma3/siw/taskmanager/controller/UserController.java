package it.uniroma3.siw.taskmanager.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import it.uniroma3.siw.taskmanager.controller.session.SessionData;
import it.uniroma3.siw.taskmanager.controller.validation.CredentialsValidator;
import it.uniroma3.siw.taskmanager.controller.validation.UserValidator;
import it.uniroma3.siw.taskmanager.model.Credentials;
import it.uniroma3.siw.taskmanager.model.User;
import it.uniroma3.siw.taskmanager.repository.UserRepository;
import it.uniroma3.siw.taskmanager.service.CredentialsService;

/**
 * The UserController handles all interactions involving User data.
 */
@Controller
public class UserController {

	@Autowired
    CredentialsValidator credentialsValidator;
	
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserValidator userValidator;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    SessionData sessionData;
    
    @Autowired
    CredentialsService credentialsService;

    /**
     * This method is called when a GET request is sent by the user to URL "/users/user_id".
     * This method prepares and dispatches the User registration view.
     *
     * @param model the Request model
     * @return the name of the target view, that in this case is "register"
     */
    @RequestMapping(value = { "/home" }, method = RequestMethod.GET)
    public String home(Model model) {
        User loggedUser = sessionData.getLoggedUser();
        model.addAttribute("user", loggedUser);
        return "home";
    }

    /**
     * This method is called when a GET request is sent by the user to URL "/users/user_id".
     * This method prepares and dispatches the User registration view.
     *
     * @param model the Request model
     * @return the name of the target view, that in this case is "register"
     */
    @RequestMapping(value = { "/users/me" }, method = RequestMethod.GET)
    public String me(Model model) {
        User loggedUser = sessionData.getLoggedUser();
        Credentials credentials = sessionData.getLoggedCredentials();
        System.out.println(credentials.getPassword());
        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("credentials", credentials);

        return "userProfile";
    }

    /**
     * This method is called when a GET request is sent by the user to URL "/users/user_id".
     * This method prepares and dispatches the User registration view.
     *
     * @param model the Request model
     * @return the name of the target view, that in this case is "register"
     */
    @RequestMapping(value = { "/admin" }, method = RequestMethod.GET)
    public String admin(Model model) {
        User loggedUser = sessionData.getLoggedUser();
        model.addAttribute("user", loggedUser);
        return "admin";
    }
    
    @RequestMapping(value = { "/admin/users" }, method = RequestMethod.GET)
    public String usersList(Model model) {
        User loggedUser = sessionData.getLoggedUser();
        List<Credentials> allCredentials = this.credentialsService.getAllCredentials();
        
        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("allCredentials", allCredentials);
        
        return "allUsers";
    }
    
    @RequestMapping(value = { "/admin/users/{username}/delete" }, method = RequestMethod.POST)
    public String removeUser(Model model, @PathVariable String username) {
        this.credentialsService.deleteCredentials(username);
        return "redirect:/admin/users";
    }

    @RequestMapping(value= {"/update"},method=RequestMethod.GET)
    public String updateYourProfile(Model model) {
    	User loggedUser=this.sessionData.getLoggedUser();
    	Credentials credentials=this.sessionData.getLoggedCredentials();
    	model.addAttribute("userForm",loggedUser);
    	model.addAttribute("credentialsForm",credentials);
    	return "updateprofile";
    }
    
    @RequestMapping(value= {"/update/{credentialsId}"},method=RequestMethod.POST)
    public String updateProfile(@PathVariable("credentialsId") Long id,Model model,
    							@Valid @ModelAttribute("userForm") User newUser,BindingResult userBindingResult,
    							@Valid @ModelAttribute("credentialsForm") Credentials newCredentials,BindingResult credentialsBindingResult) {
    	this.userValidator.validate(newUser, userBindingResult);
    	this.credentialsValidator.validate(newCredentials, credentialsBindingResult);
    	if(!userBindingResult.hasErrors() && !credentialsBindingResult.hasErrors()) {
    	Credentials credentials=this.credentialsService.getCredentials(id);
    	User user=credentials.getUser();
    	user.setFirstName(newUser.getFirstName());
    	user.setLastName(newUser.getLastName());
    	credentials.setUserName(newCredentials.getUserName());
    	credentials.setPassword(newCredentials.getPassword());
    	this.credentialsService.saveCredentials(credentials);
    	return "redirect:/user/me";
    	}
    	return "updateprofile";
    }
}
