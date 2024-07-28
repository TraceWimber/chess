package server;

import handler.Handler;
import spark.*;
import com.google.gson.Gson;

public class Server {
    private static final Gson gson = new Gson();

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
            String msg = exception.getMessage();
            switch (msg) {
                case "Error: Username/Password is required.", "Error: Game name is required." -> response.status(400);
                case "Error: Unauthorized.", "Error: Incorrect password and/or username." -> response.status(401);
                case "Error: User already exists under that name." -> response.status(403);
                default -> response.status(500);
            }
            response.type("application/json");
            response.body(gson.toJson(new ErrorResponse(msg)));
        });

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private static class ErrorResponse {
        private final String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
