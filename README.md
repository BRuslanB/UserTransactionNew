# User Transaction Service (version 2)
This Java-based service for storing and processing spending transactions and setting custom spending limits.

Note: This custom transaction service was created as a test task for an internship at one of the IT companies. For experimental purposes, REST API services were grounded in GraphQL and a call to the gRPC method (You will find scripts for verification in the folder UserTransactionNew\scripts).


## Features
The User Transaction Service allows you to process two subtypes of services (banking and user):

1. Banking services:
* Receive and save transaction costs;
* Receive current exchange rates from an external resource;
* Convert the transaction amount to determine if the limit has been exceeded. The amount is converted at the current exchange rate and the type of currency of the established limit.
2. User services:
* Set a limit for subsequent transactions in the selected currency;
* Receive a list of all user limits;
* Receive a list of transactions that have exceeded the limit.

## Technologies Used
The User Transaction Service is built using the following technologies:

* Java 21
* Spring Boot
* Spring Data JPA
* Hibernate
* PostgreSQL
* Cassandra NoSql
* FlyWay
* Log4j2
* JUnit
* H2

## Prerequisites
Before running the Service, you will need to have the following software installed on your machine:

* Java Development Kit (JDK) 21 or higher
* Docker

## Getting Started
To run the Service locally, follow these steps:

1. Clone the repository to your local machine:
```bash
git clone https://github.com/BRuslanB/UserTransactionNew.git
```
2. Launch:
```bash
docker-compose up -d
```

## Contributing
If you would like to contribute to developing this Service, please submit a pull request or open an issue on the GitHub repository.

## License
This Service is licensed under the MIT License. See the LICENSE file for more details.
