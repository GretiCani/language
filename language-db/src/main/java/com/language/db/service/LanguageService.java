package com.language.db.service;


import com.language.model.Language;
import com.language.model.Translation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface LanguageService {

    Language find(String name);
    Language add(Language language);
    Language update(Language language);
    void delete(String name);
    List<Language> findAll();
    Page<Translation> findPaginated(String namePageRequest, PageRequest pageRequest);
    List<Language> getLanguageNamesOnly();
    void importLanguageCsv(String languageName ,MultipartFile languageTranslation)throws IOException;

}
