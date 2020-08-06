package com.company.resourceapi.services.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.securityhub.model.InternalException;
import com.company.resourceapi.dto.SearchCriteria;
import com.company.resourceapi.entities.Image;
import com.company.resourceapi.entities.QImage;
import com.company.resourceapi.exceptions.BadRequestException;
import com.company.resourceapi.repositories.ImageRepository;
import com.company.resourceapi.services.AWSS3Service;
import com.company.resourceapi.services.ImageService;
import com.google.common.base.Strings;
import com.querydsl.core.BooleanBuilder;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ImageServiceImpl.class);

  private final AWSS3Service awsService;

  private final ImageRepository imageRepo;

  public Image addImage(String description, MultipartFile multipartFile) {
    final File image = convertMultiPartFileToFile(multipartFile);
    String type = getFileExtention(image.getName());
    validateFileType(type);
    String s3ImageName = awsService.uploadImage(image);
    try {
      Image imageDetails = Image.builder().type(type).size(multipartFile.getSize()).s3Name(s3ImageName)
          .description(description).build();
      image.delete();
      return imageRepo.save(imageDetails);
    } catch (Exception ex) {
      LOGGER.info("Image to DB saving is failed.");
      LOGGER.error("Error= {} while saving DB record.", ex.getMessage());
      awsService.deleteImage(s3ImageName);
      throw new InternalException("Sorry we are facing issue " + ex.getMessage());
    }
  }

  private String getFileExtention(String fileName) {
    String[] splittedName = fileName.split("\\.");
    return splittedName[splittedName.length - 1];
  }

  private File convertMultiPartFileToFile(final MultipartFile multipartFile) {
    final File file = new File(multipartFile.getOriginalFilename());
    try (final FileOutputStream outputStream = new FileOutputStream(file)) {
      outputStream.write(multipartFile.getBytes());
    } catch (final IOException ex) {
      LOGGER.error("Error converting the multi-part file to file= ", ex.getMessage());
    }
    return file;
  }

  private void validateFileType(String type) {
    if (!"png".equalsIgnoreCase(type) && !"jpeg".equalsIgnoreCase(type)) {
      throw new BadRequestException("Only PNG and JPEG files are allowed");
    }
  }

  @Override
  public Page<Image> search(@Valid SearchCriteria searchCriteria) {
    BooleanBuilder booleanBuilder = new BooleanBuilder();
    QImage qImage = QImage.image;
    if (!Strings.isNullOrEmpty(searchCriteria.getDescription())) {
      booleanBuilder = booleanBuilder.and(qImage.description.contains(searchCriteria.getDescription()));
    }

    if (!Strings.isNullOrEmpty(searchCriteria.getType())) {
      booleanBuilder = booleanBuilder.and(qImage.type.equalsIgnoreCase(searchCriteria.getType()));
    }

    booleanBuilder = booleanBuilder.and(qImage.size.goe(searchCriteria.getSize()));

    return imageRepo.findAll(booleanBuilder.getValue(),
        PageRequest.of(searchCriteria.getPage(), 20, Sort.by(Sort.Direction.ASC, "id")));
  }
}
