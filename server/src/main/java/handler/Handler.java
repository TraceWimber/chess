package handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.BadRequestException;
import service.GameService;
import service.UserService;
import spark.Route;
import spark.Response;
import spark.Request;
import java.util.Objects;

public class Handler {

    private static final Gson GSON = new Gson();
    private static final UserService userService = new UserService();
    private static final GameService gameService = new GameService();

    public static Route handleRegister = (Request req, Response res) -> {
        UserData user = GSON.fromJson(req.body(), UserData.class);
        AuthData auth = userService.register(user);

        res.type("application/json");
        return GSON.toJson(auth);
    };

    public static Route handleLogin = (Request req, Response res) -> {
        UserData user = GSON.fromJson(req.body(), UserData.class);
        AuthData auth = userService.login(user);

        res.type("application/json");
        return GSON.toJson(auth);
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

        //See my notes in handleCreate()... uncomment the following 4 lines below to use memory-based db
        //ArrayList<GameData> gamesArray = new ArrayList<>();
        //for (GameData game : gameService.listGames(auth)) {
        //    gamesArray.add(new GameData(game.gameID() + 1, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game()));
        //}
        Games games = new Games(gameService.listGames(auth));

        res.type("application/json");
        return GSON.toJson(games);
    };

    public static Route handleCreate = (Request req, Response res) -> {
        String token = req.headers("authorization");
        AuthData auth = new AuthData(token, null);
        GameData gameName = GSON.fromJson(req.body(), GameData.class);

        GameData game = gameService.createGame(auth, gameName);

        //I had to add this code, because the project specifications for my CS 240 class were unfortunately incomplete.
        //There were a number of output requirements that weren't in the instructions.
        //So, in this case, they made me code the entire thing just to find out that the test cases only accept gameIDs > 0.
        //I had to spend another few hours adding unnecessary code like this to suit very particular output requirements.
        //EDIT: this line below only helps to use the memory-based db. It has no effect on SQL db
        // (I'll rework my memory implementation when I'm no longer pressured by due dates)
        game = new GameData(game.gameID() + 1, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());

        res.type("application/json");
        return GSON.toJson(game);
    };

    public static Route handleJoin = (Request req, Response res) -> {
        String token = req.headers("authorization");
        AuthData auth = new AuthData(token, null);
        GameData game = GSON.fromJson(req.body(), GameData.class);

        //Here is another case of poor instructions on requirements
        //I was given the option to just reuse my User, Auth, and GameData models instead of creating specific request and response classes
        //Little did I know that the test cases would require specific input formats that force strange workarounds like this
        //Here I am extracting a specific key/value pair from the input using GSON's JsonParser.
        JsonObject jsonObj = JsonParser.parseString(req.body()).getAsJsonObject();
        String teamColor = null;
        if (jsonObj.has("playerColor")) {teamColor = jsonObj.get("playerColor").getAsString();}

        if (Objects.equals(teamColor, "WHITE")) {
            game = new GameData(game.gameID(), teamColor, null, null, null);
        } else if (Objects.equals(teamColor, "BLACK")) {
            game = new GameData(game.gameID(), null, teamColor, null, null);
        } else {throw new BadRequestException("Error: Please provide valid team color.");}

        //See my notes in handleCreate()... Add the line below back in to use Memory-based db
        //game = new GameData(game.gameID() - 1, game.whiteUsername(), game.blackUsername(), null, null);
        gameService.joinGame(auth, game);

        res.type("application/json");
        return "{}";
    };

    public static Route handleClear = (Request req, Response res) -> {
        userService.clear();
        gameService.clear();

        res.type("application/json");
        return "{}";
    };
}
