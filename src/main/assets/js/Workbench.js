function Workbench(token) {
    this.USER_TOKEN = token;
}

Workbench.prototype.setCurrentServer(server) {
    this.serverId = server.id;
}

Workbench.prototype.setInteractionHandler(interactionHandler) {
    this.interactionHandler = interactionHandler;
}

Workbench.prototype.setHistoryHandler(historyHandler) {
    this.historyHandler = historyHandler;
}

Workbench.prototype.newResourceAction = function(resource, description) {
    var xhttp = new XMLHttpRequest();
    xhttp.open("POST", "/teamnotifier/1.0/environment/application/action/" + resource.id, true);
    xhttp.setRequestHeader("ActionDetails", action);
    xhttp.setRequestHeader("Authorization", "Bearer " + USER_TOKEN);
    xhttp.onreadystatechange = function () {
       interactionHandler(xhttp);
    };
    xhttp.send();
}

Workbench.prototype.newServerAction = function(description) {
    var xhttp = new XMLHttpRequest();
    xhttp.open("POST", "/teamnotifier/1.0/environment/server/action/" + serverId, true);
    xhttp.setRequestHeader("ActionDetails", action);
    xhttp.setRequestHeader("Authorization", "Bearer " + USER_TOKEN);
    xhttp.onreadystatechange = function () {
       interactionHandler(xhttp);
    };
    xhttp.send();
}

Workbench.prototype.subscribe = function() {
    var xhttp = new XMLHttpRequest();
    xhttp.open("POST", "/teamnotifier/1.0/environment/server/subscribe/" + serverId, true);
    xhttp.setRequestHeader("Authorization", "Bearer " + USER_TOKEN);
    xhttp.onreadystatechange = function () {
      handleInteraction(xhttp);
    };
    xhttp.send();
}

Workbench.prototype.unsubscribe = function() {
    var xhttp = new XMLHttpRequest();
    xhttp.open("DELETE", "/teamnotifier/1.0/environment/server/subscribe/" + serverId, true);
    xhttp.setRequestHeader("Authorization", "Bearer " + USER_TOKEN);
    xhttp.onreadystatechange = function () {
      handleInteraction(xhttp);
    };
    xhttp.send();
}

Workbench.prototype.reserve = function(resource) {
    var xhttp = new XMLHttpRequest();
    xhttp.open("POST", "/teamnotifier/1.0/environment/application/reserve/" + resource.id, true);
    xhttp.setRequestHeader("Authorization", "Bearer " + USER_TOKEN);
    xhttp.onreadystatechange = function () {
      handleInteraction(xhttp);
    };
    xhttp.send();
}

Workbench.prototype.free = function(resource) {
    var xhttp = new XMLHttpRequest();
    xhttp.open("DELETE", "/teamnotifier/1.0/environment/server/subscribe/" +  resource.id, true);
    xhttp.setRequestHeader("Authorization", "Bearer " + USER_TOKEN);
    xhttp.onreadystatechange = function () {
        handleInteraction(xhttp);
    };
    xhttp.send();
}

Workbench.prototype.getResourceActions(resource, fromStr, toStr) {
    var xhttp = new XMLHttpRequest();
    xhttp.open("GET", "/teamnotifier/1.0/environment/application/action/" + resource.id, true);

    var fromStr = from.toISOString();
    var toStr = to.toISOString();
    xhttp.setRequestHeader("ActionsFrom", btoa(fromStr));
    xhttp.setRequestHeader("ActionsTo", btoa(toStr));
    xhttp.setRequestHeader("Authorization", "Bearer " + USER_TOKEN);
    xhttp.onreadystatechange = function () {
        historyHandler(xhttp);
    };
    xhttp.send();
}

Workbench.prototype.getServerActions(fromStr, toStr) {
    var xhttp = new XMLHttpRequest();
    xhttp.open("GET", "/teamnotifier/1.0/environment/server/action/" + serverId, true);

    var fromStr = from.toISOString();
    var toStr = to.toISOString();
    xhttp.setRequestHeader("ActionsFrom", btoa(fromStr));
    xhttp.setRequestHeader("ActionsTo", btoa(toStr));
    xhttp.setRequestHeader("Authorization", "Bearer " + USER_TOKEN);
    xhttp.onreadystatechange = function () {
        historyHandler(xhttp);
    };
    xhttp.send();
}