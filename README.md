A lightweight, scalable chat server built with Apache Kafka for real-time message streaming and Java for backend processing. 
This project demonstrates how to implement a reliable and efficient messaging system for local communication, using Kafka as the backbone for message distribution.

Features:
Real-Time Messaging: Ensures instant message delivery between connected clients.
Scalable Architecture: Designed to handle increasing traffic using Kafka's distributed system.
Easy Setup: Local development and deployment made simple.
Reliable Communication: Kafka guarantees message delivery with high fault tolerance.
Extensible Design: Easily adaptable for more complex chat systems or additional features.

Tech Stack:
Backend: Java
Message Broker: Apache Kafka
Protocol: TCP/IP for client-server communication

How It Works:
Producer: Chat messages are sent from clients and published to a Kafka topic.
Kafka Broker: Distributes messages to subscribed consumers in real-time.
Consumer: The chat server processes and delivers messages to connected clients.
