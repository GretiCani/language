package com.language.db.service;


import com.language.db.exception.ResourceNotFoundException;
import com.language.model.Language;
import com.language.model.Translation;
import com.language.repository.LanguageRepository;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class LanguageServiceImpl implements LanguageService {

    private final LanguageRepository languageRepository;

    @Override
    public Language find(String name) {
        return languageRepository.findByName(name)
                .orElseThrow(()->new ResourceNotFoundException(String.format("Language %s not found",name)));
    }

    @Override
    public List<Language> findAll() {
        return languageRepository.findAll();
    }

    @Override
    public List<Language> getLanguageNamesOnly() {
        return languageRepository.getLanguagesName();
    }

    @Override
    public Language add(Language language) {
        return languageRepository.save(language);
    }

    @Override
    public Language update(Language language) {

         Language lang = languageRepository.findById(language.getId())
                .orElseThrow(()->new ResourceNotFoundException(String
                        .format("Can't update language. Reason: language %S doesn't exist.",language.getName())));

        List<Translation> dublicatedKeys = new ArrayList<>();
        for (Translation t : language.getTranslations()){
            for (Translation l : lang.getTranslations())
                if(t.getKey().equals(l.getKey())){
                    dublicatedKeys.add(t);
                }
        }
        language.getTranslations().removeAll(dublicatedKeys);
        lang.getTranslations().addAll(language.getTranslations()
                .stream().collect(Collectors.toList()));

        languageRepository.save(lang);

        language.getTranslations().forEach(translation -> translation.setValue(""));

         languageRepository.findAll().forEach(l -> {
                    if (!l.getName().equals(language.getName())){
                        l.getTranslations().addAll(language.getTranslations().stream().distinct().collect(Collectors.toList()));
                        languageRepository.save(l);
                }
         });
        return lang;
    }

    @Override
    public void delete(String name){
        Language language = languageRepository.findByName(name)
                .orElseThrow(()->new ResourceNotFoundException(String
                        .format("Can't delete language. Reason: language %S doesn't exist.",name)));
        languageRepository.delete(language);
    }


    @Override
    public Page<Translation> findPaginated(String name, PageRequest pageRequest) {
        Language language = languageRepository.findByName(name)
                .orElseThrow(()->new ResourceNotFoundException(String
                .format("Can't delete language. Reason: language %S doesn't exist.",name)));
        List<Translation> translations = language.getTranslations()
                .stream().skip(pageRequest.getPageNumber()*pageRequest.getPageSize())
                .limit(pageRequest.getPageSize())
                .collect(Collectors.toList());
        return PageableExecutionUtils.getPage(translations,pageRequest,()->0L);
    }

    @Override
    public void importLanguageCsv(String languageName,MultipartFile languageTranslation) throws IOException {
        File file = convertMultiPartToFile(languageTranslation);
        List<Translation> translations;
        Language language = new Language();
        try (Reader reader=new FileReader(file)){
            @SuppressWarnings("unchecked")
            CsvToBean<Translation> translationCsvToBean = new CsvToBeanBuilder<Translation>(reader)
                    .withType(Translation.class)
                    .withIgnoreLeadingWhiteSpace(true).build();
            translations = translationCsvToBean.parse();
            language.setName(languageName);
            language.setTranslations(translations);
            languageRepository.save(language);

        }
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
}