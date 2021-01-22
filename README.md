# java-14


````
curl -X GET 'localhost:8080/accounts/1/balance'

curl -X POST -H "Content-type: application/json" -d "100" "http://localhost:8080/accounts/1/deposit"

curl -X POST -H "Content-type: application/json" -d "100" "http://localhost:8080/accounts/1/withdraw"

curl -X POST -H 'Content-Type: application/json' 'localhost:8080/accounts/1/transfer' \
--data-raw '{
"amount": 100,
"receiverAccountId": 1
}'
````