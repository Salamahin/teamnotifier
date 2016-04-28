function View() {
    const that = this;

    var sideMenuView = undefined;
	var userServiceView = undefined;
	var chatView = undefined;
	var avatarCreator = undefined;

    var currentEnvironments = undefined;

	function jumpTo(anchor){
		window.location.href = "#"+anchor;
	}

    View.prototype.showAuthenticationError = function () {
		userServiceView.showAuthenticationError();
    };

    View.prototype.authenticationMode = function() {
        jumpTo("authentication");
    };

    View.prototype.mainMode = function() {
		jumpTo("workbench");
    };

    View.prototype.updateStatus = function (environments) {
        console.error("not implemented");
    };

	View.prototype.showHistory = function(actions) {
		chatView.showChatMessage(actions);
	};

	View.prototype.setUserServiceView = function(view) {
		userServiceView = view;

		userServiceView.authenticationHandler = function(username, password) {
			that.authenticationAttemptHandler(username, password);
		};

		userServiceView.registrationHandler = function(username, password) {
			that.registrationHandler(username, password);
		};
	}

	function bindAvatarCreator() {
		if(!avatarCreator)
			return;

		if(sideMenuView)
			sideMenuView.avatarCreator = avatarCreator;

		if(chatView)
			chatView.avatarCreator = avatarCreator;
	}

	View.prototype.setAvatarCreator = function(creator) {
		avatarCreator = creator;
		bindAvatarCreator();
	}

	View.prototype.setSideMenuView = function(view) {
        sideMenuView = view;

		bindAvatarCreator();

        sideMenuView.serverSelectionHandler = function(environment, server) {
			chatView.select(server);
        }

        sideMenuView.resourceSelectionHandler = function(environment, server, resource) {
			chatView.select(resource);
        }
    };

	View.prototype.setChatView = function(view) {
		chatView = view;
		bindAvatarCreator();
		chatView.serverActionsHistoryHandler = that.serverActionsHistoryHandler;
		chatView.resourceActionsHistoryHandler = that.resourceActionsHistoryHandler;
		chatView.newResourceActionHandler = that.resourceActionHandler;
		chatView.newServerActionHandler = that.serverActionHandler;
	}
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
