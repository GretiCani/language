package com.language.web;

import com.language.db.service.LanguageService;
import com.language.model.Language;
import com.language.model.Translation;
import com.language.repository.LanguageRepository;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;


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

        String key = "prop1";
        languageService.deleteKeys(key);

        }


}