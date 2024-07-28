package handler;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.GameService;
import service.UserService;
import spark.Route;
import spark.Response;
import spark.Request;
import java.util.ArrayList;

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
        UserData user = gson.fromJson(req.body(), UserData.class);
        AuthData auth = userService.login(user);

        res.type("application/json");
        return gson.toJson(auth);
    };

    public static Route handleLogout = (Request req, Response res) -> {
        String token = req.headers("authorization");
        AuthData auth = new AuthData(token, null);
        userService.logout(auth);

        res.type("application/json");
        return "{}";
    };

    public static Route handleList = (Request req, Response res) -> {
        String token = req.headers("authorization");
        AuthData auth = new AuthData(token, null);
        ArrayList<GameData> games = gameService.listGames(auth);

        res.type("application/json");
        return gson.toJson(games);
    };

    public static Route handleCreate = (Request req, Response res) -> {
        String token = req.headers("authorization");
        AuthData auth = new AuthData(token, null);
        GameData gameName = gson.fromJson(req.body(), GameData.class);

        GameData game = gameService.createGame(auth, gameName);

        res.type("application/json");
        //TODO: Do I need to change this so that it only returns the gameID and not the whole GameData?
        return gson.toJson(game);
    };

    public static Route handleJoin = (Request req, Response res) -> {
        String token = req.headers("authorization");
        AuthData auth = new AuthData(token, null);
        //TODO: finish this one once I know if I will be forced to use custom req/res classes.

        res.type("application/json");
        return null;
    };

    public static Route handleClear = (Request req, Response res) -> {
        userService.clear();
        gameService.clear();

        res.type("application/json");
        return "{}";
    };
}
