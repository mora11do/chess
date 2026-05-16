package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DataAccessSQLException;
import io.javalin.http.Context;
import models.ListRequest;
import services.ListService;

import java.util.Map;

public class ListHandler {
    private final ListService listService;

    public ListHandler(ListService listService) {
        this.listService = listService;
    }

    public void list(Context ctx) {
        String authToken = ctx.header("authorization");

        ListRequest listObject = new ListRequest(authToken);
        try{
            var listOfGames = listService.list(listObject);
            ctx.status(200);
            ctx.result(new Gson().toJson(Map.of("games",listOfGames)));
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