const TOKEN_COOKIE = "userToken";
const USER_COOKIE = "currentUser";

var USER_TOKEN;
var USER_NAME;

function loadCookie(cookieName) {
    var matches = document.cookie.match(new RegExp(
        "(?:^|; )" + cookieName.replace(/([\.$?*|{}\(\)\[\]\\\/\+^])/g, '\\$1') + "=([^;]*)"
    ));
    return matches ? decodeURIComponent(matches[1]) : undefined;
}

function storeCookie(cookieName, cookieValue) {
    document.cookie = cookieName + "=" + cookieValue;
}

function removeCookie(cookieName) {
    var cookieString = cookieName + "=";
    var expiryDate = new Date();
    expiryDate.setTime(expiryDate.getTime() - 86400 * 1000);
    cookieString += ';max-age=0';
    cookieString += ';expires=' + expiryDate.toUTCString();
    document.cookie = cookieString;
}

function authenticate() {
    var authForm = document.getElementById("frm.authentication");

    var loadedToken = loadCookie(TOKEN_COOKIE);
    if (loadedToken != undefined) {
        USER_TOKEN = loadedToken;
        USER_NAME = loadCookie(USER_COOKIE);
        connectStatusSocket();
    }

    var authenticate = document.getElementById("btn.authenticate");
    authenticate.onclick = function () {
        var username = document.getElementById("ibox.username").value;
        var password = document.getElementById("ibox.password").value;
        sendAuthRequest(username, password);
    };
}

function sendAuthRequest(username, password) {
    var xhttp = new XMLHttpRequest();
    xhttp.open("GET", "/teamnotifier/1.0/users/authenticate", true);
    xhttp.setRequestHeader("Authorization", "Basic " + btoa(username + ":" + password));
    xhttp.onreadystatechange = function () {
        handleAuthentication(xhttp, username);
    }
    ;
    xhttp.send();
}

function showAuthenticationResult(success) {
    var authForm = document.getElementById("frm.authentication");

    if (success) {
        console.debug("authentication success");
        authForm.classList.remove("authenticationFailed");
    } else {
        console.debug("authentication failed");
        authForm.classList.add("authenticationFailed");
    }
}

/** @namespace authInfo.token */
function handleAuthentication(XMLHttpRequest, userName) {
    if (XMLHttpRequest.readyState != 4)
        return;

    if (XMLHttpRequest.status == 200) {
        var authInfo = JSON.parse(XMLHttpRequest.responseText);
        USER_TOKEN = authInfo.token;
        USER_NAME = userName;
        storeCookie(TOKEN_COOKIE, USER_TOKEN);
        storeCookie(USER_COOKIE, userName);
        connectStatusSocket();
        return;
    }

    showAuthenticationResult(false);
}

function getSocketUrl() {
    var servUrl = document.URL.replace(/.*?:\/\//g, "");
    return "ws://" + servUrl + "state/?token=" + USER_TOKEN;
}

function connectStatusSocket() {
    var websocket = new WebSocket(getSocketUrl());

    websocket.onopen = function (evt) {
        console.debug(evt.data);
        showAuthenticationResult(true);
        getState();
    };

    websocket.onclose = function (evt) {
        console.debug(evt.data);
    };

    websocket.onmessage = function (evt) {
        console.debug();
        new Notification(evt.data);
        getState();
    };

    websocket.onerror = function () {
        removeCookie(TOKEN_COOKIE);
        removeCookie(USER_COOKIE);
        showAuthenticationResult(false);
    };
}

/** @namespace XMLHttpRequest.responseText */
function handleStatus(XMLHttpRequest) {
    if (XMLHttpRequest.readyState != 4)
        return;

    if (XMLHttpRequest.status == 200) {
        var status = JSON.parse(XMLHttpRequest.responseText);
        showStatus(status);
    }
}

function removeAllChildren(parent) {
    while (parent.firstChild) {
        parent.removeChild(parent.firstChild);
    }
}
/** @namespace status.environments */
function showStatus(status) {
    var environments = status.environments;

    var envList = newUnsignedList();
    environments.forEach(function (env) {
            envList.appendChild(envToListElem(env));
        }
    );

    var envFrame = document.getElementById("frm.environment");
    removeAllChildren(envFrame);
    envFrame.appendChild(envList);
}

/** @namespace environment.servers */
function envToListElem(environment) {
    var servers = environment.servers;

    var serverList = newUnsignedList();
    servers.forEach(function (server) {
            serverList.appendChild(servToListElem(server))
        }
    );

    return decorateWith(newListElement(), newLabel(environment.name), serverList);
}


function subscribersToUnsignedList(subscribers) {
    var listSubscribers = newUnsignedList();

    subscribers.forEach(function (subscriber) {
            listSubscribers.appendChild(subscriberToListElem(subscriber));
        }
    );

    return listSubscribers;
}

function resourcesToUnsignedList(resources) {
    var listResources = newUnsignedList();

    resources.forEach(function (resource) {
            listResources.appendChild(resourceToListElem(resource));
        }
    );

    return listResources;
}

function decorateWith() {
    var parent = arguments[0];
    for (var i = 1; i < arguments.length; i++) {
        parent.appendChild(arguments[i]);
    }
    return parent;
}


/** @namespace server.resources */
/** @namespace server.subscribers */
function servToListElem(server) {
    var subscribers = server.subscribers;
    var resources = server.resources;

    const subscribed = subscribers.indexOf(USER_NAME) >= 0;
    var cbSubscribe = newLabeledCheckbox("Subscription on " + server.name, subscribed, function () {
            subscribed
                ? unsubscribe(server.id)
                : subscribe(server.id);
        }
    );

    var listSubscribersElem = decorateWith(newListElement(), newLabel("subscribers"));
    if (subscribers.length != 0)
        listSubscribersElem.appendChild(subscribersToUnsignedList(subscribers));

    var listResourcesElem = decorateWith(newListElement(), newLabel("resources"), resourcesToUnsignedList(resources));
    var listResourcesAndSubscribers = decorateWith(newUnsignedList(), listSubscribersElem, listResourcesElem);
    var cbSubscribeWrapper = decorateWith(document.createElement("label"), cbSubscribe);

    return decorateWith(newListElement(), cbSubscribeWrapper, listResourcesAndSubscribers);
}

function newLabel(value) {
    var label = document.createElement("label");
    label.appendChild(document.createTextNode(value));
    return label;
}

function newUnsignedList() {
    return document.createElement("ul");
}

function newListElement() {
    return document.createElement("li");
}

function newButton(value, onclick) {
    var button = document.createElement("input");
    button.type = "button";
    button.appendChild(document.createTextNode(value));
    button.onclick = onclick;
    return button;
}

function newLabeledCheckbox(value, checked, onchange) {
    const uniqueId = "id" + Math.random().toString(16).slice(2);

    var checkbox = document.createElement("input");
    checkbox.type = "checkbox";
    checkbox.onchange = onchange;
    checkbox.checked = checked;
    checkbox.className = "cmn-toggle cmn-toggle-round";
    checkbox.id = uniqueId;

    var label = document.createElement("label");
    label.htmlFor = uniqueId;

    var wrapper = document.createElement("div");
    wrapper.appendChild(checkbox);
    wrapper.appendChild(label);
    wrapper.appendChild(document.createTextNode(value));

    return wrapper;
}


/** @namespace resource.occupationInfo */
/** @namespace occupationInfo.occupationTime */

function getHistoryButton(resource) {
    var btnHistory = newButton("", function () {
        console.debug("history of resource " + resource.id);
    });
    btnHistory.className = "round-button history-button";
    return btnHistory;
}

function getActionButton(resource) {
    var btnAction = newButton("", function () {
        sendActionRequest(resource.id);
    });
    btnAction.className = "round-button action-button";
    return btnAction;
}

function getReservationCheckbox(resource, reserved) {
    return newLabeledCheckbox("Reserve " + resource.name, reserved, function () {
            reserved ? free(resource.id) : reserve(resource.id);
        }
    );
}
function decorateOccupationInfo(occupationInfo) {
    return decorateWith(
        document.createElement("div"),
        document.createTextNode("Reserved by " + occupationInfo.userName),
        document.createTextNode(" on " + reformatDate(occupationInfo.occupationTime)));
}
/** @namespace occupationInfo.userName */
function resourceToListElem(resource) {
    var occupationInfo = resource.occupationInfo;

    var btnHistory = getHistoryButton(resource);
    var btnAction = getActionButton(resource);
    var action;

    if (!occupationInfo) {
        action = getReservationCheckbox(resource, false);
    } else if (occupationInfo.userName == USER_NAME) {
        action = getReservationCheckbox(resource, true);

    } else {
        action = decorateOccupationInfo(occupationInfo);
    }

    var div = decorateWith(document.createElement("div"), action, btnAction, btnHistory);
    var label = decorateWith(document.createElement("label"), div);

    return decorateWith(newListElement(), label);
}

function reformatDate(dateStr) {
    var d = new Date(dateStr);
    var curr_date = d.getDate();
    var curr_month = d.getMonth();
    var curr_year = d.getFullYear();
    var curr_hour = d.getHours();
    var curr_min = d.getMinutes();
    var curr_sec = d.getSeconds();

    return curr_hour + ":" + curr_min + ":" + curr_sec + " " +
        curr_date + "-" + curr_month + "-" + curr_year;
}

function subscriberToListElem(subscriber) {
    var listElem = newListElement();
    listElem.appendChild(newLabel(subscriber));
    return listElem;
}

function getState() {
    var xhttp = new XMLHttpRequest();
    xhttp.open("GET", "/teamnotifier/1.0/environment", true);
    xhttp.setRequestHeader("Authorization", "Bearer " + USER_TOKEN);
    xhttp.onreadystatechange = function () {
        handleStatus(xhttp);
    };
    xhttp.send();
}

function handleInteraction(XMLHttpRequest) {
    if (XMLHttpRequest.readyState != 4)
        return;

    if (XMLHttpRequest.status == 204) {
        getState();
        return;
    }

    if (XMLHttpRequest.status == 401) {
        showAuthenticationResult(false);
    }
}

function reserve(resourceId) {
    var xhttp = new XMLHttpRequest();
    xhttp.open("POST", "/teamnotifier/1.0/environment/application/reserve/" + resourceId, true);
    xhttp.setRequestHeader("Authorization", "Bearer " + USER_TOKEN);
    xhttp.onreadystatechange = function () {
        handleInteraction(xhttp);
    };
    xhttp.send();
}

function free(resourceId) {
    var xhttp = new XMLHttpRequest();
    xhttp.open("DELETE", "/teamnotifier/1.0/environment/application/reserve/" + resourceId, true);
    xhttp.setRequestHeader("Authorization", "Bearer " + USER_TOKEN);
    xhttp.onreadystatechange = function () {
        handleInteraction(xhttp);
    };
    xhttp.send();
}

function subscribe(serverId) {
    var xhttp = new XMLHttpRequest();
    xhttp.open("POST", "/teamnotifier/1.0/environment/server/subscribe/" + serverId, true);
    xhttp.setRequestHeader("Authorization", "Bearer " + USER_TOKEN);
    xhttp.onreadystatechange = function () {
        handleInteraction(xhttp);
    };
    xhttp.send();
}

function unsubscribe(serverId) {
    var xhttp = new XMLHttpRequest();
    xhttp.open("DELETE", "/teamnotifier/1.0/environment/server/subscribe/" + serverId, true);
    xhttp.setRequestHeader("Authorization", "Bearer " + USER_TOKEN);
    xhttp.onreadystatechange = function () {
        handleInteraction(xhttp);
    };
    xhttp.send();
}

function newMessage(permission) {
    if (permission != "granted")
        return false;
}

function handleRegistration(XMLHttpRequest, username, password) {
    if (XMLHttpRequest.readyState != 4)
        return;

    if (XMLHttpRequest.status == 204) {
        sendAuthRequest(username, password);
    }
}

function sendRegisterRequest() {
    const username = document.getElementById("ibox.username").value;
    const password = document.getElementById("ibox.password").value;

    var xhttp = new XMLHttpRequest();
    xhttp.open("POST", "/teamnotifier/1.0/users/register", true);
    xhttp.setRequestHeader("Authorization", "Basic " + btoa(username + ":" + password));
    xhttp.onreadystatechange = function () {
        handleRegistration(xhttp, username, password);
    };
    xhttp.send();
}

function sendActionRequest(resourceId) {
    var action = prompt("New action", "deploy");
    if (!action)
        return;
    var xhttp = new XMLHttpRequest();
    xhttp.open("POST", "/teamnotifier/1.0/environment/application/actions/" + resourceId, true);
    xhttp.setRequestHeader("ActionDetails", action);
    xhttp.setRequestHeader("Authorization", "Bearer " + USER_TOKEN);
    xhttp.send();
}

window.onload = function () {
    authenticate();
    document.getElementById("btn.register").onclick = function () {
        sendRegisterRequest();
    };

    Notification.requestPermission(newMessage);
};
