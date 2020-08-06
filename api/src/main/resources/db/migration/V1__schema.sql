CREATE TABLE `image` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `s3_name` varchar(255) NOT NULL,
  `type` varchar(10) NOT NULL,
   `size` int  NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `created_date` datetime NOT NULL,
  `last_modified_date` datetime NOT NULL,
  PRIMARY KEY (`ID`)
);
