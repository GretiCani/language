package com.language.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Document
public class Language {

    @Id
    private String id;
    @Indexed(unique = true)
    private String name;
    private List<Translation> translations;

    public Language(String name, List<Translation> translation) {
        this.name = name;
        this.translations = translation;
    }

    public Language(List<Translation> translations) {
    }
}
