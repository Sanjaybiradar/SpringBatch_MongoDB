package com.learning.springbatch_mongodb.processor;

import com.learning.springbatch_mongodb.model.Employee;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class EmployeeProcessor implements ItemProcessor<Employee, Employee> {
    @Override
    public Employee process(Employee employee) throws Exception {
        return employee;
    }
}
