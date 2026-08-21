# Pastry-And-Delicious-Aliments

A starter Maven Java project using Java 17, JUnit 5, Mockito, and JaCoCo code coverage.

## Requirements

- Java Development Kit (JDK) 17
- Apache Maven 3.9 or later
- MySQL Server and the `mysql` command-line client available on your `PATH`

Confirm the installed versions:

```powershell
java -version
mvn -version
```

Run all commands below from the project root—the folder containing `pom.xml`.

## Database reset during builds

Every Maven lifecycle build runs `mysql_sqltables.sql` first. The script **deletes and recreates** the `pastry_db` database, then creates its tables and triggers.

The default connection is MySQL on `localhost:3306` using the `root` user. Configure another server or user with Maven properties:

```powershell
mvn test "-Dmysql.host=localhost" "-Dmysql.port=3306" "-Dmysql.user=your_user"
```

Keep the password out of `pom.xml`. Set it for the current PowerShell session before running Maven:

```powershell
$env:MYSQL_PWD = "your_password"
```

To build without resetting the database, use:

```powershell
mvn test "-Dskip.database.reset=true"
```

## Run the application

```powershell
mvn exec:java
```

## Run the unit tests

```powershell
mvn test
```

Unit tests are stored in `src/UnitTests/java`.

To run one test class only:

```powershell
mvn "-Dtest=WelcomeServiceTest" test
```

## View code coverage

JaCoCo generates coverage automatically after a successful `mvn test` run.
Open this file in a browser or your IDE:

```text
target/site/jacoco/index.html
```

If a test fails, Maven stops before the automatic report step. Generate a report from the captured coverage data with:

```powershell
mvn jacoco:report
```

## Clean generated files

```powershell
mvn clean
```

This removes the generated `target` directory, including compiled classes, test reports, coverage reports, and packaged artifacts.
