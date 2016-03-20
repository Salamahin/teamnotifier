function Workbench() {
    const that = this;
    this.token = "";
    this.server = undefined;


    function prehandler(xhttp, handler) {
        if (xhttp.readyState != 4)
            return;

        handler(xhttp);
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

    Workbench.prototype.newServerAction = function (description) {
        var xhttp = new XMLHttpRequest();
        xhttp.open("POST", "/teamnotifier/1.0/environment/server/action/" + that.server.id, true);
        xhttp.setRequestHeader("ActionDetails", description);
        xhttp.setRequestHeader("Authorization", "Bearer " + that.token);
        xhttp.onreadystatechange = function () {
            prehandler(xhttp, that.interactionHandler);
        };
        xhttp.send();
    };

    Workbench.prototype.subscribe = function () {
        var xhttp = new XMLHttpRequest();
        xhttp.open("POST", "/teamnotifier/1.0/environment/server/subscribe/" + that.server.id, true);
        xhttp.setRequestHeader("Authorization", "Bearer " + that.token);
        xhttp.onreadystatechange = function () {
            prehandler(xhttp, that.interactionHandler);
        };
        xhttp.send();
    };

    Workbench.prototype.unsubscribe = function () {
        var xhttp = new XMLHttpRequest();
        xhttp.open("DELETE", "/teamnotifier/1.0/environment/server/subscribe/" + that.server.id, true);
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
        xhttp.open("DELETE", "/teamnotifier/1.0/environment/server/subscribe/" + resource.id, true);
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
            prehandler(xhttp, that.historyHandler);
        };
        xhttp.send();
    };

    Workbench.prototype.getServerActions = function (from, to) {
        var xhttp = new XMLHttpRequest();
        xhttp.open("GET", "/teamnotifier/1.0/environment/server/action/" + that.server.id, true);

        var fromStr = from.toISOString();
        var toStr = to.toISOString();
        xhttp.setRequestHeader("ActionsFrom", btoa(fromStr));
        xhttp.setRequestHeader("ActionsTo", btoa(toStr));
        xhttp.setRequestHeader("Authorization", "Bearer " + that.token);
        xhttp.onreadystatechange = function () {
            prehandler(xhttp, that.historyHandler);
        };
        xhttp.send();
    };

    Workbench.prototype.status = function () {
        var xhttp = new XMLHttpRequest();
        xhttp.open("GET", "/teamnotifier/1.0/environment", true);
        xhttp.setRequestHeader("Authorization", "Bearer " + that.token);
        xhttp.onreadystatechange = function () {
            prehandler(xhttp, that.statusHandler);
        };
        xhttp.send();
    };
}

Workbench.prototype.interactionHandler = function (xhttp) {
    throw new Error("not bound");
};

Workbench.prototype.historyHandler = function (xhttp) {
    throw new Error("not bound");
};

Workbench.prototype.statusHandler = function (xhttp) {
    throw new Error("not bound");
};