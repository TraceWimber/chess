package handler;

import com.google.gson.Gson;
import model.AuthData;
import model.UserData;
import service.GameService;
import service.UserService;
import spark.Route;
import spark.Response;
import spark.Request;

public class Handler {

    private static final Gson gson = new Gson();
    private static UserService userService = new UserService();
    private static GameService gameService = new GameService();

    public static Route handleRegister = (Request req, Response res) -> {
        UserData user = gson.fromJson(req.body(), UserData.class);
        AuthData auth = userService.register(user);

        res.type("application/json");
        return gson.toJson(auth);
    };

    public static Route handleLogin = (Request req, Response res) -> {
        return null;
    };

    public static Route handleLogout = (Request req, Response res) -> {
        return null;
    };

    public static Route handleList = (Request req, Response res) -> {
        return null;
    };

    public static Route handleCreate = (Request req, Response res) -> {
        return null;
    };

    public static Route handleJoin = (Request req, Response res) -> {
        return null;
    };

    public static Route handleClear = (Request req, Response res) -> {
        return null;
    };
}
