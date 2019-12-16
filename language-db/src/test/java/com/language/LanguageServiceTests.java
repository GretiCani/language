package com.language;

import com.language.model.Language;
import com.language.model.Translation;
import com.language.repository.LanguageRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;


@SpringBootTest
public class LanguageServiceTests {

    public LanguageServiceTests(LanguageRepository repository) {
        this.repository = repository;
    }

    LanguageRepository repository;
    Language languageAl,languageEn,languageIt;
    Translation translation1,translation2,translation3,translation4;

    @Test
    public void  add(){
        languageAl = languageEn =
                languageIt = new Language();
        translation1 = translation2 = translation3
                = translation4 = new Translation();
        translation1 = Translation.builder()
                .key("prop1").value("value1 al").build();
        translation2 = Translation.builder()
                .key("prop2").value("value2 al").build();
        translation3 = Translation.builder()
                .key("prop3").value("value3 al").build();
        translation4 = Translation.builder()
                .key("prop4").value("value4 al").build();
        languageAl = Language.builder().name("albanian")
                .translations(Arrays.asList(translation1,translation2,translation3,translation4))
                .build();

        translation1 = Translation.builder()
                .key("prop1").value("value1 en").build();
        translation2 = Translation.builder()
                .key("prop2").value("value2 en").build();
        translation3 = Translation.builder()
                .key("prop3").value("value3 en").build();
        translation4 = Translation.builder()
                .key("prop4").value("value4 en").build();
        languageEn = Language.builder().name("english")
                .translations(Arrays.asList(translation1,translation2,translation3,translation4))
                .build();

        translation1 = Translation.builder()
                .key("prop1").value("value1 it").build();
        translation2 = Translation.builder()
                .key("prop2").value("value2 it").build();
        translation3 = Translation.builder()
                .key("prop3").value("value3 it").build();
        translation4 = Translation.builder()
                .key("prop4").value("value4 it").build();
        languageIt = Language.builder().name("italian")
                .translations(Arrays.asList(translation1,translation2,translation3,translation4))
                .build();
       repository.saveAll(Arrays.asList(languageAl,languageEn,languageIt));
    }
}
