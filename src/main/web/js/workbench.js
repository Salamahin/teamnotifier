function Workbench() {
    const that = this;
    this.token = undefined;


    function prehandler(xhttp, handler) {
        if (xhttp.readyState != 4)
            return;

        handler(xhttp);
    }
    
    function environmentPrehandler(xhttp) {
        if(xhttp.readyState != 4)
            return;
        
        if(xhttp.status == 200) {
            that.statusHandler(JSON.parse(xhttp.responseText).environments);
            return;
        }
        
        throw new Error("Failed to get status");
    }

	function historyPrehandler(xhttp) {
		if(xhttp.readyState != 4)
			return;

		if(xhttp.status == 200) {
			that.historyHandler(JSON.parse(xhttp.responseText).actions);
			return;
		}

		throw new Error("Failed to get history");
	}

    Workbench.prototype.newResourceAction = function (resource, description) {
        var xhttp = new XMLHttpRequest();
        xhttp.open("POST", "/teamnotifier/1.0/environment/application/action/" + resource.id, true);
        xhttp.setRequestHeader("ActionDetails", description);
        xhttp.setRequestHeader("Authorization", "Bearer " + that.token);
        xhttp.onreadystatechange = function () {
            prehandler(xhttp, that.interactionHandler);
        };
        xhttp.send();
    };

    Workbench.prototype.newServerAction = function (server, description) {
        var xhttp = new XMLHttpRequest();
        xhttp.open("POST", "/teamnotifier/1.0/environment/server/action/" + server.id, true);
        xhttp.setRequestHeader("ActionDetails", description);
        xhttp.setRequestHeader("Authorization", "Bearer " + that.token);
        xhttp.onreadystatechange = function () {
            prehandler(xhttp, that.interactionHandler);
        };
        xhttp.send();
    };

    Workbench.prototype.subscribe = function (server) {
        var xhttp = new XMLHttpRequest();
        xhttp.open("POST", "/teamnotifier/1.0/environment/server/subscribe/" + server.id, true);
        xhttp.setRequestHeader("Authorization", "Bearer " + that.token);
        xhttp.onreadystatechange = function () {
            prehandler(xhttp, that.interactionHandler);
        };
        xhttp.send();
    };

    Workbench.prototype.unsubscribe = function (server) {
        var xhttp = new XMLHttpRequest();
        xhttp.open("DELETE", "/teamnotifier/1.0/environment/server/subscribe/" + server.id, true);
        xhttp.setRequestHeader("Authorization", "Bearer " + that.token);
        xhttp.onreadystatechange = function () {
            prehandler(xhttp, that.interactionHandler);
        };
        xhttp.send();
    };

    Workbench.prototype.reserve = function (resource) {
        var xhttp = new XMLHttpRequest();
        xhttp.open("POST", "/teamnotifier/1.0/environment/application/reserve/" + resource.id, true);
        xhttp.setRequestHeader("Authorization", "Bearer " + that.token);
        xhttp.onreadystatechange = function () {
            prehandler(xhttp, that.interactionHandler);
        };
        xhttp.send();
    };

    Workbench.prototype.free = function (resource) {
        var xhttp = new XMLHttpRequest();
        xhttp.open("DELETE", "/teamnotifier/1.0/environment/application/reserve/" + resource.id, true);
        xhttp.setRequestHeader("Authorization", "Bearer " + that.token);
        xhttp.onreadystatechange = function () {
            prehandler(xhttp, that.interactionHandler);
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
            historyPrehandler(xhttp);
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
            historyPrehandler(xhttp);
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

Workbench.prototype.interactionHandler = function (xhttp) {
    throw new Error("not bound");
};

Workbench.prototype.historyHandler = function (actions) {
    throw new Error("not bound");
};

Workbench.prototype.statusHandler = function (environments) {
    throw new Error("not bound");
};
