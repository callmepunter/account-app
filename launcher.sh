#!/usr/bin/env bash


mvn clean install -Pdev
java -jar -Dspring.profiles.active=dev ./target/account-app-1.0.0-SNAPSHOT.jar & echo $! > ./pid.file &