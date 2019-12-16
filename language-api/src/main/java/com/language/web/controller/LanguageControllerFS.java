package com.language.web.controller;

import com.language.fs.service.LanguageService;
import com.language.model.Language;
import com.language.model.Translation;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@AllArgsConstructor
@Validated
@RestController
@RequestMapping("/v2/language")
public class LanguageControllerFS {

    private LanguageService languageService;

    @GetMapping
    public ResponseEntity<List<Language>> all() throws IOException {
        return ResponseEntity.ok(languageService.findAll());
    }

    @GetMapping("/")
    public ResponseEntity<Page<Translation>> getLangByPage(@RequestParam(defaultValue = "0") Integer pageSize,
                                                           @RequestParam(defaultValue = "10") Integer pageIndex,
                                                           @RequestParam String name){
        Page<Translation> paginatedResult = languageService.findPaginated(name, PageRequest.of(pageSize,pageIndex));
        return ResponseEntity.ok(paginatedResult);
    }

    @GetMapping("/names")
    public ResponseEntity<List<Language>> getLanguagesName()throws IOException{
        return ResponseEntity.ok(languageService.getLanguageNamesOnly());
    }

    @GetMapping("/{name}")
    public ResponseEntity<Language> find(@PathVariable String name)throws IOException{
        return ResponseEntity.ok(languageService.find(name));
    }

    @PostMapping
    public ResponseEntity<Void> add(@RequestBody @Valid Language language)throws IOException{
        Language lang = languageService.add(language);
        return new ResponseEntity<>( HttpStatus.CREATED);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> delete(@PathVariable String name){
        languageService.delete(name);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody @Valid Language language)throws IOException {
        languageService.update(language);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
