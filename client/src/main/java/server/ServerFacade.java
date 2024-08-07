package server;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import model.Games;
import server.BadFacadeRequestException;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public AuthData register(UserData userData) throws BadFacadeRequestException {
        var path = "/user";
        return this.makeRequest("POST", path, userData, AuthData.class);
    }

    public AuthData login(UserData userData) throws BadFacadeRequestException {
        var path = "/session";
        return this.makeRequest("POST", path, userData, AuthData.class);
    }

    //TODO: Do I need to adjust makeRequest because the headers matter when passing authTokens?
    public void logout(String authToken) throws BadFacadeRequestException {
        var path = "/session";
        this.makeRequest("DELETE", path, authToken, null);
    }

    //TODO: This passes auth header
    public ArrayList<GameData> listGames(String authToken) throws BadFacadeRequestException {
        var path = "/game";
        var response = this.makeRequest("GET", path, authToken, Games.class);
        return response.getGames();
    }

    //TODO: This should pass auth header, also may only need to return an int gameID
    public GameData createGame(String authToken, GameData game) throws BadFacadeRequestException {
        var path = "/game";
        return this.makeRequest("POST", path, game, GameData.class);
    }

    //TODO: This also should pass auth header
    public void joinGame(String authToken, GameData gameData) throws BadFacadeRequestException {
        var path = "/game";
        this.makeRequest("PUT", path, gameData, null);
    }

    public void clear() throws BadFacadeRequestException {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws BadFacadeRequestException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new BadFacadeRequestException(500 , ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws Exception {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new BadFacadeRequestException(status, "failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() > 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}

