#!/usr/bin/env bash


mvn clean install -Pdev
java -jar -Dspring.profiles.active=dev ./target/account-app-0.0.1-SNAPSHOT.jar  --server.port=8989 & echo $! > ./pid.file &