package com.language.repository;

import com.language.model.Language;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LanguageRepository extends MongoRepository<Language,String> {

    Optional<Language> findByName(String name);
    List<Language> findByNameContainsAndName(String name);

    @Query(value = "{}", fields = "{ 'name' : 1 }")
    List<Language> getLanguagesName();
}
