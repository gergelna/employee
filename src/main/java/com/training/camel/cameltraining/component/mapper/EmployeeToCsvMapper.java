package com.training.camel.cameltraining.component.mapper;

import com.training.camel.cameltraining.service.dto.InEmployeeCsv;
import com.training.camel.cameltraining.service.dto.OutEmployeeCsv;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface EmployeeToCsvMapper {

    @Mappings({
                  @Mapping(source = "id", target = "employeeId"),
                  @Mapping(source = "name", target = "fullName")
              })
    OutEmployeeCsv employeeToOutput(InEmployeeCsv input);

}
