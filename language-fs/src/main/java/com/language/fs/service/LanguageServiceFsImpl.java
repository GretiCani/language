package com.language.fs.service;

import com.language.model.Language;
import com.language.model.Translation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class LanguageServiceFsImpl implements LanguageService {

    FileSystemUtils utils = new FileSystemUtils();
    private String fileExtension = ".txt";

    @Value("${language.fs.basedir}")
    private String baseDir;

    @Override
    public Language find(String name) {

        File dir = new File(baseDir);
        FilenameFilter filter = (file, s) -> s.startsWith(name);
        File[] files = dir.listFiles(filter);

        List<Language> lang = Stream.of(files).flatMap(file -> {
            Language language = new Language();
            try {
                language.setName(file.getName()
                        .replaceAll("[.][^.]+$", ""));
                language
                        .setTranslations(utils.
                                readProperties(new FileReader(file.getAbsoluteFile())));
            } catch (IOException e) {
                throw new RuntimeException();
            }
            return Stream.of(language);
        }).collect(Collectors.toList());

        return lang.get(0);


    }

    @Override
    public Language add(Language language) throws IOException {

        String fullPath = String.join("", baseDir, language.getName(), fileExtension);

        File file = new File(fullPath);
        if (file.exists())
            throw new RuntimeException("Language already exist");

        FileWriter fileWriter = new FileWriter(new File(fullPath));
        PrintWriter printWriter = new PrintWriter(fileWriter, true);

        for (Translation translation : language.getTranslations()) {
            printWriter
                    .append(translation.getKey()).append(" = ").append(translation.
                    getValue()).append('\n');
        }
        printWriter.close();
        fileWriter.close();
        return language;
    }

    @Override
    public Language update(Language language) throws IOException {

        String fullPath = String.join("", baseDir, language.getName(), fileExtension);
        File file = new File(fullPath);
        FileWriter fileWriter = new FileWriter(file, true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        if (!file.exists())
            throw new RuntimeException("Cann't update language. Language doesn't exist");

        Language lang = new Language();
        lang.setName(language.getName());
        lang.setTranslations(utils.readProperties(new FileReader(file)));

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

        for (Translation translation : language.getTranslations()) {
            bufferedWriter
                    .append(translation.getKey()).append(" = ").append(translation.
                    getValue()).append('\n');
        }
        bufferedWriter.flush();

        language.getTranslations().forEach(translation -> translation.setValue(""));
        List<Language> languages = findAll();
        for (Language language1 : languages){
            if(!language1.getName().equals(language.getName())) {
                fullPath = String.join("", baseDir, language1.getName(), fileExtension);
                try {

                    BufferedWriter bufferedWriter1 = new BufferedWriter(new FileWriter(new File(fullPath),true));

                    for (Translation translation : language.getTranslations()) {
                        bufferedWriter1
                                .append(translation.getKey()).append(" = ").append(translation.
                                getValue()).append('\n');
                    }
                    bufferedWriter1.flush();

                    bufferedWriter1.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        bufferedWriter.close();
        fileWriter.close();


        return language;
    }

    @Override
    public boolean delete(String name) {
        String fullPath = String.join("", baseDir, name, fileExtension);
        System.err.println("FilePath :: " + fullPath);
        File file = new File(fullPath);
        return file.delete();
    }

    @Override
    public List<Language> findAll() throws IOException {
        List<Language> languages;
        List<File> files = Files.list(Paths.get(baseDir))
                .map(path -> path.toFile())
                .collect(Collectors.toList());
        languages = files.stream()
                .map(file -> find(file.getName()))
                .collect(Collectors.toList());

        return languages;
    }

    @Override
    public Page<Translation> findPaginated(String name, PageRequest pageRequest) {

        Language language = find(name);
        List<Translation> translations = language.getTranslations()
                .stream().skip(pageRequest.getPageNumber()*pageRequest.getPageSize())
                .limit(pageRequest.getPageSize())
                .collect(Collectors.toList());
        return PageableExecutionUtils.getPage(translations,pageRequest,()->0L);
    }

    @Override
    public List<Language> getLanguageNamesOnly() throws IOException {

        return Files.list(Paths.get(baseDir)).flatMap(file ->{
            String fileName = file.getFileName().toString()
                    .replaceFirst("[.][^.]+$", "");
            return Stream.of(fileName);
        }).flatMap(filename ->{
            Language language = new Language();
            language.setName(filename);
            return Stream.of(language);
        }).collect(Collectors.toList());
    }
}


class FileSystemUtils {

    private BufferedReader bufferedReader;

    public  List<Translation> readProperties(FileReader fileReader) {

        bufferedReader = new BufferedReader(fileReader);
        List<String> lines = bufferedReader.lines()
                .collect(Collectors.toList());
        if(lines.size()>0) {
            return lines.stream().flatMap(l -> {
                String[] concatenatedLine = l.split(" = ", 2);
                ;

                Translation translation = new Translation(concatenatedLine[0], concatenatedLine[1]);
                return Stream.of(translation);
            }).collect(Collectors.toList());
        }

        return new ArrayList<>();

    }
}
