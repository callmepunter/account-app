# Getting Started
[![Build Status](https://travis-ci.org/callmepunter/account-app.svg?branch=master)](https://travis-ci.org/callmepunter/account-app)

[![codecov](https://codecov.io/gh/callmepunter/account-app/branch/master/graph/badge.svg)](https://codecov.io/gh/callmepunter/account-app)

A Sample application created with Spring Boot.
This is a simple scaffold running port 8989 using in memory H2 database. 


* Each account is treated as multi-currency account and more holder can 
maintain various currencies linked to single account. 

* Transactions can be performed on account. Each transaction is recorded. 


To start the application use 
./launcher.sh
swagger is available at the application root to play
http://localhost:8989/accounts-app

h2 console can be accessed at 
http://localhost:8989/accounts-app/h2-console

To stop the application use 
./shutdown.sh

If any problems encountered during launch related to locking of H2 DB. Use the 
./locks.sh to remove the locks and launch again

