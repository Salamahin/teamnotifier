function EnvironmentMonitor() {

	const servers = [];
	const resources = [];

	var environments;

	const listeners = [];


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
		if(!oldServer) {
			fireOnlineStatusChanged(newServer);
			fireSubscribersChanged(newServer);
			return;
		}

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
		if(!oldResource) {
			fireReservationChanged(newResource);
			return;
		}

		if(oldResource.occupationInfo != newResource.occupationInfo) {
			fireReservationChanged(newResource);
			return;
		}

		if(oldResource.occupationInfo.userName != newResource.occupationInfo.userName) {
			fireReservationChanged(newResource);
			return;
		}

		if(oldResource.occupationInfo.occupationTime != newResource.occupationInfo.occupationTime)
			fireReservationChanged(newResource);
	}

	function updateServer(newServer) {
		var oldServer = servers[newServer.id];
		servers[newServer.id] = newServer;

		fireServerChanges(oldServer, newServer);
	}

	function updateResource(newResource) {
		var oldResource = resources[newResource.id];
		resources[newResource.id] = newResource;

		fireResourceChanges(oldResource, newResource);
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
		environments = [];
		
		for(var i = 0; i<actualEnvironments.length; i++) {
			var env = actualEnvironments[i];
			environments[i] = clone(env);
		

			for(var j = 0; j<env.servers.length; j++) {
				var srv = env.servers[j];
				updateServer(srv);

				for(var k = 0; k<srv.resources.length; k++)
					updateResource(srv.resources[k]);
			}
		}
	}

	EnvironmentMonitor.prototype.getServer = function(serverId) {
		return clone(servers[serverId]);
	}

	EnvironmentMonitor.prototype.getResource = function(resourceId) {
		return clone(resources[resourceId]);
	}
	
	EnvironmentMonitor.prototype.getEnvironments = function() {
		return environments;
	}


	EnvironmentMonitor.prototype.setServerOnline = function(serverId, isOnline) {
		var server = servers[serverId];
		fireOnlineStatusChanged(server);
	}

	EnvironmentMonitor.prototype.addSubscriber = function(serverId, user) {
		var server = servers[serverId];
		if(!server.subscribers)
			server.subscribers = [];
		server.subscribers.push(user);
		fireSubscribersChanged(server);
	}

	EnvironmentMonitor.prototype.removeSubscriber = function(serverId, user) {
		var server = servers[serverId];

		var index = server.subscribers.indexOf(user);
		server.subscribers.splice(index, 1);
		
		fireSubscribersChanged(server);
	}

	EnvironmentMonitor.prototype.reserve = function(resourceId, user) {
		var resource = resources[resourceId];
		if(!resource.occupationInfo)
			resource.occupationInfo = {};
		resource.occupationInfo.occupationTime = new Date().toISOString();
		resource.occupationInfo.userName = user;

		fireReservationChanged(resource);
	}

	EnvironmentMonitor.prototype.free = function(resourceId, user) {
		var resource = resources[resourceId];
		resource.occupationInfo = undefined;
		fireReservationChanged(resource);
	}

	EnvironmentMonitor.prototype.addListener = function(listener) {
		listeners.push(listener);		
	}
}
