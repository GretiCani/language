package com.language.web;

import com.language.db.service.LanguageService;
import com.language.repository.LanguageRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
public class CMD implements CommandLineRunner {

    private final com.language.db.service.LanguageService languageService;
    private final LanguageRepository languageRepository;

    public CMD(LanguageService languageService, LanguageRepository languageRepository) {
        this.languageService = languageService;
        this.languageRepository = languageRepository;
    }

    @Override
    public void run(String... args) {

        String key = "prop2";
        languageService.deleteKeys(key);

        }


}