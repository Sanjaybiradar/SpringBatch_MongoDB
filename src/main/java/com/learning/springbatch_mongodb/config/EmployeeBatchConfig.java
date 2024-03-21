package com.learning.springbatch_mongodb.config;

import com.learning.springbatch_mongodb.model.Employee;
import com.learning.springbatch_mongodb.processor.EmployeeProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.File;

@Configuration
@EnableBatchProcessing
public class EmployeeBatchConfig {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private EmployeeProcessor employeeProcessor;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Bean
    public FlatFileItemReader<Employee> reader(){
        FlatFileItemReader<Employee> reader = new FlatFileItemReader<Employee>();
        reader.setResource(new FileSystemResource("C:/Users/USER/Downloads/MOCK_DATA"));
        reader.setLineMapper(new DefaultLineMapper<>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames(new String[]{"id", "title", "first_name", "last_name", "email", "gender"});
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<Employee>() {{
                setTargetType(Employee.class);
            }});
        }});
        return reader;
    }

    @Bean
    public MongoItemWriter<Employee> writer(){
        MongoItemWriter<Employee> writer = new MongoItemWriter<>();
        writer.setTemplate(mongoTemplate);
        writer.setCollection("Employee");
        return writer;
    }

    @Bean
    public Step step() {
        return stepBuilderFactory.get("step1").<Employee, Employee>chunk(2)
                .reader(reader())
                .processor(employeeProcessor)
                .writer(writer())
                .build();
    }

    @Bean
    public Job job(){
        return jobBuilderFactory.get("job")
                .incrementer(new RunIdIncrementer())
                .start(step())
                .build();
    }
}
