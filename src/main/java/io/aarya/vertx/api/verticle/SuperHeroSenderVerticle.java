package io.aarya.vertx.api.verticle;

import io.aarya.vertx.api.entity.SuperHero;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

import static io.aarya.vertx.api.utils.HelperUtil.SERVICE_ADDRESS;

public class SuperHeroSenderVerticle extends AbstractVerticle {

    public static final String EB_SUPERHEROES_ENDPOINT = "/eb-superheroes";
    Router router;

    public SuperHeroSenderVerticle(Router router) {
        this.router = router;
    }

    @Override
    public void start() {

        // GET endpoint for reading all SuperHeroes
        router.get(EB_SUPERHEROES_ENDPOINT + "/:id").handler(rc -> {

            vertx.eventBus().send("GET:" + SERVICE_ADDRESS, Integer.parseInt(rc.request().getParam("id")), reply -> {
                if (reply.succeeded()) {
                    String response = reply.result().body().toString();
                    System.out.println("GET:" + SERVICE_ADDRESS + " response: " + response);
                    rc.response().end(response);
                } else {
                    reply.cause().printStackTrace();
                    rc.fail(reply.cause());
                }
            });
        });

        // POST endpoint for creating new SuperHero
        router.get(EB_SUPERHEROES_ENDPOINT).handler(rc -> {
            vertx.eventBus().send("POST:" + SERVICE_ADDRESS, null, reply -> {
                if (reply.succeeded()) {
                    String response = reply.result().body().toString();
                    System.out.println("POST:" + SERVICE_ADDRESS + " response: " + response);
                    rc.response().end(response);
                } else {
                    reply.cause().printStackTrace();
                    rc.fail(reply.cause());
                }
            });
        });

        // PUT endpoint for updating SuperHero
        router.put(EB_SUPERHEROES_ENDPOINT + "/:id").handler(rc -> {
            String id = rc.request().getParam("id");
            SuperHero superHero = Json.decodeValue(rc.getBodyAsString(), SuperHero.class);
            superHero.setId(Integer.parseInt(id));

            vertx.eventBus().send("PUT:" + SERVICE_ADDRESS, superHero, reply -> {
                if (reply.succeeded()) {
                    String response = reply.result().body().toString();
                    System.out.println("PUT:" + SERVICE_ADDRESS + " response: " + response);
                    rc.response().end(response);
                } else {
                    reply.cause().printStackTrace();
                    rc.fail(reply.cause());
                }
            });
        });

        // DELETE endpoint for deleting items
        router.delete(EB_SUPERHEROES_ENDPOINT + "/:id").handler(rc -> {
            vertx.eventBus().<Void>send("DELETE:" + SERVICE_ADDRESS, Integer.parseInt(rc.request().getParam("id")), reply -> {
                if (reply.succeeded()) {
                    String response = reply.result().body().toString();
                    System.out.println("PUT:" + SERVICE_ADDRESS + " response: " + response);
                    rc.response().end(response);
                } else {
                    reply.cause().printStackTrace();
                    rc.fail(reply.cause());
                }
            });
        });
    }
}