package com.ayds.Cloudmerce.model.dto.report;

public record ProductDTO (
        String name,
        Float price,
        Long stock,
        String creationAt
){
}
