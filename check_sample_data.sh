#!/usr/bin/env bash


curl -X GET --header 'Accept: application/json' 'http://localhost:8989/account-app/api/v1/accounts'

curl -X POST --header 'Content-Type: application/json' --header 'Accept: application/json;charset=UTF-8' -d '{ \
   "amount": 10, \
   "currency": "EUR", \
   "remarks": "from curl", \
   "type": "CREDIT" \
 }' 'http://localhost:8989/account-app/api/v1/accounts/303/transaction'

