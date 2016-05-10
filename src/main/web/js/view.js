function View() {
    const that = this;

    var sideMenuView = undefined;
	var authenticationView = undefined;
	var chatView = undefined;
	var avatarCreator = undefined;

    var currentEnvironments = undefined;

	function jumpTo(anchor){
		window.location.href = "#"+anchor;
	}

	View.prototype.setCurrentUser = function(username) {
		sideMenuView.user = username;
		chatView.currentUser = username;
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

    View.prototype.updateStatus = function (environments) {
        sideMenuView.setEnvironments(environments);
    };

	View.prototype.showServerActionsHistory = function(server, actions) {
		chatView.showServerActionsHistory(server, actions);
	};

	View.prototype.showResourceActionsHistory = function(resource, actions) {
		chatView.showResourceActionsHistory(resource, actions);
	};

	View.prototype.showServerActionConfirmation = function(server, description) {
		chatView.showServerAction(server, description);
	}

	View.prototype.showResourceActionConfirmation = function(resource, description) {
		chatView.showResourceAction(resource, description);
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
