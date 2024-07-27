package server;

import handler.Handler;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", Handler.handleRegister);
        Spark.post("/session", Handler.handleLogin);
        Spark.delete("/session", Handler.handleLogout);
        Spark.get("/game", Handler.handleList);
        Spark.post("/game", Handler.handleCreate);
        Spark.put("/game", Handler.handleJoin);
        Spark.delete("/db", Handler.handleClear);

        Spark.exception(Exception.class, (exception, request, response) -> {
            response.status(500);
            response.body("Internal Server Error");
        });

        //This line initializes the server and can be removed once you have a functioning endpoint 
        //Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
