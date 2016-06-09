function EnvironmentMonitor() {

	const servers = new Map();
	const resources = new Map();
	
	const listeners = [];

	function fireServerAdded(environment, server) {
		var e = clone(environment);
		var s = clone(server);
		for(var i = 0; i<listeners.length; i++) {
			var l = listeners[i];
			if(l.onServerAdded)
				l.onServerAdded(e, s);
		}
	}

	function fireResourceAdded(server, resource) {
		s = clone(server);
		r = clone(resource);
		for(var i = 0;i<listeners.length; i++) {
			var l = listeners[i];
			if(l.onResourceAdded) 
				l.onResourceAdded(s, r);
		}
	}

	function fireSubscribersChanged(server) {
		var s = clone(server);

		for(var i = 0; i<listeners.length; i++) {
			var l = listeners[i];
			if(l.onSubscribersChanged)
				l.onSubscribersChanged(s);
		}
	}

	function fireReservationChanged(resource) {
		var r = clone(resource);

		for(var i = 0; i<listeners.length; i++) {
			var l = listeners[i];
			if(l.onReservationChanged)
				l.onReservationChanged(r);
		}
	}

	function fireOnlineStatusChanged(server) {
		var s = clone(server);

		for(var i = 0; i<listeners.length; i++) {
			var l = listeners[i];
			if(l.onOnlineStatusChanged)
				l.onOnlineStatusChanged(s);
		}
	}

	function fireAction(target) {
		var t = clone(target);

		for(var i = 0; i<listeners.length; i++) {
			var l = listeners[i];
			if(l.onAction)
				l.onAction(t);
		}
	}
	
	function arraysHaveSameContent(arr1, arr2) {
		if(arr1.length != arr2.length)
			return false;

		for(var i = 0; i<arr1.length; i++)
			if(arr1[i] != arr2[i])
				return false;

		return true;
	}

	function fireServerChanges(oldServer, newServer) {
		
		if(oldServer.isOnline != newServer.isOnline) {
			fireOnlineStatusChanged(newServer);
			return;
		}

		if(oldServer.subscribers && !newServer.subscribers) {
			fireSubscribersChanged(newServer);
			return;
		}

		if(!oldServer.subscribers && newServer.subscribers) {
			fireSubscribersChanged(newServer);
			return;
		}

		if(!arraysHaveSameContent(oldServer.subscribers, newServer.subscribers))
			fireSubscribersChanged(newServer);
	}

	function fireResourceChanges(oldResource, newResource) {

		if(oldResource.occupationInfo !== newResource.occupationInfo) {
			fireReservationChanged(newResource);
			return;
		}

		if(!newResource.occupationInfo)
			return;

		if(oldResource.occupationInfo.userName != newResource.occupationInfo.userName) {
			fireReservationChanged(newResource);
			return;
		}

		if(oldResource.occupationInfo.occupationTime != newResource.occupationInfo.occupationTime)
			fireReservationChanged(newResource);
	}

	function updateServer(environment, newServer) {
		var oldServer = servers.get(newServer.id);
		servers.set(newServer.id, newServer);

		if(!oldServer) {
			newServer.environment = environment.name;
			fireServerAdded(environment, newServer);
		} else {
			newServer.environment = oldServer.environment;
			fireServerChanges(oldServer, newServer);
		}
	}

	function updateResource(server, newResource) {
		var oldResource = resources.get(newResource.id);
		resources.set(newResource.id, newResource);

		if(!oldResource) {
			newResource.serverId = server.id;
			fireResourceAdded(server, newResource);
		} else  {
			newResource.serverId = oldResource.serverId;
			fireResourceChanges(oldResource, newResource);
		}
	}

	function clone(obj) {
		if (null == obj || "object" != typeof obj) return obj;
		var copy = obj.constructor();
		for (var attr in obj) {
			if (obj.hasOwnProperty(attr)) copy[attr] = obj[attr];
		}
		return copy;
	}

	EnvironmentMonitor.prototype.rebuild = function(actualEnvironments) {
		for(var i = 0; i<actualEnvironments.length; i++) {
			var env = actualEnvironments[i];

			for(var j = 0; j<env.servers.length; j++) {
				var srv = env.servers[j];
				updateServer(env, srv);

				for(var k = 0; k<srv.resources.length; k++)
					updateResource(srv, srv.resources[k]);
			}
		}
	}

	EnvironmentMonitor.prototype.getServer = function(serverId) {
		return clone(servers.get(serverId));
	}

	EnvironmentMonitor.prototype.getResource = function(resourceId) {
		return clone(resources.get(resourceId));
	}
	
	EnvironmentMonitor.prototype.setServerOnline = function(serverId, isOnline) {
		var server = servers.get(serverId);
		fireOnlineStatusChanged(server);
	}

	EnvironmentMonitor.prototype.updateServerInfo = function(server) {
		var newServer = clone(server);
		updateServer(undefined, newServer);
		for(var i = 0; i<newServer.resources.length; i++)
			updateResource(undefined, clone(newServer.resources[i]));
	}

	EnvironmentMonitor.prototype.clearServer = function(serverId) {
		var oldServer = servers.get(serverId);

		var newServer = clone(oldServer);		
		newServer.subscribers = [];

		updateServer(undefined, newServer);
		
		for(var i = 0; i<oldServer.resources.length; i++) {
			var oldResource = oldServer.resources[i];

			var newResource = clone(oldResource);
			delete newResource.occupationInfo;

			updateResource(undefined, newResource);
		}
	}

	EnvironmentMonitor.prototype.addSubscriber = function(serverId, user) {
		var server = servers.get(serverId);
		if(!server.subscribers)
			server.subscribers = [];

		if(server.subscribers.includes(user))
			return;

		server.subscribers.push(user);
		fireSubscribersChanged(server);
	}

	EnvironmentMonitor.prototype.removeSubscriber = function(serverId, user) {
		var server = servers.get(serverId);

		var index = server.subscribers.indexOf(user);
		if(index < 0)
			return;

		server.subscribers.splice(index, 1);
		
		fireSubscribersChanged(server);
	}

	EnvironmentMonitor.prototype.reserve = function(resourceId, user) {
		var resource = resources.get(resourceId);
		if(!resource.occupationInfo)
			resource.occupationInfo = {};
		resource.occupationInfo.occupationTime = new Date().toISOString();
		resource.occupationInfo.userName = user;

		fireReservationChanged(resource);
	}

	EnvironmentMonitor.prototype.free = function(resourceId, user) {
		var resource = resources.get(resourceId);
		resource.occupationInfo = undefined;
		fireReservationChanged(resource);
	}

	EnvironmentMonitor.prototype.addListener = function(listener) {
		if(!listeners.includes(listener))
			listeners.push(listener);		
	}
}
