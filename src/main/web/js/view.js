function View() {
    const that = this;

    var sideMenuView = undefined;
	var userServiceView = undefined;

    var selectedServer = undefined;
    var selectedResource = undefined;

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
        console.error("not implemented");
    };

    View.prototype.updateStatus = function (environments) {
        console.error("not implemented");
    };

	View.prototype.showHistory = function(actions) {
		console.error("not implemented");
	};

	View.prototype.setUserServiceView= function(view) {
		userServiceView = view;

		userServiceView.authenticationHandler = function(username, password) {
			that.authenticationAttemptHandler(username, password);
		};

		userServiceView.registrationHandler = function(username, password) {
			that.registrationHandler(username, password);
		};
	}

	View.prototype.setSideMenuView = function(view) {
        sideMenuView = view;

        sideMenuView.serverSelectionHandler = function(server) {
            selectedServer = server;
        }

        sideMenuView.resourceSelectionHandler = function(resource) {
            selectedResource = resource;
        }
    };
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
