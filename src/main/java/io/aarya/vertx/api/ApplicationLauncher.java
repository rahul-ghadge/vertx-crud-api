package io.aarya.vertx.api;

import io.aarya.vertx.api.verticle.SuperHeroVerticle;
import io.vertx.core.Launcher;

public class ApplicationLauncher {

	public static void main(String[] args) {

		Launcher.main(new String[] { "run", SuperHeroVerticle.class.getName(),
				"-Dvertx.options.maxWorkerExecuteTime=30000000000000"});
	}

}