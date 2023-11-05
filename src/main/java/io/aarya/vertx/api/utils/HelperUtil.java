package io.aarya.vertx.api.utils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import io.aarya.vertx.api.entity.SuperHero;

public class HelperUtil {

    private HelperUtil() {
    }

    public static final String SERVICE_ADDRESS = "SUPER_HERO";


    public static Supplier<List<SuperHero>> superHeroesSupplier = () ->
            Arrays.asList(
                    SuperHero.builder().id(1).name("Wade").superName("Deadpool").profession("Street fighter").age(28).canFly(false).build(),
                    SuperHero.builder().id(2).name("Bruce").superName("Hulk").profession("Doctor").age(50).canFly(false).build(),
                    SuperHero.builder().id(3).name("Steve").superName("Captain America").profession("Solder").age(120).canFly(false).build(),
                    SuperHero.builder().id(4).name("Tony").superName("Iron Man").profession("Business man").age(45).canFly(true).build(),
                    SuperHero.builder().id(5).name("Peter").superName("Spider Man").profession("Student").age(21).canFly(true).build()
            );




}