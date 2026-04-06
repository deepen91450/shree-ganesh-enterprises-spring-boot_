package com.shreeganesh.enterprises.service;

import com.shreeganesh.enterprises.entity.WhyChooseUs;
import com.shreeganesh.enterprises.repository.WhyChooseUsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WhyChooseUsService {

    private final WhyChooseUsRepository repository;

    public WhyChooseUsService(WhyChooseUsRepository repository) {
        this.repository = repository;
    }

    public List<WhyChooseUs> getActiveItems() {
        return repository.findByActiveTrue();
    }

    public List<WhyChooseUs> getAll() {
        return repository.findAll();
    }

    public void save(WhyChooseUs item) {
        repository.save(item);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
