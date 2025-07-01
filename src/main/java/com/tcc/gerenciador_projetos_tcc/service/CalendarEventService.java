package com.tcc.gerenciador_projetos_tcc.service;

import com.tcc.gerenciador_projetos_tcc.entity.CalendarEvent;
import com.tcc.gerenciador_projetos_tcc.repository.CalendarEventRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CalendarEventService {

    private final CalendarEventRepository repository;

    public CalendarEventService(CalendarEventRepository repository) {
        this.repository = repository;
    }

    public List<CalendarEvent> getEventsForUser(int userId) {
        return repository.findByOwnerIdAndType(userId, "USER");
    }

    public List<CalendarEvent> getEventsForGroup(int groupId) {
        return repository.findByOwnerIdAndType(groupId, "GROUP");
    }

    public CalendarEvent save(CalendarEvent event) {
        return repository.save(event);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public void deleteAllByGroupId(int groupId) {
        repository.deleteByOwnerIdAndType(groupId, "GROUP");
    }

}
