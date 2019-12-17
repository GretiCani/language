package com.language.db.service;


import com.language.db.exception.ResourceNotFoundException;
import com.language.model.Language;
import com.language.model.Translation;
import com.language.repository.LanguageRepository;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class LanguageServiceImpl implements LanguageService {

    private final LanguageRepository languageRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public Language find(String name) {
        Language language = languageRepository.findByName(name)
                .orElseThrow(()->new ResourceNotFoundException(String.format("Language %s not found",name)));
        return language;
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


        if(language.getName()!=null && !language.getName().equals("")) {
            return languageRepository.save(language);
        }
        else {
            Language lang = languageRepository.findByName("english")
                    .orElseThrow(()->new ResourceNotFoundException(String
                            .format("Can't update language. Reason: language %S doesn't exist.")));

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
                if (!l.getName().equals("english")){
                    l.getTranslations().addAll(language.getTranslations().stream().distinct().collect(Collectors.toList()));
                    languageRepository.save(l);
                }
            });
            return lang;
        }
    }

    @Override
    public Language update(Language language) {

        Language updateLang = languageRepository
                .findByName(language.getName())
                .orElseThrow(()->new ResourceNotFoundException("Language not found"));
        Translation tempTrans = new Translation();
        for (Translation translation : updateLang.getTranslations()){
            if(translation.getKey().equals(language.getTranslations().get(0).getKey()))
                tempTrans = translation;
        }

        if ( tempTrans.getKey() !=null && !tempTrans.getKey().equals("")) {
            updateLang.getTranslations().remove(tempTrans);
            updateLang.getTranslations().add(language.getTranslations().get(0));
        }

        return languageRepository.save(updateLang);
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
        return PageableExecutionUtils.getPage(translations,pageRequest,()->language.getTranslations().size());
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

    @Override
    public List<Language> deleteKeys(List<String> keys) {
        keys.forEach(key->{
            Query query = new Query(Criteria.where("translations")
                    .elemMatch(Criteria.where("key").is(key)));
            Update update = new Update().pull("translations",new Query(Criteria.where("key").is(key)));
            mongoTemplate.updateMulti(query,update,Language.class);
        });

        return languageRepository.findAll();
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
}
