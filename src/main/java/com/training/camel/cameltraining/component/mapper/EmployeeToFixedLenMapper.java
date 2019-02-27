package com.training.camel.cameltraining.component.mapper;

import com.training.camel.cameltraining.service.dto.InEmployeeCsv;
import com.training.camel.cameltraining.service.dto.OutEmployeeFixedLen;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface EmployeeToFixedLenMapper {

    @Mappings({
                  @Mapping(source = "id", target = "employeeId"),
                  @Mapping(source = "name", target = "fullName")
              })
    OutEmployeeFixedLen employeeToOutput(InEmployeeCsv input);

}
