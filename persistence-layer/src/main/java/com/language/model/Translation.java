package com.language.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Translation {

    private String key;
    private String value;

}
