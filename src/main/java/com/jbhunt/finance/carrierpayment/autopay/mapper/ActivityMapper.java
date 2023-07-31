package com.jbhunt.finance.carrierpayment.autopay.mapper;

import com.jbhunt.finance.carrierpayment.autopay.dto.ActivityDTO;
import com.jbhunt.finance.carrierpayment.autopay.dto.ActivityResponseDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.Activity;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
@DecoratedWith(ActivityDecorator.class)

public interface ActivityMapper {

    public Activity activityDTOToActivity(ActivityDTO activityDto);

    @Mapping(target = "activityTypeDesc", ignore = true)
    @Mapping(target = "activityPerformTypeDesc", ignore = true)
    @Mapping(target = "scac", ignore = true)
    @Mapping(target = "loadNumber", ignore = true)
    @Mapping(target = "dispatchNumber", ignore = true)
    @Mapping(target = "activityDetailTypeDescription", ignore = true)
    public ActivityResponseDTO activityDTOToDetailsDTO(ActivityDTO activityDto);
}
