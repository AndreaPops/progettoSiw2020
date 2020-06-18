package it.uniroma3.siw.taskmanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import it.uniroma3.siw.taskmanager.model.Commento;
import it.uniroma3.siw.taskmanager.repository.CommentoRepository;

 

@Service
public class CommentoService {
    
    @Autowired
    private CommentoRepository commentoRepository;
    
    @Transactional
    public void saveCommento(Commento commento) {
        commentoRepository.save(commento);
    }

}
