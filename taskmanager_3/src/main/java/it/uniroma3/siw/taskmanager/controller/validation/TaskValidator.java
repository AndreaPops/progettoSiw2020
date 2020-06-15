package it.uniroma3.siw.taskmanager.controller.validation;

import org.springframework.validation.Validator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.uniroma3.siw.taskmanager.model.Task;

@Component
public class TaskValidator implements Validator{
	final Integer MAX_NAME_LENGTH = 100;
	final Integer MIN_NAME_LENGTH = 2;
	final Integer MAX_DESCRIPTION_LENGTH = 1000;


	@Override
	public void validate(Object o, Errors errors) {
		Task task = (Task) o;
		String name = task.getName().trim();
		String description = task.getDescription().trim();

		if (name.isBlank())
			errors.rejectValue("name", "required");
		else if (name.length() < MIN_NAME_LENGTH || name.length() > MAX_NAME_LENGTH)
			errors.rejectValue("name", "size");


		if (description.length() > MAX_DESCRIPTION_LENGTH)
			errors.rejectValue("description", "size");

	}

	@Override
	public boolean supports(Class<?> clazz) {
		return Task.class.equals(clazz);
	}
}
