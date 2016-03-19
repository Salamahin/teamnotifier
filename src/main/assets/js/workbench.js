function Workbench() {
    this.token = "";
    this.serverId = -1;

    this.interactionHandler = function (xhttp) {
    };

    this.historyHandler = function (xhttp) {
    };

    this.statusHandler = function (xhttp) {
    };
}

Workbench.prototype.newResourceAction = function (resource, description) {
    var xhttp = new XMLHttpRequest();
    xhttp.open("POST", "/teamnotifier/1.0/environment/application/action/" + resource.id, true);
    xhttp.setRequestHeader("ActionDetails", description);
    xhttp.setRequestHeader("Authorization", "Bearer " + token);
    xhttp.onreadystatechange = function () {
        if (xhttp.readyState != 4)
            return;

        interactionHandler(xhttp);
    };
    xhttp.send();
};

Workbench.prototype.newServerAction = function (description) {
    var xhttp = new XMLHttpRequest();
    xhttp.open("POST", "/teamnotifier/1.0/environment/server/action/" + this.serverId, true);
    xhttp.setRequestHeader("ActionDetails", description);
    xhttp.setRequestHeader("Authorization", "Bearer " + token);
    xhttp.onreadystatechange = function () {
        if (xhttp.readyState != 4)
            return;

        interactionHandler(xhttp);
    };
    xhttp.send();
};

Workbench.prototype.subscribe = function () {
    var xhttp = new XMLHttpRequest();
    xhttp.open("POST", "/teamnotifier/1.0/environment/server/subscribe/" + this.serverId, true);
    xhttp.setRequestHeader("Authorization", "Bearer " + token);
    xhttp.onreadystatechange = function () {
        if (xhttp.readyState != 4)
            return;

        interactionHandler(xhttp);
    };
    xhttp.send();
};

Workbench.prototype.unsubscribe = function () {
    var xhttp = new XMLHttpRequest();
    xhttp.open("DELETE", "/teamnotifier/1.0/environment/server/subscribe/" + this.serverId, true);
    xhttp.setRequestHeader("Authorization", "Bearer " + token);
    xhttp.onreadystatechange = function () {
        if (xhttp.readyState != 4)
            return;

        interactionHandler(xhttp);
    };
    xhttp.send();
};

Workbench.prototype.reserve = function (resource) {
    var xhttp = new XMLHttpRequest();
    xhttp.open("POST", "/teamnotifier/1.0/environment/application/reserve/" + resource.id, true);
    xhttp.setRequestHeader("Authorization", "Bearer " + token);
    xhttp.onreadystatechange = function () {
        if (xhttp.readyState != 4)
            return;

        interactionHandler(xhttp);
    };
    xhttp.send();
};

Workbench.prototype.free = function (resource) {
    var xhttp = new XMLHttpRequest();
    xhttp.open("DELETE", "/teamnotifier/1.0/environment/server/subscribe/" + resource.id, true);
    xhttp.setRequestHeader("Authorization", "Bearer " + token);
    xhttp.onreadystatechange = function () {
        if (xhttp.readyState != 4)
            return;

        interactionHandler(xhttp);
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
    xhttp.setRequestHeader("Authorization", "Bearer " + token);
    xhttp.onreadystatechange = function () {
        if (xhttp.readyState != 4)
            return;

        Workbench.historyHandler(xhttp);
    };
    xhttp.send();
};

Workbench.prototype.getServerActions = function (from, to) {
    var xhttp = new XMLHttpRequest();
    xhttp.open("GET", "/teamnotifier/1.0/environment/server/action/" + this.serverId, true);

    var fromStr = from.toISOString();
    var toStr = to.toISOString();
    xhttp.setRequestHeader("ActionsFrom", btoa(fromStr));
    xhttp.setRequestHeader("ActionsTo", btoa(toStr));
    xhttp.setRequestHeader("Authorization", "Bearer " + token);
    xhttp.onreadystatechange = function () {
        if (xhttp.readyState != 4)
            return;

        this.historyHandler(xhttp);
    };
    xhttp.send();
};

Workbench.prototype.status = function () {
    var xhttp = new XMLHttpRequest();
    xhttp.open("GET", "/teamnotifier/1.0/environment", true);
    xhttp.setRequestHeader("Authorization", "Bearer " + token);
    xhttp.onreadystatechange = function () {
        if (xhttp.readyState != 4)
            return;

        statusHandler(xhttp);
    };
    xhttp.send();
};