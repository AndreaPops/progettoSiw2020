package it.uniroma3.siw.taskmanager.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.taskmanager.model.Tag;
import it.uniroma3.siw.taskmanager.model.Task;
import it.uniroma3.siw.taskmanager.repository.TagRepository;

@Service
public class TagService {
	@Autowired
	TagRepository tagRepository;

	@Transactional
	public void saveTag( Tag tag) {
		this.tagRepository.save(tag);
	}
	
	@Transactional
	public void addTaskToTag(Tag tag,Task task) {
		tag.getListaTask().add(task);
		this.tagRepository.save(tag);
	}
}
