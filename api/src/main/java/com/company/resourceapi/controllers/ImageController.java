package com.company.resourceapi.controllers;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.company.resourceapi.dto.SearchCriteria;
import com.company.resourceapi.entities.Image;
import com.company.resourceapi.services.ImageService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/images")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ImageController {

  private Logger logger = LoggerFactory.getLogger(ImageController.class);

  private final ImageService imageService;

  @PostMapping
  @ApiOperation("Add Image with description")
  public ResponseEntity<Image> uploadFile(
      @RequestParam(name = "description", required = true) String description,
      @RequestPart(value = "file") final MultipartFile multipartFile) {
    final Image response = imageService.addImage(description, multipartFile);
    return ResponseEntity.status(CREATED).body(response);
  }
  

  @GetMapping
  @ApiOperation("List Images based on search criteria")
  public ResponseEntity<Page<Image>> search(@Valid SearchCriteria searchCriteria) {
    logger.info("New call with searchCriteria: {}", searchCriteria);
    return ResponseEntity.status(OK).body(imageService.search(searchCriteria));
  }
}
