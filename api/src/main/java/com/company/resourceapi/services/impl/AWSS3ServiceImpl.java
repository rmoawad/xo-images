package com.company.resourceapi.services.impl;

import java.io.File;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.company.resourceapi.services.AWSS3Service;

@Service
public class AWSS3ServiceImpl implements AWSS3Service {

  private static final Logger LOGGER = LoggerFactory.getLogger(AWSS3ServiceImpl.class);

  @Autowired
  private AmazonS3 amazonS3;

  @Value("${aws.s3.bucket}")
  private String bucketName;

  @Override
  public String uploadImage(final File image) throws AmazonServiceException {
    LOGGER.info("Image upload in progress.");
    try {

      String s3FileName = uploadFileToS3Bucket(bucketName, image);
      LOGGER.info("Image upload is completed.");
      return s3FileName;
    } catch (final AmazonServiceException ex) {
      LOGGER.info("Image upload is failed.");
      LOGGER.error("Error= {} while uploading Image.", ex.getMessage());
      throw ex;
    }
  }

  @Override
  public void deleteImage(String s3ImageName) {
    amazonS3.deleteObject(bucketName, s3ImageName);
  }

  private String uploadFileToS3Bucket(final String bucketName, final File file) {
    final String uniqueFileName = LocalDateTime.now() + "_" + file.getName();
    LOGGER.info("Uploading file with name= " + uniqueFileName);
    final PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, uniqueFileName, file);
    amazonS3.putObject(putObjectRequest);
    return uniqueFileName;
  }

}