package com.company.resourceapi.services.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import java.io.File;

import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AWSS3ServiceImplTest {

  private static final String s3FileName = "s3FileName value";

  @InjectMocks
  private AWSS3ServiceImpl awsS3ServiceImpl;

  @Mock
  private AmazonS3 amazonS3;
  
  private ArgumentCaptor<PutObjectRequest> fileCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
  
  @Before
  public void setUp() {
  }

  @Test
  public void shouldCallPutObject_whenAddImageIsCalled() throws Exception{
 
    // Act
    File file = new File("File name");
    awsS3ServiceImpl.uploadImage(file);

    // Assert
    verify(amazonS3).putObject(fileCaptor.capture());
    SoftAssertions assertions = new SoftAssertions();
    assertions.assertThat(fileCaptor.getValue().getKey()).contains(file.getName());
    assertions.assertThat(fileCaptor.getValue().getFile()).isEqualTo(file);
    assertions.assertAll();
  }
  
  @Test
  public void shouldCallDeleteObject_whenDeleteImageIsCalled() throws Exception{
 
    // Act
    awsS3ServiceImpl.deleteImage(s3FileName);

    // Assert
    verify(amazonS3).deleteObject(any(), eq(s3FileName));
  }
}
