FROM openjdk:14-alpine
COPY build/libs/agent-orchestrator-*-all.jar agent-orchestrator.jar
EXPOSE 8080
CMD ["java", "-Dcom.sun.management.jmxremote", "-Xmx128m", "-jar", "agent-orchestrator.jar"]