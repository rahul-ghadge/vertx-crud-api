package io.aarya.vertx.api.verticle;

import io.aarya.vertx.api.entity.SuperHero;
import io.aarya.vertx.api.service.SuperHeroServiceImpl;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

import java.util.HashSet;
import java.util.Set;

public class SuperHeroVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> fut) {
        // CORS support
        Set<String> allowHeaders = new HashSet<>();
        allowHeaders.add("x-requested-with");
        allowHeaders.add("Access-Control-Allow-Origin");
        allowHeaders.add("origin");
        allowHeaders.add("Content-Type");
        allowHeaders.add("accept");

        Set<HttpMethod> allowMethods = new HashSet<>();
        allowMethods.add(HttpMethod.GET);
        allowMethods.add(HttpMethod.POST);
        allowMethods.add(HttpMethod.DELETE);
        allowMethods.add(HttpMethod.PUT);

        Router router = Router.router(vertx);
        router.route().handler(CorsHandler.create("*")
                .allowedHeaders(allowHeaders)
                .allowedMethods(allowMethods));
        router.route().handler(BodyHandler.create()); // <3>

        // routes
        router.get("/superheroes").handler(this::superHeroes);
        router.get("/superheroes/:id").handler(this::getById);
        router.post("/superheroes").handler(this::save);
        router.put("/superheroes").handler(this::update);
        router.delete("/superheroes/:id").handler(this::remove);

        vertx.deployVerticle(new SuperHeroSenderVerticle(router),
                new DeploymentOptions().setWorker(true).setInstances(1));
        vertx.deployVerticle(new SuperHeroConsumerVerticle(),
                new DeploymentOptions().setWorker(true).setInstances(1));

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(8080, result -> {
                    if (result.succeeded())
                        fut.complete();
                    else
                        fut.fail(result.cause());
                });
    }

    SuperHeroServiceImpl superHeroService = new SuperHeroServiceImpl();

    private void superHeroes(RoutingContext context) {
        superHeroService.list(ar -> {
            if (ar.succeeded()) {
                sendSuccess(Json.encodePrettily(ar.result()), context.response());
            } else {
                sendError(ar.cause().getMessage(), context.response());
            }
        });
    }

    private void getById(RoutingContext context) {
        superHeroService.getById(Integer.parseInt(context.request().getParam("id")), ar -> {
            if (ar.succeeded()) {
                if (ar.result() != null) {
                    sendSuccess(Json.encodePrettily(ar.result()), context.response());
                } else {
                    sendSuccess(context.response());
                }
            } else {
                sendError(ar.cause().getMessage(), context.response());
            }
        });
    }

    private void save(RoutingContext context) {
        superHeroService.save(Json.decodeValue(context.getBodyAsString(), SuperHero.class), ar -> {
            if (ar.succeeded()) {
                sendSuccess(context.response());
            } else {
                sendError(ar.cause().getMessage(), context.response());
            }
        });
    }

    private void update(RoutingContext context) {
        superHeroService.update(Json.decodeValue(context.getBodyAsString(), SuperHero.class), ar -> {
            if (ar.succeeded()) {
                sendSuccess(context.response());
            } else {
                sendError(ar.cause().getMessage(), context.response());
            }
        });
    }

    private void remove(RoutingContext context) {
        superHeroService.remove(Integer.parseInt(context.request().getParam("id")), ar -> {
            if (ar.succeeded()) {
                sendSuccess(context.response());
            } else {
                sendError(ar.cause().getMessage(), context.response());
            }
        });
    }

    private void sendError(String errorMessage, HttpServerResponse response) {
        JsonObject jo = new JsonObject();
        jo.put("errorMessage", errorMessage);

        response
                .setStatusCode(500)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(jo));
    }

    private void sendSuccess(HttpServerResponse response) {
        response
                .setStatusCode(200)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end();
    }

    private void sendSuccess(String responseBody, HttpServerResponse response) {
        response
                .setStatusCode(200)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(responseBody);
    }
}
