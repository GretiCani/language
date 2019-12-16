package com.language.model.dto;

import com.language.model.Translation;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@Builder
@EqualsAndHashCode
@ToString
public class LanguageDto {
    private String id;
    private String language;
    private List<Translation> translations;
}
