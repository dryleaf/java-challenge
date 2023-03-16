**Author**: Josimar Lopes
- 9+ years experience using Java.
- 6 years using Spring Boot.
- Practical project experience as a Team Leader.

## [Begin] Notes
The objective of this task is to perform refactoring on the source code, determine improvement points and perform such improvements according to best practices.

### Refactoring points
1. **API Versioning**
In order to properly consume an API, the client must have prior knowledge of Media Types such as "Accept" or "Content-Type", etc... This is the basis of content negotiation.
So, we will perform API versioning using Media Types, and any changes in features on the API service is easier to maintain and release a new version of the API without breaking existing URI.
    - *i.e. We remove URI based versioning because it is not a best practice, in favor of Media Types in the "Content-Type" header:*
    - :x: ~~`/api/v1`, `Content-Type: application/json`~~
    - :white_check_mark:`/api`, `Content-Type: application/vnd.axa.v1+json`


2. **Test cases**
The test cases focus is performed on all Http Methods in the `EmployeeController.class`.
    - `#shouldPostEmployee_andCheckIfCreated_withSuccess()`
    - `#shouldGetAllEmployees_andCheckIfFetched_withSuccess()`
    - `#shouldPutEmployee_andCheckIfUpdated_withSuccess()`
    - `#shouldGetOneEmployeeByTextId_andCheckIfFetched_withSuccess()`

3. **Securing the endpoints**
- Uses OAuth 2.0 to secure the endpoint. It uses in-memory strategy to generate the Bearer token. We define 2 types of scopes for demonstration purposes, which secures and grants access to endpoints according to the scope provided and client credentials. i.e. `employee:write` and `employee:read`.
    
    *HTTP METHODS:* POST, PUT, DELETE, GET
    ```
    Access Token URL: http://localhost:8080/oauth/token
    Grant Type: Client Credentials
    Client ID: axa-manager
    Client Secret: manager-secret
    Scope: employee:write
    ```
    
    *HTTP METHODS:* GET
    ```
    Access Token URL: http://localhost:8080/oauth/token
    Grant Type: Client Credentials
    Client ID: axa-client
    Client Secret: client-secret
    Scope: employee:read
    ```
- In ideal scenarios, we would use an open source server like Keycloak.

4. **Documentation**
- We use Spring Docs OpenAI to swagger-ui for providing a more descriptive documentation, containing useful annotations. The documentation also provides a way to test the API using client credentials.
- Swagger UI : http://localhost:8080/api/swagger-ui

5. **Caching**
- We add a basic in-memory caching and eviction strategy. In ideal scenarios, the a cache server like redis would be used.

6. **Comments**
In all possible parts of the project, we add documentation comments to the classes, methods, and inline comments.

7. **logging**
We add lombok logger.

8. **Coding Style**
- We use Google coding style in this project.

**In-memory H2 DB**
- H2 UI : http://localhost:8080/h2-console

## [End]
--------------------------------------------------

### How to use this spring-boot project

- Install packages with `mvn package`
- Run `mvn spring-boot:run` for starting the application (or use your IDE)

Application (with the embedded H2 database) is ready to be used ! You can access the url below for testing it :

- Swagger UI : http://localhost:8080/api/swagger-ui
- H2 UI : http://localhost:8080/h2-console

> Don't forget to set the `JDBC URL` value as `jdbc:h2:mem:testdb` for H2 UI.



### Instructions

- download the zip file of this project
- create a repository in your own github named 'java-challenge'
- clone your repository in a folder on your machine
- extract the zip file in this folder
- commit and push

- Enhance the code in any ways you can see, you are free! Some possibilities:
  - Add tests
  - Change syntax
  - Protect controller end points
  - Add caching logic for database calls
  - Improve doc and comments
  - Fix any bug you might find
- Edit readme.md and add any comments. It can be about what you did, what you would have done if you had more time, etc.
- Send us the link of your repository.

#### Restrictions
- use java 8


#### What we will look for
- Readability of your code
- Documentation
- Comments in your code 
- Appropriate usage of spring boot
- Appropriate usage of packages
- Is the application running as expected
- No performance issues

#### Your experience in Java

Please let us know more about your Java experience in a few sentences. For example:

- I have 3 years experience in Java and I started to use Spring Boot from last year
- I'm a beginner and just recently learned Spring Boot
- I know Spring Boot very well and have been using it for many years
