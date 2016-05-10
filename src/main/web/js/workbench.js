function Workbench() {
    this.token = undefined;
	const that = this;

    function subscribePrehandler(xhttp, server) {
        if (xhttp.readyState != 4)
            return;

        that.subscribeRequestSuccessHandler(server);
    }

	function unsubscribePrehandler(xhttp, server) {
        if (xhttp.readyState != 4)
            return;

        that.unsubscribeRequestSuccessHandler(server);
    }

    function reservePrehandler(xhttp, resource) {
		if (xhttp.readyState != 4)
            return;

		that.reserveRequestSuccessHandler(resource);
    }

	function freePrehandler(xhttp, resource) {
		if (xhttp.readyState != 4)
            return;

		that.freeRequestSuccessHandler(resource);
    }

    function serverActionPrehandler(xhttp, server, action) {
    	if (xhttp.readyState != 4)
            return;

         that.serverActionRequestSuccessHandler(server, action);
    } 

	function resourceActionPrehandler(xhttp, resource, action) {
    	if (xhttp.readyState != 4)
            return;

         that.resourceActionRequestSuccessHandler(resource, action);
    } 
    
    function environmentPrehandler(xhttp) {
        if(xhttp.readyState != 4)
            return;
        
        if(xhttp.status == 200) {
            that.statusRequestSuccessHandler(JSON.parse(xhttp.responseText).environments);
            return;
        }
        
        throw new Error("Failed to get status");
    }

	function serverActionsHistoryPrehandler(xhttp, server) {
		if(xhttp.readyState != 4)
			return;

		if(xhttp.status == 200) {
			that.serverActionsHistoryRequestSuccessHandler(server, JSON.parse(xhttp.responseText));
			return;
		}

		throw new Error("Failed to get server history");
	}

	function resourceActionsHistoryPrehandler(xhttp, resource) {
		if(xhttp.readyState != 4)
			return;

		if(xhttp.status == 200) {
			that.resourceActionsHistoryRequestSuccessHandler(resource, JSON.parse(xhttp.responseText));
			return;
		}

		throw new Error("Failed to get resource history");
	}

	function whoAmIPrehandler(xhttp) {
		if(xhttp.readyState != 4)
			return;

		if(xhttp.status == 200) {
			that.whoAmISuccessHandler(JSON.parse(xhttp.responseText).name);
			return;
		}

		that.whoAmIErrorHandler();
	}

    Workbench.prototype.newResourceAction = function (resource, description) {
        var xhttp = new XMLHttpRequest();
        xhttp.open("POST", "/teamnotifier/1.0/environment/application/action/" + resource.id, true);
        xhttp.setRequestHeader("ActionDetails", window.btoa(description));
        xhttp.setRequestHeader("Authorization", "Bearer " + that.token);
        xhttp.onreadystatechange = function () {
            resourceActionPrehandler(xhttp, resource, description);
        };
        xhttp.send();
    };

    Workbench.prototype.newServerAction = function (server, description) {
        var xhttp = new XMLHttpRequest();
        xhttp.open("POST", "/teamnotifier/1.0/environment/server/action/" + server.id, true);
        xhttp.setRequestHeader("ActionDetails", window.btoa(description));
        xhttp.setRequestHeader("Authorization", "Bearer " + that.token);
        xhttp.onreadystatechange = function () {
            serverActionPrehandler(xhttp, server, description);
        };
        xhttp.send();
    };

    Workbench.prototype.subscribe = function (server) {
        var xhttp = new XMLHttpRequest();
        xhttp.open("POST", "/teamnotifier/1.0/environment/server/subscribe/" + server.id, true);
        xhttp.setRequestHeader("Authorization", "Bearer " + that.token);
        xhttp.onreadystatechange = function () {
            subscribePrehandler(xhttp, server);
        };
        xhttp.send();
    };

    Workbench.prototype.unsubscribe = function (server) {
        var xhttp = new XMLHttpRequest();
        xhttp.open("DELETE", "/teamnotifier/1.0/environment/server/subscribe/" + server.id, true);
        xhttp.setRequestHeader("Authorization", "Bearer " + that.token);
        xhttp.onreadystatechange = function () {
            unsubscribePrehandler(xhttp, server);
        };
        xhttp.send();
    };

    Workbench.prototype.reserve = function (resource) {
        var xhttp = new XMLHttpRequest();
        xhttp.open("POST", "/teamnotifier/1.0/environment/application/reserve/" + resource.id, true);
        xhttp.setRequestHeader("Authorization", "Bearer " + that.token);
        xhttp.onreadystatechange = function () {
            reservePrehandler(xhttp, resource);
        };
        xhttp.send();
    };

    Workbench.prototype.free = function (resource) {
        var xhttp = new XMLHttpRequest();
        xhttp.open("DELETE", "/teamnotifier/1.0/environment/application/reserve/" + resource.id, true);
        xhttp.setRequestHeader("Authorization", "Bearer " + that.token);
        xhttp.onreadystatechange = function () {
            freePrehandler(xhttp, resource);
        };
        xhttp.send();
    };

    Workbench.prototype.getResourceActions = function (resource, from, to) {
        var xhttp = new XMLHttpRequest();
        xhttp.open("GET", "/teamnotifier/1.0/environment/application/action/" + resource.id, true);

        var fromStr = from.toISOString();
        var toStr = to.toISOString();
        xhttp.setRequestHeader("ActionsFrom", btoa(fromStr));
        xhttp.setRequestHeader("ActionsTo", btoa(toStr));
        xhttp.setRequestHeader("Authorization", "Bearer " + that.token);
        xhttp.onreadystatechange = function () {
            resourceActionsHistoryPrehandler(xhttp, resource);
        };
        xhttp.send();
    };

    Workbench.prototype.getServerActions = function (server, from, to) {
        var xhttp = new XMLHttpRequest();
        xhttp.open("GET", "/teamnotifier/1.0/environment/server/action/" + server.id, true);

        var fromStr = from.toISOString();
        var toStr = to.toISOString();
        xhttp.setRequestHeader("ActionsFrom", btoa(fromStr));
        xhttp.setRequestHeader("ActionsTo", btoa(toStr));
        xhttp.setRequestHeader("Authorization", "Bearer " + that.token);
        xhttp.onreadystatechange = function () {
            serverActionsHistoryPrehandler(xhttp, server);
        };
        xhttp.send();
    };

    Workbench.prototype.status = function () {
        var xhttp = new XMLHttpRequest();
        xhttp.open("GET", "/teamnotifier/1.0/environment", true);
        xhttp.setRequestHeader("Authorization", "Bearer " + that.token);
        xhttp.onreadystatechange = function () {
            environmentPrehandler(xhttp);
        };
        xhttp.send();
    };

    Workbench.prototype.whoAmI = function(token) {
		var xhttp = new XMLHttpRequest();
        xhttp.open("GET", "/teamnotifier/1.0/users/whoami", true);
        xhttp.setRequestHeader("Authorization", "Bearer " + token);
        xhttp.onreadystatechange = function () {
            whoAmIPrehandler(xhttp);
        };
        xhttp.send();
    };
}

Workbench.prototype.whoAmISuccessHandler = function(login) {
	throw new Error("not bound");
};

Workbench.prototype.whoAmIErrorHandler = function() {
	throw new Error("not bound");
};

Workbench.prototype.serverActionRequestSuccessHandler = function(server, description) {
	throw new Error("not bound");	
};

Workbench.prototype.resourceActionRequestSuccessHandler = function(resource, description) {
	throw new Error("not bound");	
};

Workbench.prototype.reserveRequestSuccessHandlerHandler = function (target) {
    throw new Error("not bound");
};

Workbench.prototype.freeRequestSuccessHandler = function (target) {
    throw new Error("not bound");
};

Workbench.prototype.subscribeRequestSuccessHandler = function (target) {
    throw new Error("not bound");
};

Workbench.prototype.unsubscribeRequestSuccessHandler = function (target) {
    throw new Error("not bound");
};

Workbench.prototype.serverActionsHistoryRequestSuccessHandler = function (server, actions) {
    throw new Error("not bound");
};

Workbench.prototype.resourceActionsHistoryRequestSuccessHandler = function (resource, actions) {
    throw new Error("not bound");
};

Workbench.prototype.statusRequestSuccessHandler = function (environments) {
    throw new Error("not bound");
};
