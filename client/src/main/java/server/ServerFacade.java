package server;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import model.Games;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    /**
     * Registers a new user
     *
     * @param userData UserData containing the new user's credentials and email
     * @return auth data for the new user
     */
    public AuthData register(UserData userData) throws BadFacadeRequestException {
        var path = "/user";
        return this.makeRequest("POST", path, null, userData, AuthData.class);
    }

    /**
     * Logs an existing user in
     *
     * @param userData UserData containing the user's credentials
     * @return auth data for the user
     */
    public AuthData login(UserData userData) throws BadFacadeRequestException {
        var path = "/session";
        return this.makeRequest("POST", path, null, userData, AuthData.class);
    }

    /**
     * Logs out the current user
     *
     * @param authToken Auth token from the client
     */
    public void logout(String authToken) throws BadFacadeRequestException {
        var path = "/session";
        this.makeRequest("DELETE", path, authToken, null, null);
    }

    /**
     * Lists all games in the db
     *
     * @param authToken UserData containing the user's credentials
     * @return list of all the games
     */
    public ArrayList<GameData> listGames(String authToken) throws BadFacadeRequestException {
        var path = "/game";
        var response = this.makeRequest("GET", path, authToken, null, Games.class);
        return response.getGames();
    }

    /**
     * Creates a new game
     *
     * @param authToken UserData containing the user's credentials
     * @return GameData for the new game
     */
    public GameData createGame(String authToken, GameData game) throws BadFacadeRequestException {
        var path = "/game";
        return this.makeRequest("POST", path, authToken, game, GameData.class);
    }

    /**
     * Joins the current user to a game as their chose team
     *
     * @param authToken UserData containing the user's credentials
     */
    public void joinGame(String authToken, GameData gameData) throws BadFacadeRequestException {
        var path = "/game";
        this.makeRequest("PUT", path, authToken, gameData, null);
    }

    /**
     * Clears the db of all data. Used only in development. Delete this method before going to production.
     */
    public void clear() throws BadFacadeRequestException {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null, null);
    }

    // creates a http connection to the server
    private <T> T makeRequest(String method, String path, String header, Object request, Class<T> responseClass) throws BadFacadeRequestException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (header != null) {
                http.addRequestProperty("authorization", header);
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (BadFacadeRequestException ex) {
            throw new BadFacadeRequestException(ex.getStatusCode() , ex.getMessage());
        } catch (Exception ex) {
            throw new BadFacadeRequestException(500 , ex.getMessage());
        }
    }

    // writes the body of the http request
    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    // checks if the given http request was successful or not
    private void throwIfNotSuccessful(HttpURLConnection http) throws Exception {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new BadFacadeRequestException(status, "http failure: " + status);
        }
    }

    // returns the contents of a http response as JSON
    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        try (InputStream respBody = http.getInputStream()) {
            InputStreamReader reader = new InputStreamReader(respBody);
            if (responseClass != null) {
                response = new Gson().fromJson(reader, responseClass);
            }
        }
        return response;
    }

    // checks if the given status code is a successful one
    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}

