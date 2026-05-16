package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DataAccessSQLException;
import io.javalin.http.Context;
import models.JoinRequest;
import services.JoinService;

import java.util.Map;

public class JoinHandler {
    private final JoinService joinService;

    public JoinHandler(JoinService joinService) {
        this.joinService = joinService;
    }

    public void join(Context ctx) {
        var body = new Gson().fromJson(ctx.body(), Map.class);
        String authToken = ctx.header("authorization");
        String playerColor = (String) body.get("playerColor");

        int gameID;
        try {
            int testGameID = ((Number) body.get("gameID")).intValue();
            gameID = testGameID;
        }
        catch (NullPointerException e){
            ctx.status(400);
            ctx.result(new Gson().toJson(Map.of("message", "Error: Please enter a gameID")));
            return;
        }

        JoinRequest joinObject = new JoinRequest(authToken, playerColor,gameID);
        try{
            joinService.join(joinObject);
            ctx.status(200);
            ctx.result("{}");
        }
        catch (DataAccessException e) {
            ctx.status(e.getStatusCode());
            ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));
        }
        catch (DataAccessSQLException e) {
            ctx.status(e.getStatusCode());
            ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));
        }
    }
}