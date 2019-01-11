package com.ly.flower.center.http;

import com.ly.flower.center.http.ServiceRegistryMessages.ShowServices;

import akka.actor.AbstractActor;
import akka.actor.Props;

public class ServiceRegistryActor extends AbstractActor {

	static Props props() {
		return Props.create(ServiceRegistryActor.class);
	}

	public static class ServiceInfo {
		String serviceName;

		public ServiceInfo(String serviceName) {
			this.serviceName = serviceName;
		}

		public String getServiceName() {
			return serviceName;
		}

	}

	@Override
	public Receive createReceive() {
		return receiveBuilder().match(ShowServices.class, ss -> getSender().tell(new ServiceInfo("hi"), getSelf()))
				.build();
	}
}