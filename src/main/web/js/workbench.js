function Workbench() {
    this.token = undefined;
	const that = this;

	function getAppTokenPrehandler(xhttp) {
		if(xhttp.readyState != 4)
			return;

		if(xhttp.status != 200) {
			that.requestErrorHandler(xhttp.status);
			return;
		}

		var token = JSON.parse(xhttp.responseText).token;
		that.getAppTokenRequestSuccessHandler(token);
	}

    function subscribePrehandler(xhttp, server) {
        if (xhttp.readyState != 4)
            return;

        if(xhttp.status != 200) {
        	that.requestErrorHandler(xhttp.status);
        	return;
        }

        that.subscribeRequestSuccessHandler(JSON.parse(xhttp.responseText));
    }

	function unsubscribePrehandler(xhttp, server) {
        if (xhttp.readyState != 4)
            return;

        if(xhttp.status != 204) {
        	that.requestErrorHandler(xhttp.status);
        	return;
        }

        that.unsubscribeRequestSuccessHandler(server);
    }

    function reservePrehandler(xhttp, resource) {
		if (xhttp.readyState != 4)
            return;

		if(xhttp.status != 204) {
        	that.requestErrorHandler(xhttp.status);
        	return;
        }

		that.reserveRequestSuccessHandler(resource);
    }

	function freePrehandler(xhttp, resource) {
		if (xhttp.readyState != 4)
            return;

		if(xhttp.status != 204) {
        	that.requestErrorHandler(xhttp.status);
        	return;
        }

		that.freeRequestSuccessHandler(resource);
    }

    function serverActionPrehandler(xhttp, server, action) {
    	if (xhttp.readyState != 4)
            return;

		if(xhttp.status != 204) {
        	that.requestErrorHandler(xhttp.status);
        	return;
        }

         that.serverActionRequestSuccessHandler(server, action);
    } 

	function resourceActionPrehandler(xhttp, resource, action) {
    	if (xhttp.readyState != 4)
            return;

		if(xhttp.status != 204) {
        	that.requestErrorHandler(xhttp.status);
        	return;
        }

         that.resourceActionRequestSuccessHandler(resource, action);
    } 
    
    function environmentPrehandler(xhttp) {
        if(xhttp.readyState != 4)
            return;

		if(xhttp.status != 200) {
        	that.requestErrorHandler(xhttp.status);
        	return;
        }

        var environments = JSON.parse(xhttp.responseText).environments;
        sortEnvironments(environments);

        that.statusRequestSuccessHandler(environments);
    }

    function sortEnvironments(environments) {
        sortNamedObject(environments);
        for(var e = 0; e < environments.length; e++) {
            sortNamedObject(environments[e].servers);
            for (var s = 0; s < environments[e].servers.length; s++)
                sortNamedObject(environments[e].servers[s].resources);
        }
    }

    function sortNamedObject(objects) {
        objects.sort(function(a, b){
            if(a.name < b.name) return -1;
            if(a.name > b.name) return 1;
            return 0;
        })
    }

	function serverActionsHistoryPrehandler(xhttp, server) {
		if(xhttp.readyState != 4)
			return;

		if(xhttp.status != 200) {
        	that.requestErrorHandler(xhttp.status);
        	return;
        }

		that.serverActionsHistoryRequestSuccessHandler(server, JSON.parse(xhttp.responseText));
	}

	function resourceActionsHistoryPrehandler(xhttp, resource) {
		if(xhttp.readyState != 4)
			return;

		if(xhttp.status != 200) {
        	that.requestErrorHandler(xhttp.status);
        	return;
        }

		that.resourceActionsHistoryRequestSuccessHandler(resource, JSON.parse(xhttp.responseText));
	}


    Workbench.prototype.newResourceAction = function (resource, description) {
		var encodedDescription = encodeURIComponent(description);
    	
        var xhttp = new XMLHttpRequest();
        xhttp.open("POST", "/teamnotifier/1.0/environment/application/" + + resource.id + "/action?details=" + encodedDescription , true);
        xhttp.setRequestHeader("Authorization", "Bearer " + that.token);
        xhttp.onreadystatechange = function () {
            resourceActionPrehandler(xhttp, resource, description);
        };
        xhttp.send();
    };

    Workbench.prototype.newServerAction = function (server, description) {
		var encodedDescription = encodeURIComponent(description);

        var xhttp = new XMLHttpRequest();
        xhttp.open("POST", "/teamnotifier/1.0/environment/server/" + server.id + "/action?details=" + encodedDescription, true);
        xhttp.setRequestHeader("Authorization", "Bearer " + that.token);
        xhttp.onreadystatechange = function () {
            serverActionPrehandler(xhttp, server, description);
        };
        xhttp.send();
    };

	Workbench.prototype.getAppToken = function() {
		var xhttp = new XMLHttpRequest();
		xhttp.open("GET", "/teamnotifier/1.0/external/token");
        xhttp.setRequestHeader("Authorization", "Bearer " + that.token);
		xhttp.onreadystatechange = function() {
			getAppTokenPrehandler(xhttp);
		};
		xhttp.send();
	};

    Workbench.prototype.subscribe = function (server) {
        var xhttp = new XMLHttpRequest();
        xhttp.open("POST", "/teamnotifier/1.0/environment/server/" + server.id + "/subscribe", true);
        xhttp.setRequestHeader("Authorization", "Bearer " + that.token);
        xhttp.onreadystatechange = function () {
            subscribePrehandler(xhttp);
        };
        xhttp.send();
    };

    Workbench.prototype.unsubscribe = function (server) {
        var xhttp = new XMLHttpRequest();
        xhttp.open("DELETE", "/teamnotifier/1.0/environment/server/" + server.id + "/subscribe", true);
        xhttp.setRequestHeader("Authorization", "Bearer " + that.token);
        xhttp.onreadystatechange = function () {
            unsubscribePrehandler(xhttp, server);
        };
        xhttp.send();
    };

    Workbench.prototype.reserve = function (resource) {
        var xhttp = new XMLHttpRequest();
        xhttp.open("POST", "/teamnotifier/1.0/environment/application/" + resource.id + "/reserve", true);
        xhttp.setRequestHeader("Authorization", "Bearer " + that.token);
        xhttp.onreadystatechange = function () {
            reservePrehandler(xhttp, resource);
        };
        xhttp.send();
    };

    Workbench.prototype.free = function (resource) {
        var xhttp = new XMLHttpRequest();
        xhttp.open("DELETE", "/teamnotifier/1.0/environment/application/" + resource.id + "/reserve", true);
        xhttp.setRequestHeader("Authorization", "Bearer " + that.token);
        xhttp.onreadystatechange = function () {
            freePrehandler(xhttp, resource);
        };
        xhttp.send();
    };

    Workbench.prototype.getResourceActions = function (resource, from, to) {
        var fromStr = from.toISOString();
        var toStr = to.toISOString();

        var xhttp = new XMLHttpRequest();
        xhttp.open("GET", "/teamnotifier/1.0/environment/application/" + resource.id + "/action?from=" + fromStr + "&to=" + toStr, true);
        xhttp.setRequestHeader("Authorization", "Bearer " + that.token);
        xhttp.onreadystatechange = function () {
            resourceActionsHistoryPrehandler(xhttp, resource);
        };
        xhttp.send();
    };

    Workbench.prototype.getServerActions = function (server, from, to) {
        var fromStr = from.toISOString();
        var toStr = to.toISOString();

        var xhttp = new XMLHttpRequest();
        xhttp.open("GET", "/teamnotifier/1.0/environment/server/" + server.id + "/action?from=" + fromStr + "&to=" + toStr, true);
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
}

Workbench.prototype.getAppTokenRequestSuccessHandler = function(token) {
	throw new Error("not binded");
}

Workbench.prototype.requestErrorHandler = function(code) {
	throw new Error("not bound");
};

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

Workbench.prototype.reserveRequestSuccessHandler = function (target) {
    throw new Error("not bound");
};

Workbench.prototype.freeRequestSuccessHandler = function (target) {
    throw new Error("not bound");
};

Workbench.prototype.subscribeRequestSuccessHandler = function (serverInfo) {
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
