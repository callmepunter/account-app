# Getting Started
[![Build Status](https://travis-ci.org/callmepunter/account-app.svg?branch=master)](https://travis-ci.org/callmepunter/account-app)

[![codecov](https://codecov.io/gh/callmepunter/account-app/branch/master/graph/badge.svg)](https://codecov.io/gh/callmepunter/account-app)

A simple spring boot demonstrator for a multi currency account. Basic transaction of credit/debit is 
supported. 
 


### Built With

---

* Spring Boot
* Flyway    
* Maven
* H2 Database

### Docker image is available

--- 
    docker pull callmepunter/accounting

    docker run -p 8085:8085 -p 8086:8086 --name accouting callmepunter/accounting

