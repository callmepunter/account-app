#!/usr/bin/env bash

java -jar ./target/account-app-0.0.1-SNAPSHOT.jar --server.port=8989 & echo $! > ./pid.file &