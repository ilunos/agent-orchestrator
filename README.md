# Agent Orchestrator
The Agent Orchestrator is responsible for... well orchestrating a number of agents within a central access point.  
It ensures agents availability and controls which Agents are enabled.  

Agents generally register themself at an Orchestrator
and any requests that need to go to the agent will by proxied by the Orchestrator.


## Building 
#### Requirements
- JDK 13 or higher

Building on Unix:
```
./gradlew build
```

Building on Windows:
```
gradlew build
```

### Building Docker Image *(Optional)*
```
docker build -t ilunos/agent-orchestrator:[VERSION] .
```

## Running
Ensure your Orchestrator is running somewhere, where it can reach all the Agents, be it in Docker Engines
or on Host Systems.

Running on Host:
```
java -jar build/libs/agent-orchestrator-[VERSION]-all.jar
```

Running in Docker:
```
docker run --name ilunos/agent-orchestrator --publish 8080:8080 --volume config:/config ilunos/agent-orchestrator:[VERSION]
```

# Important TODOS
- Implement Security
    - Configurable Password to prevent anyone from registering ✓
    - Secure Enable / Disable Agent endpoints ✓
    - Do we allow anyone to use the proxy and let the agent handle auth?
- Do all the things