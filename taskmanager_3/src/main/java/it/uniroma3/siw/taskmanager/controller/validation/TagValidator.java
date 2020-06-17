package it.uniroma3.siw.taskmanager.controller.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.taskmanager.model.Tag;


@Component
public class TagValidator implements Validator{
	final Integer MAX_NAME_LENGTH = 100;
	final Integer MIN_NAME_LENGTH = 2;
	final Integer MAX_DESCRIPTION_LENGTH = 1000;
    
	@Override
	public void validate(Object o, Errors errors) {
		Tag tags = (Tag) o;
		String name = tags.getName().trim();
		String description = tags.getDescription().trim();
		String color= tags.getColor().trim();

		if (name.isBlank())
			errors.rejectValue("name", "required");
		else if (name.length() < MIN_NAME_LENGTH || name.length() > MAX_NAME_LENGTH)
			errors.rejectValue("nome", "size");
		if (description.length() > MAX_DESCRIPTION_LENGTH)
			errors.rejectValue("description", "size");
		if(color.length()<MIN_NAME_LENGTH )
			errors.rejectValue("color", "size");
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return Tag.class.equals(clazz);
	}
}

