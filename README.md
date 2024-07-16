# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```

## Phase 2 Server Design Sequence Diagram
View it here -> [sequencediagram.org](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATE4nN0y0MxWMYFXHlNa6l6020C3Vgd0BxTF5fP4AtB2OSYAAZCDRJIBNIZLLdvJF4ol6p1JqtAzqBJoIcDcuj3ZfF5vD6L9sgwr5iWw63O+nxPF+SwfgC5wFrKaooOUCAHjysL7oeqLorE2IJoYLphm6ZIUgatJvqMJpEuGFocjA3K8gagrCjAoriq60oADxJuy5SRGAPi3HaAraI6eIcbkMj4Sy5RGkJOFsjh5QAGqUEgABmYQQe8wmJgSEmkjAnrekGJEjmRoYUW6VHlHAuqZDAsbBtoMDJBkqRMaRYiseaolcXZQbxnKhjeZ57owNODqAbA3nyXuyFoNmuZ-jA3kRVcr4mfc1ZTkGs7NhOfQHNk5hJZxxZpv2TgwC+zEZZOfTTjl855UunCrt4fiBF4KDoDF9i+Mwx7pJkmCFReRTUNe0gAKK7pN9STc0LQPqoT7dNO0BIAAXvEiTlGx9WNug+SJcmMElvtc6YBFckBYhB59ahd2+hhGLYQFGq6SF5JgPZAbZQdaDkUyFmRtRtExn5jmMed6BmUD7GcZa6a8fx9n+fBxVicF5RhTJb14eZklGCg3CZD90MA7DZoRoUiPSMTFKGKjkNhOTl2nZFJU3T1fXxQgWClRjZxAkBBXnswUVXmVA6Vb0zUrp4bUbpCtq7tCMAAOKjqyA2nsNYvXeNaYVOrs0LfYo6rUG61bQQO1JeTR0CydwtpqzV1wU68owF9mujKosLqxSvtqM9WFabhOkE3pX1k39c6A1Tlk0Ty4NxszoVxzDslBR92MQ-I4fvVHIXILEwf+wnlEg+UYN2cqGujg6wWC4jCjKsHaOe4FnFY6F7eN0JV0S-B5SB2A5e8-zkuCylpZ9ObfvjJUPQLygACS0h7HlTwnpkBoVtv3RTDoCCgA2++ga2XyrwAcpfi6zC4T+NKLOTi5zht9gOL6r6oS8VCvUcG8t5Xx3oNfU7kl7ViPn0E+Z8L6jCgbVW+999iP2fnLVq65AjYD4tgbg8AbKGGDikcBes34GxKOUG8DQzYW2CFbKAm1tpoF2g7H+o476IKasdAC7M851n+hw0YXD7hX2gi7Sh4J9JEODrCayXpMjB1DliQu+M4bRwpLHQR8dKZVxpqDFOvk07yCFCzTOaBC452LgIhyBdZLqKpuUAySjRywhQaZZu1dCGKOIQPUxzk-Te04aOPRFlvKIx4nxJIHdcbo2sRoz6-jgBs0kcPLuVlZGjknolZK-C56rw3lBchRV0klnKsI9e0hinLiwe1AIlhiZIWSDAAAUhAHkDdRiBDgSABsJTRqlWoVUSkd4Wir0tn6a2LC2EWPyC+fBwBGlQDgBAJCUAPxPEKdU-4vChaplsQ1BZp9lmrPWZsqY2zinu0vCPGAAArDpaA5HtJ5Co166NHHhkIt9Iy5NK7AwMTXIxTNTFQwsVYnuucM4mJSQ4yOiSflyO2QC6U3ja6xLBWEbZYT4ZiUiRSaJXSUCdxElCmxwTRiku0mNDJPi9QwDQCgZImLgBORcjARZpy1nQDUQipxTFsBaFcaMdxQDpCzCZSy5JqLzTeMpEKhlrKzGUqqd0QJrkpWstSamQW0VXlxTUAlAWeSXZXFfqUj+VCYAVNlmYFqCtsEBC8EsrsXpYDAGwPgwgLDSG6xGswD2E1pqzXmq0YwezZ4cHpkNG5tLpHRpJigWEdMk3vL5eJClIBuB4FhLK6mPlpDeH8MAeu9RdDcA0EPK10jU0MxySazis8egWtIGUqWg47W1KAA)
