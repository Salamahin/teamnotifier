function View() {
    const that = this;

    var sideMenuView = undefined;
	var authenticationView = undefined;
	var subscribtionView = undefined;
	var chatView = undefined;
	var avatarCreator = undefined;
	var environmentMonitor = undefined;
	var serverHistMonitor = undefined;
	var resourceHistMonitor = undefined;
	var header = undefined;

	var user;

	function jumpTo(anchor){
		window.location.href = "#"+anchor;
	}

	View.prototype.setCurrentUser = function(username) {
		user = username;
		
		sideMenuView.user = username;
		chatView.currentUser = username;
		subscribtionView.user = username;
		header.setUser(username);
	}

    View.prototype.showAuthenticationError = function () {
		authenticationView.showAuthenticationError();
    };

    View.prototype.authenticationMode = function() {
        jumpTo("authentication");
    };

    View.prototype.mainMode = function() {
		jumpTo("workbench");
    };

	View.prototype.showUnsubscribtionConfirmation = function(server) {
		subscribtionView.unsubcribtionSuccess(server);
	}
	
	View.prototype.showServerActionsHistory = function(server, actions) {
		serverHistMonitor.parseActionsNotification(actions);
	};

	View.prototype.showResourceActionsHistory = function(resource, actions) {
		resourceHistMonitor.parseActionsNotification(actions);
	};

	View.prototype.showServerActionConfirmation = function(server, description) {
		serverHistMonitor.pushConfirmation(server, user, description);
	}

	View.prototype.showResourceActionConfirmation = function(resource, description) {
		resourceHistMonitor.pushConfirmation(resource, user, description);
	}

	View.prototype.showReservationConfirmation = function(resource) {
		subscribtionView.reservationSuccess(resource);
	}

	View.prototype.showFreeConfirmation = function(resource) {
		subscribtionView.freeSuccess(resource);
	}

	View.prototype.setAuthenticationView = function(view) {
		authenticationView = view;

		authenticationView.authenticationHandler = function(username, password) {
			that.authenticationAttemptHandler(username, password);
		};

		authenticationView.registrationHandler = function(username, password) {
			that.registrationHandler(username, password);
		};
	}

	View.prototype.setSubcribtionView = function(view) {
		subscribtionView = view;
		bindAvatarCreator();
		bindEnvironmentMonitor();

		subscribtionView.subscribeHandler = function(server) {
			that.subscribeHandler(server);
		}

		subscribtionView.unsubscribeHandler = function(server) {
			that.unsubscribeHandler(server);
		}

		subscribtionView.reserveHandler = function(resource) {
			that.reserveHandler(resource);
		}

		subscribtionView.freeHandler = function(resource) {
			that.freeHandler(resource);
		}
	}

	function bindAvatarCreator() {
		if(!avatarCreator)
			return;

		if(sideMenuView)
			sideMenuView.avatarCreator = avatarCreator;

		if(chatView)
			chatView.avatarCreator = avatarCreator;

		if(subscribtionView) 
			subscribtionView.avatarCreator = avatarCreator;

		if(header)
			header.avatarCreator = avatarCreator;
	}

	function bindEnvironmentMonitor() {
		if(!environmentMonitor)
			return;

		if(sideMenuView)
			sideMenuView.setEnvironmentMonitor(environmentMonitor);
		
		if(subscribtionView)
			subscribtionView.setEnvironmentMonitor(environmentMonitor);

		if(header)
			header.setEnvironmentMonitor(environmentMonitor);
	}

	View.prototype.handleActionNotification = function(notification) {
		if(notification.type == "ResourceActionNotification")
			resourceHistMonitor.pushNotification(notification);
		else if(notification.type == "ServerActionNotification")
			serverHistMonitor.pushNotification(notification);

		fireActionNotification(notification);
	}

	function getResourceName(resourceId) {
		var res = environmentMonitor.getResource(resourceId);
		var serv = environmentMonitor.getServer(res.serverId);

		return serv.environment.toUpperCase() + " " + serv.name.toUpperCase() + " " + res.name;
	}

	function getServerName(serverId) {
		var serv = environmentMonitor.getServer(serverId);

		return serv.environment.toUpperCase() + " " + serv.name.toUpperCase();
	}

	function fireReservationNotification(notification) {
		var header = "WTF";
		if(notification.state)
			header = "reserve by @" + notification.actor;
		else
			header = "free by @" + notification.actor;

		spawnNotification(header, avatarCreator.getAvatarUrl(notification.actor), getResourceName(notification.targetId));
	}

	function fireActionNotification(notification) {
		var data = notification.type == "ResourceActionNotification"
			? getResourceName(notification.targetId)
			: getServerName(notification.targetId);

		var header = "@" + notification.actor + ": " + notification.description;
		spawnNotification(header, avatarCreator.getAvatarUrl(notification.actor), data);
	}

	View.prototype.handleReservationNotification = function(notification) {
		if(notification.state)
			environmentMonitor.reserve(notification.targetId, notification.actor);
		else
			environmentMonitor.free(notification.targetId, notification.actor);

		fireReservationNotification(notification);
	}

	View.prototype.handleSubscribtionNotification = function(notification) {
		if(notification.state)
			environmentMonitor.addSubscriber(notification.targetId, notification.actor);
		else
			environmentMonitor.removeSubscriber(notification.targetId, notification.actor);
	}

	View.prototype.handleServerStateNotification = function(notification) {
		environmentMonitor.setServerOnline(notification.targetId, notification.state);
	}

	View.prototype.setAvatarCreator = function(creator) {
		avatarCreator = creator;
		bindAvatarCreator();
	}

	View.prototype.setEnvironmentMonitor = function(monitor) {
		environmentMonitor = monitor;
		bindEnvironmentMonitor();
	}

	function spawnNotification(theBody,theIcon,theTitle) {
	  var options = {
		  body: theBody,
		  icon: theIcon
	  }
	  new Notification(theTitle,options);
	}

	View.prototype.setSideMenuView = function(view) {
        sideMenuView = view;

		bindAvatarCreator();

        sideMenuView.serverSelectionHandler = function(server) {
			chatView.select(server);
			subscribtionView.select(server);
			header.select(server);
        }

        sideMenuView.resourceSelectionHandler = function(resource) {
			chatView.select(resource);
			subscribtionView.select(resource);
			header.select(resource);
        }
    };

	View.prototype.setChatView = function(view) {
		chatView = view;
		bindAvatarCreator();
		chatView.newResourceActionHandler = that.resourceActionHandler;
		chatView.newServerActionHandler = that.serverActionHandler;
	}

	View.prototype.setHeader = function(headerView) {
		header = headerView;
		header.requestAppToken = that.requestAppTokenHandler;
		header.resourceSelectedHandler = function(resource) {
			sideMenuView.selectResource(resource);
		}
		bindAvatarCreator();
		bindEnvironmentMonitor();
	}

	View.prototype.setHistoryMonitors = function(serverHistoryMonitor, resourceHistoryMonitor) {
		resourceHistMonitor = resourceHistoryMonitor;
		serverHistMonitor = serverHistoryMonitor;

		serverHistMonitor.getActions = that.serverActionsHistoryHandler;
		resourceHistMonitor.getActions = that.resourceActionsHistoryHandler;
		chatView.setServerHistoryMonitor(serverHistMonitor);
		chatView.setResourceHistoryMonitor(resourceHistMonitor);
	}

	View.prototype.updateEnvironments = function(environments) {
		environmentMonitor.rebuild(environments);
	}

	View.prototype.showAppToken = function(token) {
		window.prompt("Your app token is:", token);
	}
}

View.prototype.requestAppTokenHandler = function() {
	throw new Error("not bound");
}

View.prototype.resourceActionHandler = function(resource, action) {
	throw new Error("not bound");
};

View.prototype.serverActionHandler = function(server, action) {
	throw new Error("not bound");
};

View.prototype.resourceActionsHistoryHandler = function(resource, from, to) {
	throw new Error("not bound");
};

View.prototype.serverActionsHistoryHandler = function(server, from, to) {
	throw new Error("not bound");
};

View.prototype.subscribeHandler = function (server) {
    throw new Error("not bound");
};

View.prototype.unsubscribeHandler = function (server) {
    throw new Error("not bound");
};

View.prototype.reserveHandler = function (resource) {
    throw new Error("not bound");
};

View.prototype.freeHandler = function (resource) {
    throw new Error("not bound");
};

View.prototype.serverSelectionHandler = function (server) {
    throw new Error("not bound");
};

View.prototype.authenticationAttemptHandler = function (login, password) {
    throw new Error("not bound");
};

View.prototype.registrationHandler = function (login, password) {
    throw new Error("not bound");
};
