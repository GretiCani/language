package com.language.fs.service;

import com.language.model.Language;
import com.language.model.Translation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.util.List;

public interface LanguageService {
    Language find(String name)throws IOException;
    Language add(Language language)throws IOException;
    Language update(Language language) throws IOException;
    boolean delete(String name);
    List<Language> findAll()throws IOException;
    Page<Translation> findPaginated(String name, PageRequest pageRequest);
    List<Language> getLanguageNamesOnly()throws IOException;
}
