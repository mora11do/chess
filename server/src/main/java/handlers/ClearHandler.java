package handlers;

import io.javalin.http.Context;
import services.ClearService;


public class ClearHandler {
    private final ClearService clearService;

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    public void clear(Context ctx) {
        clearService.clear();
        ctx.status(200);
        ctx.result("{}");
    }
}