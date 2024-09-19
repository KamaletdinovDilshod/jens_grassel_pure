# Jens Grassel Pure

A web service project built with Scala, leveraging modern functional programming libraries such as Cats Effect, http4s, Doobie, and PureConfig.

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Database Setup](#database-setup)
- [Running the Application](#running-the-application)
- [Contributing](#contributing)
- [License](#license)

## Overview

Jens Grassel Pure is a functional Scala web application that provides APIs for managing products. It uses Cats Effect to handle asynchronous and concurrent tasks, http4s for the web layer, and Doobie to interact with a PostgreSQL database.

The project demonstrates the use of modern functional programming paradigms and libraries in a practical, real-world application.

## Features

- Functional programming with Cats Effect
- Asynchronous and concurrent operations using IO monads
- PureConfig for configuration management
- Doobie for database interactions (PostgreSQL)
- http4s with Ember for building web services
- Flyway for database migrations

## Technologies Used

- **Scala 2.13**
- **Cats Effect** for functional effects and concurrency
- **http4s** for HTTP services
- **Doobie** for database access
- **PureConfig** for configuration loading
- **Flyway** for database migration
- **PostgreSQL** as the relational database
- **SLF4J + Logback** for logging

## Getting Started

To run this project, you'll need to have [Scala](https://www.scala-lang.org/) and [SBT](https://www.scala-sbt.org/) installed.

### Prerequisites

- Scala 2.13.x
- SBT 1.9+
- PostgreSQL

### Clone the Repository

```bash
git clone https://github.com/your-username/jens-grassel-pure.git
cd jens-grassel-pure
```

### Install Dependencies

Run the following command to download the necessary dependencies:

```bash
sbt update
```

### Configuration

You can configure the application by modifying the `application.conf` file located in the `src/main/resources` directory.

The configuration includes settings for the HTTP server, database, and logging. Sensitive values like passwords can be masked in the logs.

Example `application.conf`:

```hocon
api {
  host = "0.0.0.0"
  port = 8080
}

database {
  driver = "org.postgresql.Driver"
  url = "jdbc:postgresql://localhost:5432/puredb"
  user = "dbuser"
  pass = "dbpass"
}
```

### Database Setup

Make sure you have PostgreSQL installed and running. Set up the database using the following commands:

```sql
CREATE DATABASE puredb;
CREATE USER dbuser WITH PASSWORD 'dbpass';
GRANT ALL PRIVILEGES ON DATABASE puredb TO dbuser;
```

### Running Migrations

To apply database migrations using Flyway, run the following SBT task:

```bash
sbt flywayMigrate
```

### Running the Application

To run the application locally:

```bash
sbt run
```

The service will start on the host and port specified in `application.conf` (default: `http://localhost:8080`).

## Running Tests

To execute the unit tests:

```bash
sbt test
```

## API Endpoints

### Product Endpoints

- `GET /products` - List all products
- `POST /products` - Create a new product
- `GET /products/{id}` - Get details of a product by ID
- `PUT /products/{id}` - Update a product by ID

## Logging

Logging is configured using SLF4J and Logback. You can modify the logging behavior by editing the `logback.xml` file in `src/main/resources`.

## Contributing

Contributions are welcome! If you'd like to contribute to this project, please open an issue or submit a pull request.

### To contribute:

1. Fork the repository
2. Create a new branch (`git checkout -b feature/new-feature`)
3. Commit your changes (`git commit -m 'Add new feature'`)
4. Push the branch (`git push origin feature/new-feature`)
5. Open a pull request

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for more details.
```

### Customizing the README:
- Replace `"your-username"` with your actual GitHub username and repository link.
- Update the API endpoints section as needed to reflect the actual endpoints your project exposes.
- Modify the configuration and database details to fit your environment.

This README provides a good overview of the project, its dependencies, setup, and usage, making it easy for others to contribute or use your project.
