package com.company.resourceapi.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.company.resourceapi.dto.SearchCriteria;
import com.company.resourceapi.entities.Image;
import com.company.resourceapi.exceptions.BadRequestException;
import com.company.resourceapi.exceptions.GlobalExceptionHandler;
import com.company.resourceapi.services.ImageService;
import com.google.common.collect.Lists;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class ImageControllerIT {

  private static final long ID_1 = 1;

  private MockMvc mockMvc;

  @Mock
  private ImageService imageService;
  
  private MockMultipartFile filePart;

  @InjectMocks
  private ImageController imageController;

  @Before
  public void setup() throws Exception {
    mockMvc = MockMvcBuilders.standaloneSetup(imageController)
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();
    
    byte[] fileContent = "bar".getBytes(StandardCharsets.UTF_8);
    filePart = new MockMultipartFile("file", "orig", null, fileContent);
  }

  @Test
  public void returnSavedImageObject_whenPostSuccess() throws Exception {
    // Arrange
    Image image = new Image();
    image.setId(ID_1);
    when(imageService.addImage(any(), any())).thenReturn(image);

    // Act & Assert
    mockMvc
        .perform(multipart("/images").file(filePart).param("description", "any"))
        .andExpect(status().isCreated());
  }

  @Test
  public void returnException_whenDescriptionsIsMisssing() throws Exception {
    // Act & Assert
    mockMvc.perform(multipart("/images").file(filePart))
        .andExpect(status().is4xxClientError())
        .andExpect(result -> assertThat("Required String parameter 'description' is not present")
            .isEqualTo(result.getResolvedException().getMessage()));
  }

  @Test
  public void returnException_whenServiceReturnExxception() throws Exception {
    
    // Arrange
    when(imageService.addImage(any(), any())).thenThrow(new BadRequestException("bad message1"));
    
    // Act & Assert
    mockMvc.perform(multipart("/images").file(filePart).param("description", "any"))
        .andExpect(result -> assertThat("bad message1")
            .isEqualTo(result.getResolvedException().getMessage()))
        .andExpect(status().isBadRequest());

  }
  
  @Test
  public void returnImagesPage_whenSearchForImages() throws Exception {
    // Arrange
    Page<Image> page =new PageImpl<>(Lists.newArrayList(new Image(), new Image()));
    when(imageService.search(any(SearchCriteria.class))).thenReturn(page);

    // Act & Assert
    mockMvc.perform(get("/images")).andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }
}
