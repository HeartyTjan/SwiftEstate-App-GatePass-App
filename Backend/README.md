
# Estate Gate Pass Backend

The Estate Gate Pass system is a backend application designed to manage security access for residential estates. It facilitates the authentication of users, tenant management, visitor pass requests, and security personnel operations. This backend is built using Java Spring Boot.


## Features

- User Authentication: Secure login and registration system.

- Tenant Management: Handles tenant registration and information storage.

- Security Management: Manages security personnel for the estate.

- Visitor Pass Management: Handles visitor entry requests and approvals.

- OTP Authentication: Secure authentication through OTP validation.

- OTP Generation: Only Resident can generate OTP token to give access to visitor 

## Technologies Used

- Java (Spring Boot) – Backend framework

- MongoDB – Database for storing user, tenant, and security records

- Spring Security – Authentication and authorization

- Lombok – Code reduction and cleaner syntax

- Maven – Dependency management

- JUnit (Testing framework)


## Installation

Clone the repository:

```bash
  git clone https://github.com/HeartyTjan/Estate-Gate-Pass-Backend.git
```
Navigate to the project directory:
```bash
cd estate-gate-pass
```
Build the project using Maven:
```bash
mvn clean install
```

Configuration

Update application.properties to set up your MongoDB connection:
```bash
spring.data.mongodb.uri=mongodb://localhost:27017/estate_gatepass

```



## API Documentation

[API Reference](https://documenter.getpostman.com/view/41966478/2sAYk8w3qG)
