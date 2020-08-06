package com.company.resourceapi.services.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;

import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import com.company.resourceapi.dto.SearchCriteria;
import com.company.resourceapi.entities.Image;
import com.company.resourceapi.exceptions.BadRequestException;
import com.company.resourceapi.repositories.ImageRepository;
import com.company.resourceapi.services.AWSS3Service;
import com.querydsl.core.types.Predicate;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ImageServiceImplTest {

  private static final String description = "description value";
  private static final String s3FileName = "s3FileName value";

  @InjectMocks
  private ImageServiceImpl imageServiceImpl;

  @Mock
  private ImageRepository imageRepository;

  @Mock
  private AWSS3Service awsService;

  private MockMultipartFile filePart;

  private ArgumentCaptor<Image> imageCaptor = ArgumentCaptor.forClass(Image.class);
  private ArgumentCaptor<Predicate> predicateCaptor = ArgumentCaptor.forClass(Predicate.class);

  @Before
  public void setUp() {
    when(imageRepository.save(any(Image.class))).thenReturn(new Image());
    when(awsService.uploadImage(any())).thenReturn(s3FileName);
    Mockito.doNothing().when(awsService).deleteImage(any());
    byte[] fileContent = "bar".getBytes(StandardCharsets.UTF_8);
    filePart = new MockMultipartFile("file", "name.png", null, fileContent);
  }

  @Test
  public void shouldSaveNewImage_whenParametersAreValid() {

    // Act
    imageServiceImpl.addImage(description, filePart);

    // Assert
    verify(imageRepository).save(imageCaptor.capture());
    SoftAssertions assertions = new SoftAssertions();
    assertions.assertThat(imageCaptor.getValue().getDescription()).isEqualTo(description);
    assertions.assertThat(imageCaptor.getValue().getS3Name()).isEqualTo(s3FileName);
    assertions.assertAll();
  }

  @Test
  public void shouldFailForFileType_whenFileNameNotPngOrJpeg() {

    // Arrange
    byte[] fileContent = "bar".getBytes(StandardCharsets.UTF_8);
    filePart = new MockMultipartFile("file", "name.temp", null, fileContent);
    // Act & Assert
    assertThrows(BadRequestException.class, () -> imageServiceImpl.addImage(description, filePart));
  }

  @Test
  public void shouldRevertUpload_whenDBSavingFails() {

    // Arrange
    when(imageRepository.save(any(Image.class))).thenThrow(new RuntimeException());

    // Act
    assertThrows(RuntimeException.class, () -> imageServiceImpl.addImage(description, filePart));

    // Assert
    verify(awsService).deleteImage(s3FileName);

  }

  @Test
  public void shouldSearchByDescription_whenDescriptionIsNeeded() {

    // Arrange
    String searchDescValue = "search Desc Value";
    SearchCriteria searchCriteria = SearchCriteria.builder().description(searchDescValue).build();

    // Act
    imageServiceImpl.search(searchCriteria);

    // Assert
    verify(imageRepository).findAll(predicateCaptor.capture(), any(Pageable.class));
    SoftAssertions assertions = new SoftAssertions();
    assertions.assertThat(predicateCaptor.getValue().toString())
        .isEqualTo("contains(image.description," + searchDescValue + ") && image.size >= 0");
    assertions.assertAll();

  }

  @Test
  public void shouldSearchByType_whenTypeIsNeeded() {

    // Arrange
    String type = "png";
    SearchCriteria searchCriteria = SearchCriteria.builder().type(type).size(100).build();

    // Act
    imageServiceImpl.search(searchCriteria);

    // Assert
    verify(imageRepository).findAll(predicateCaptor.capture(), any(Pageable.class));
    SoftAssertions assertions = new SoftAssertions();
    assertions.assertThat(predicateCaptor.getValue().toString())
        .isEqualTo("eqIc(image.type,png) && image.size >= 100");
    assertions.assertAll();

  }
}
