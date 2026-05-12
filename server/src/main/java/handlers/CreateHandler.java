package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import models.CreateRequest;
import services.CreateService;

import java.util.Map;

public class CreateHandler {
    private final CreateService createService;

    public CreateHandler(CreateService createService) {
        this.createService = createService;
    }

    public void create(Context ctx) {
        var body = new Gson().fromJson(ctx.body(), Map.class);
        String authToken = ctx.header("authorization");
        String gameName = (String) body.get("gameName");

        CreateRequest createObject = new CreateRequest(authToken, gameName);
        try{
            int gameID = createService.create(createObject);
            ctx.status(200);
            ctx.result(new Gson().toJson(Map.of("gameID",gameID)));
        }
        catch (DataAccessException e) {
            ctx.status(e.getStatusCode());
            ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));
        }
    }
}