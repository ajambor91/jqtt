# jQTT
## Lightweight MQTT Broker (Java)
### A core component of an IoT ecosystem

### Project Overview
A minimalist MQTT broker written from scratch in pure Java. Designed as the backbone for a larger IoT system that includes:

* [MQTT Kafka Connector](https://github.com/ajambor91/mqtt-kafka-jconnector)
* Embedded and PC clients for edge devices (planned)
* Data pipeline integrating ElasticSearch for analytics (planned)

### Current Features

#### Basic MQTT protocol implementation
* TCP socket communication (no external networking libraries)
* Topic subscription management
* Message publishing fundamentals



### Quick Start

Clone repository:
```bash
git clone https://github.com/ajambor91/jqtt

```
Build project:

```bash
mvn clean package

```

Run broker:
```bash
 java -jar .\target\jQTT-1.0-SNAPSHOT.jar
```


### Short-Term Goals

* Performance optimization

* Refactor message routing implementation

* Unit/integration testing framework

* QoS support

* Core control packets (PUBACK/PUBREC etc)

* Client authentication/authorization

* Web-based admin interface (REST API) as a isolated and optional module


### Project Status
#### Early Development Phase
⚠️ Not production-ready ⚠️

#### Current limitations:
* Partial MQTT specification coverage
* Experimental message routing
* No QoS/authentication mechanisms


### License
### PROPRIETARY SOFTWARE – ALL RIGHTS RESERVED

### Commercial use strictly prohibited

### Redistribution forbidden without explicit permission

Educational/personal use permitted
For licensing inquiries contact: ajjambor912@gmail.com

### Part of IoT Ecosystem
Explore related components:
* [MQTT-Kafka Connector](https://github.com/ajambor91/mqtt-kafka-jconnector)



