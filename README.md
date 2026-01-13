# SpringBoot_RampUp_Devesh_Sharma

## Description
A collection of APIs and java tools to manage list of users available on a social network along with details of people following each other on the social platform. Currently, it exposes authentication and a set of CRUD APIs for modifying/managing user’s details and list of users they are following and java based tools to load/export user into database.

## Tech Stack

- Core
  - Java 17
  - Spring Boot (v4)
    - Gradle

- DB & Persistence
  - Spring Data JPA
  - MySQL (database)
  - Flyway (database migrations)

- Security & Auth
  - Spring Security
  - Java Json Web Token (JJWT)

- Validation
  - Validation (Jakarta/Bean Validation)

- Dev / Compile-time Dependencies
  - Lombok (getter/setter/builder generation)
  - MapStruct (object mapping)

- Testing & Data
  - Datafaker (test data generation)

- Documentation & API
  - Springdoc

- Import tool
  - Spring Shell
  - OpenCSV

## Installation & Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/jtg-induction/SpringBoot_RampUp_Devesh_Sharma
   ```
2. Navigate to the project directory:
   ```bash
    cd SpringBoot_RampUp_Devesh_Sharma
   ```
3. Set environment variables. Refer to `.env.example` for required variables and set them up in your shell environment.
4. Build the project using Gradle:
   ```bash
    ./gradlew build
    ```
5. Run the application:
   ```bash
    ./gradlew bootRun
   ```

6. Docs are accessible at `/swagger-ui/index.html`

## Spring Shell

The project includes a Spring Shell component that provides a command-line interface for utility tools for the application. 
To use the Spring Shell, run the application jar with the `--shell` argument
```sh
./gradlew build && java -jar build/libs/minisocial-0.0.1-SNAPSHOT.jar --shell`
```
## Additional Resources
- [Design Document](https://docs.google.com/document/d/18DldasPoUK8Pp5TlUITPDnU2d9lvr2p6V0CjDahxNfo/edit?usp=sharing)
- [WBS](https://docs.google.com/spreadsheets/d/1w_lIvqW8_LCA_-W7phpHCAJYh4Wj7_bCFDvFw5shcVo/edit?usp=sharing)
