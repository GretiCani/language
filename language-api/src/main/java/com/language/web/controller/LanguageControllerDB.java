package com.language.web.controller;


import com.language.db.service.LanguageService;
import com.language.model.Language;
import com.language.model.Translation;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/v1/language")
public class LanguageControllerDB {

  private final LanguageService languageService;

  @GetMapping
  public ResponseEntity<List<Language>> all(){
      return ResponseEntity.ok(languageService.findAll());
  }

  @GetMapping("/find")
  public ResponseEntity<Page<Translation>> find(@RequestParam(defaultValue = "10") Integer pageSize,
                                       @RequestParam(defaultValue = "0") Integer pageIndex,
                                       @RequestParam(name = "Language") String name){
      Page<Translation> paginatedResult = languageService.findPaginated(name,PageRequest.of(pageIndex,pageSize));
      return ResponseEntity.ok(paginatedResult);
  }

  @GetMapping("/names")
  public ResponseEntity<List<Language>> getLanguagesName(){

      return ResponseEntity.ok(languageService.getLanguageNamesOnly());
  }

  @GetMapping("/")
  public ResponseEntity<Page<Translation>> getLangByPage(@RequestParam(defaultValue = "1") Integer pageIndex,
                                                         @RequestParam(defaultValue = "10") Integer pageSize,
                                                         @RequestParam String name){
      Page<Translation> paginatedResult = languageService.findPaginated(name,PageRequest.of(pageIndex,pageSize));
      return ResponseEntity.ok(paginatedResult);
  }

  @PostMapping
  public ResponseEntity<Void> add(@RequestBody @Valid Language language, UriComponentsBuilder builder){
      Language lang = languageService.add(language);
      HttpHeaders headers = new HttpHeaders();
      headers.setLocation(builder.path("/v1/language/{name}").buildAndExpand(lang.getName()).toUri());
      return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }

  @PutMapping
  public ResponseEntity<Void> update(@RequestBody @Valid Language language){
      languageService.update(language);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @DeleteMapping("/{name}")
  public ResponseEntity<Void> delete(@PathVariable String name){
      languageService.delete(name);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @PostMapping("/import")
  public ResponseEntity<Void> importLanguage(@RequestParam String name,@RequestParam MultipartFile file)throws IOException {
      languageService.importLanguageCsv(name,file);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }


}
