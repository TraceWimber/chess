package handler;

import com.google.gson.Gson;
import model.UserData;
import spark.Route;
import spark.Response;
import spark.Request;

public class Handler {

    private static final Gson gson = new Gson();

    public static Route handleRegister = (Request req, Response res) -> {
        UserData user = new UserData("testUser", "Hello!", "testEmail@byu.edu");

        res.type("application/json");
        return gson.toJson(user);
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
