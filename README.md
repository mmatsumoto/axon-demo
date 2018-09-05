### Axonframework Bank Demo Application using Kotlin language
    
* Dispatch Interceptors
* Custom Command Handler
* Distributed Command Bus
* Saga
* Group Processors
* TrackingEventProcessor (start|stop|replay)
* Upcaster
* EventScheduler (Quartz)
* Kafka Integration
* Snapshot


### Start the Postgresql and Kafka:

```$ docker-compose up```

Start the applications in this order:

#### **axon-eureka** 

Eureka Server port 8761. Dashboard [http://localhost:8761](http://localhost:8761)

```
$ java -jar axon-eureka/target/axon-eureka-1.0.0-SNAPSHOT.jar
```

#### **axon-command**

If you want to see the Distributed Command Bus working, start at least two of these.

```
$ java -jar -Dserver.port=8085 axon-command/target/axon-command-1.0.0-SNAPSHOT.jar
$ java -jar -Dserver.port=8086 axon-command/target/axon-command-1.0.0-SNAPSHOT.jar
```
  
Wait few seconds and go back to [Eureka Dashboard](http://localhost:8761). You will see both instances registered there. 

#### **axon-gateway**

```
$ java -jar axon-gateway/target/axon-gateway-0.0.1-SNAPSHOT.jar
```

If you started more then one **axon-command** you can call every instance directly like 

```
$ curl -XGET http://localhost:8085/accounts
```
and 
```
$ curl -XGET http://localhost:8086/accounts
```

But that's not very pretty because you have to know the exactly address/port to every instance running.
Spring Gateway will help you make agnostics calls to **axon-command** application. 
Start this application and wait a few seconds to everything get register correctly. 
Now, start making calls to the gateway itself instead of calling the instances directly. 
Spring Gateway will load-balance your calls to all **axon-command** instances registered at Eureka-Server. 

Now, you can make all your calls to the Gateway at port 8760:

E.g.
```
$ curl -XGET http://localhost:8760/accounts
```

#### axon-kafka-consumer

**axon-command** is configured to send all events to a Kafka Topic. This project will consume those events using the Axon Tracking Processor approach.

```
java -jar axon-kafka-consumer/target/axon-kafka-consumer-1.0.0-SNAPSHOT.jar
```

#### Check some rest-api examples
[API Examples](axon-command/src/test/resources/accounts.http)

If you are using Intellij you can open this file and just click on the Green Arrow Icon to make the calls.


