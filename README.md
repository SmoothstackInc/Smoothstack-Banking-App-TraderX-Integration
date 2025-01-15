# TraderX 

Please follow this document to set up the Secure Sentinel Bank integration with TraderX

## Getting started

Clone all the repositories in the root directory.

## Use Maven to package the projects

Update application.yml or application.properties values with your PostgreSQL database credentials

Run mvn clean package in these folders:
- accounts-service
- api-gateway
- eureka-server
- investment-orchestrator
- live-data-service
- user-service

## Run each project

- Please run the projects in this order using these commands:

apigateway 

    java -jar target/api-gateway-0.0.1-SNAPSHOT.jar

eurekaserver

    java -jar target/eureka-server-3.1.2.jar

userservice

    java -jar target/UserService-0.0.1-SNAPSHOT.jar

accountservice

    java -jar target/accounts-service-0.0.1-SNAPSHOT.jar


live-data-service

    java -jar target/live_data-0.0.1-SNAPSHOT.jar

investment-orchestrator

    java -jar target/investment-orchestrator-0.0.1-SNAPSHOT.jar

frontend

    npm install
    npm run dev

## Using the traderX implementation
Go to http://localhost:5173/ to use the website.

1. Create an account
2. Navigate to investments tab
- Your account should be populated with 100 total shares of stocks in 5 different companies
- Click on a stock to be taken to the individual stock page where you are able to buy/sell the stocks and receive investment advice