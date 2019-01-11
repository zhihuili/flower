package com.ly.flower.center.main;

import com.ly.flower.center.http.AxisHttpServer;

import akka.actor.ActorSystem;

public class Axis {

	ActorSystem system = ActorSystem.create("Axis");

	public static void main(String[] args) throws Exception {
		Axis axis = new Axis();
		AxisHttpServer httpServer = new AxisHttpServer(axis.system);
		httpServer.startHttpServer();
	}

}
