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

### Available Commands

- `import-user-details` - Imports user data from csv files
    - `-u` or `--user-details` - set the path for the user details csv file
        - Format for user details csv:
        ```csv
        Email,Password,First Name,Last Name,Marital Status,Age,Gender,R_Address,R_City,R_State,R_Country,R_Contact_No_1,R_Contact_No_2,O_Address,O_City,O_State,O_Country,O_Employee_Code,O_Company_Contact_No,O_Company_Contact_Email,O_Company_Name
        arjun.nair@zenithcorp.in,$2a$12$m7UoG79lSiUuVL785PNtmOpf7NJnq1tuetJdHINQfSmDci1hwOfVq,Arjun,Nair,MARRIED,32,MALE,"Flat 202, Palm Grove",Kochi,Kerala,India,+919845012345,+914842354567,"12th Floor, Cyber Tower",Kochi,Kerala,India,ZNC-441,+914844005000,info@zenithcorp.in,Zenith Corp
        ```
    - `-f` or `--following-details` - set the path for the following detail csv file
      - Format for following details csv:
        ```csv
        user_email,following
        arjun.nair@zenithcorp.in,maya.pillai@zenithcorp.in sara.khan@novatech.com vikram.rao@bluehorizon.co meera.joshi@stellaris.in
        ```
- `help` - Print available commands
