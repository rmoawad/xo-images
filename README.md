# xo-images

This is XO task to upload and search for images

### Prerequisites
* [Angular CLI](https://github.com/angular/angular-cli) version 9.1.5.
* Git
* Maven
* Java8
* Free port 8080


## How to run locally
- Clone the repository
- go to project home.
- For Backend `cd api` then ->
```shell
mvn spring-boot:run
```
- OR 
```shell
mvn clean install
java -jar target/code-project-imageupload-0.0.1-SNAPSHOT.jar
```
- OR from code run the main which is in class com.company.resourceapi.Application.java

- For UI `cd ui` then ->
- Run `ng serve`. 
- Navigate to `http://localhost:4200/`. 
- The app it talking to localhost:8080 "So we assume that the API server is running locally too" 
- OR we could start the application with `ng serve --prod` to talk to cloud service.

## Covered points and tools

- Angular 9 is used
- Typescript
- Routing
- 
- Spring boot
- FlywayDB
- AWS S3
- AWS RDS
- Mockmvc
- Querydsl
- Exception handling
- Swagger

## Covered business

- Upload image
- Validation of image size and description
- Error handling
- Spinner onloading
- Routing
- Search for images
- Paging
- Demo is recorded

## What is missed
- ElasticSearch
