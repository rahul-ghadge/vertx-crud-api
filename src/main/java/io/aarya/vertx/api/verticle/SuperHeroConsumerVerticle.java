package io.aarya.vertx.api.verticle;

import io.aarya.vertx.api.entity.SuperHero;
import io.aarya.vertx.api.utils.HelperUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.Optional;

import static io.aarya.vertx.api.utils.HelperUtil.SERVICE_ADDRESS;

public class SuperHeroConsumerVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(SuperHeroConsumerVerticle.class);

    @Override
    public void start() {

        final EventBus eventBus = vertx.eventBus();

        eventBus.consumer("GET:" + SERVICE_ADDRESS, this::getById).exceptionHandler(err -> err.printStackTrace());
        eventBus.consumer("POST:" + SERVICE_ADDRESS, this::superHeroes).exceptionHandler(err -> err.printStackTrace());
        eventBus.consumer("PUT:" + SERVICE_ADDRESS, this::update).exceptionHandler(err -> err.printStackTrace());
        eventBus.consumer("DELETE:" + SERVICE_ADDRESS, this::remove).exceptionHandler(err -> err.printStackTrace());

    }

    public void getById(Message<Integer> message) {
        try {
            int id = message.body();

            Optional<SuperHero> optionalSuperHero = HelperUtil.superHeroesSupplier.get().stream().filter(hero -> hero.getId() == id).findFirst();
            if (!optionalSuperHero.isPresent()) {
                message.fail(404, "Superhero does not exist.");
                return;
            }
            message.reply(Json.encode(optionalSuperHero.get()));
        } catch (Exception e) {
            e.printStackTrace();
            message.fail(500, "Error occured while fetching super hero: " + e);
        }
    }


    public void superHeroes(Message<Void> message) {
        try {
            message.reply(Json.encode(HelperUtil.superHeroesSupplier.get()));
        } catch (Exception e) {
            e.printStackTrace();
            message.fail(500, "Error occured while fetching super heroes data: " + e);
        }
    }

    public void update(Message<Integer> message) {
        try {
            SuperHero superHeroToBeUpdated = Json.decodeValue(message.body().toString(), SuperHero.class);
            Optional<SuperHero> optionalSuperHero = HelperUtil.superHeroesSupplier.get().stream().filter(hero -> hero.getId() == superHeroToBeUpdated.getId()).findFirst();
            if (!optionalSuperHero.isPresent()) {
                message.fail(404, "Superhero does not exist.");
                return;
            }

            SuperHero superHero = optionalSuperHero.get();
            superHero.setId(superHeroToBeUpdated.getId());
            superHero.setName(superHeroToBeUpdated.getName());
            superHero.setAge(superHeroToBeUpdated.getAge());
            superHero.setSuperName(superHeroToBeUpdated.getSuperName());
            superHero.setCanFly(superHeroToBeUpdated.isCanFly());

            message.reply(Json.encode(superHero));
        } catch (Exception e) {
            e.printStackTrace();
            message.fail(500, "Error occured while updating super hero: " + e);
        }
    }

    public void remove(Message<Integer> message) {
        try {
            int id = message.body();

            Optional<SuperHero> optionalSuperHero = HelperUtil.superHeroesSupplier.get().stream().filter(hero -> hero.getId() == id).findFirst();
            if (!optionalSuperHero.isPresent()) {
                message.fail(404, "Superhero does not exist.");
                return;
            }
            HelperUtil.superHeroesSupplier.get().remove(optionalSuperHero.get());

            message.reply("Superhero deleted successfully.!");
        } catch (Exception e) {
            e.printStackTrace();
            message.fail(500, "Error occured while removing super hero: " + e);
        }
    }

}
