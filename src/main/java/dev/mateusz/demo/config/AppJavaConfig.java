package dev.mateusz.demo.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import dev.mateusz.demo.domain.ComparativeAnalysis;
import dev.mateusz.demo.domain.PreparationDataFromFile;
import dev.mateusz.demo.domain.VideoFrameExtracter;
import javafx.stage.Stage;

@Configuration
public class AppJavaConfig {
	
    @Autowired 
    SpringFXMLLoader springFXMLLoader;

    @Bean
    @Lazy(value = true)
    public StageManager stageManager(Stage stage) throws IOException {
        return new StageManager(springFXMLLoader, stage);
    }
    
    @Bean
    public VideoFrameExtracter videoFrameExtracter() {
    	return new VideoFrameExtracter();
    }
    
    @Bean
    public PreparationDataFromFile preparationDataFromFile() {
    	return new PreparationDataFromFile();
    }
    
    @Bean
    public ComparativeAnalysis comparativeAnalysis() {
    	return new ComparativeAnalysis();
    }

}
