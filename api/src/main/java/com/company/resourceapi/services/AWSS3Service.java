package com.company.resourceapi.services;

import java.io.File;

public interface AWSS3Service {
  
  String uploadImage(final File image);
  
  void deleteImage(String s3ImageName);

}
