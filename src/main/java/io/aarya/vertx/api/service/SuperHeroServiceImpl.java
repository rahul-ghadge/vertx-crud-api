package io.aarya.vertx.api.service;

import io.aarya.vertx.api.utils.HelperUtil;
import io.aarya.vertx.api.entity.SuperHero;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import java.util.List;
import java.util.Optional;

public class SuperHeroServiceImpl {


    public void list(Handler<AsyncResult<List<SuperHero>>> handler) {
        Future<List<SuperHero>> future = Future.future();
        future.setHandler(handler);

        try {
            List<SuperHero> result = HelperUtil.superHeroesSupplier.get();
            future.complete(result);
        } catch (Throwable ex) {
            future.fail(ex);
        }
    }


    public void getById(int id, Handler<AsyncResult<SuperHero>> handler) {
        Future<SuperHero> future = Future.future();
        future.setHandler(handler);

        try {
            Optional<SuperHero> optionalSuperHero = HelperUtil.superHeroesSupplier.get().stream().filter(hero -> hero.getId() == id).findFirst();
            if (!optionalSuperHero.isPresent()) {
                future.fail("Superhero does not exist.");
                return;
            }

            future.complete(optionalSuperHero.get());
        } catch (Throwable ex) {
            future.fail(ex);
        }
    }

    public void save(SuperHero newSuperHero, Handler<AsyncResult<SuperHero>> handler) {
        Future<SuperHero> future = Future.future();
        future.setHandler(handler);

        try {
            Optional<SuperHero> superHero = HelperUtil.superHeroesSupplier.get().stream().filter(hero -> hero.getId() == newSuperHero.getId()).findFirst();
            if (superHero.isPresent()) {
                future.fail("Superhero already exist.");
                return;
            }

            HelperUtil.superHeroesSupplier.get().add(newSuperHero);
            future.complete();
        } catch (Throwable ex) {
            future.fail(ex);
        }
    }


    public void update(SuperHero superHeroToBeUpdated, Handler<AsyncResult<SuperHero>> handler) {
        Future<SuperHero> future = Future.future();
        future.setHandler(handler);

        try {
            Optional<SuperHero> optionalSuperHero = HelperUtil.superHeroesSupplier.get().stream().filter(hero -> hero.getId() == superHeroToBeUpdated.getId()).findFirst();
            if (!optionalSuperHero.isPresent()) {
                future.fail("Superhero does not exist.");
                return;
            }

            SuperHero superHero = optionalSuperHero.get();
            superHero.setId(superHeroToBeUpdated.getId());
            superHero.setName(superHeroToBeUpdated.getName());
            superHero.setAge(superHeroToBeUpdated.getAge());
            superHero.setSuperName(superHeroToBeUpdated.getSuperName());
            superHero.setCanFly(superHeroToBeUpdated.isCanFly());

            future.complete();
        } catch (Throwable ex) {
            future.fail(ex);
        }
    }


    public void remove(Integer id, Handler<AsyncResult<String>> handler) {
        Future<String> future = Future.future();
        future.setHandler(handler);

        try {
            Optional<SuperHero> optionalSuperHero = HelperUtil.superHeroesSupplier.get().stream().filter(hero -> hero.getId() == id).findFirst();
            if (!optionalSuperHero.isPresent()) {
                future.fail("Superhero does not exist.");
                return;
            }

            HelperUtil.superHeroesSupplier.get().remove(optionalSuperHero.get());

            future.complete("Superhero removed.");
        } catch (Throwable ex) {
            future.fail(ex);
        }
    }
}