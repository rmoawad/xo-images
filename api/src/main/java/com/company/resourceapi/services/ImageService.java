package com.company.resourceapi.services;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.company.resourceapi.dto.SearchCriteria;
import com.company.resourceapi.entities.Image;

public interface ImageService {

    Image addImage(String description, MultipartFile multipartFile);

    Page<Image> search(@Valid SearchCriteria searchCriteria);
}
