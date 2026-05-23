package client;

import com.google.gson.Gson;
import models.*;

import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.Map;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(int port) {
        serverUrl = "http://localhost:" + port;
    }

    public Auth register(User user) throws ResponseException {
        var request = buildRequest("POST", "/user", user,null);
        var response = sendRequest(request);
        return handleResponse(response, Auth.class);
    }

    public Auth login(User user) throws ResponseException {
        var request = buildRequest("POST", "/session", user,null);
        var response = sendRequest(request);
        return handleResponse(response,Auth.class);
    }

    public void logout(Auth auth) throws ResponseException {
        var request = buildRequest("DELETE", "/session", null, auth);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public ArrayList<GameWithNoChessGame> list(Auth auth) throws ResponseException {
        var request = buildRequest("GET", "/game", null, auth);
        var response = sendRequest(request);
        return handleResponse(response,ArrayList.class);
    }

    public int create(Auth auth, String gameName) throws ResponseException {
        var request = buildRequest("POST", "/game", Map.of("gameName", gameName), auth);
        var response = sendRequest(request);
        return handleResponse(response, CreateGameResponse.class).gameID();
    }

    public void join(Auth auth, int gameID, String playerColor) throws ResponseException {
        var body = Map.of("gameID", gameID, "playerColor", playerColor);
        var request = buildRequest("PUT", "/game", body, auth);
        var response = sendRequest(request);
        handleResponse(response,null);
    }


    public void clear() throws ResponseException {
        var request = buildRequest("DELETE", "/db", null,null);
        sendRequest(request);
    }

    private HttpRequest buildRequest(String method, String path, Object body, Auth auth) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }

        if (auth != null){
            request.setHeader("Authorization", auth.authToken());
        }
        return request.build();
    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                System.out.println("Error body: " + body);
                throw ResponseException.fromJson(body);
            }

            throw new ResponseException(ResponseException.fromHttpStatusCode(status), "other failure: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}