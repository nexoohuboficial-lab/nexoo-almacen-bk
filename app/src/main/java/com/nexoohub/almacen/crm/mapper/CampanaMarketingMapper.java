package com.nexoohub.almacen.crm.mapper;

import com.nexoohub.almacen.crm.dto.CampanaMarketingRequest;
import com.nexoohub.almacen.crm.dto.CampanaMarketingResponse;
import com.nexoohub.almacen.crm.entity.CampanaMarketing;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CampanaMarketingMapper {
    CampanaMarketing toEntity(CampanaMarketingRequest request);
    CampanaMarketingResponse toResponse(CampanaMarketing entity);
}
