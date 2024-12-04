### Build
### Spin docker : 
docker-compose up -d

### Run using profiles

-Dspring.profiles.active=client

and

-Dspring.profiles.active=server

### Run from command line
 java -jar -Dspring.profiles.active=client -Dcounter=10 target/rabbit-topic-rpc-0.0.1-SNAPSHOT.jar
### Clean up:
stop applications

docker-compose down
