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
    };
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

/** @namespace status.environments */
function showStatus(status) {
    var environments = status.environments;

    var envFrame = document.getElementById("frm.environment");

    var envList = newUnsignedList();
    environments.forEach(function (env) {
       envList.appendChild(envToListElem(env));
    });

    while (envFrame.firstChild) {
        envFrame.removeChild(envFrame.firstChild);
    }
    envFrame.appendChild(envList);
}

/** @namespace environment.servers */
function envToListElem(environment) {
    var servers = environment.servers;

    var serverList = newUnsignedList();
    servers.forEach(function (server) {
        serverList.appendChild(servToListElem(server))
    });

    var listElem = newListElement();
    listElem.appendChild(newLabel(environment.name));
    listElem.appendChild(serverList);
    return listElem;
}

/** @namespace server.resources */
/** @namespace server.subscribers */
function servToListElem(server) {
    var subscribers = server.subscribers;
    var listSubscribers = newUnsignedList();

    subscribers.forEach(function (subscriber) {
        listSubscribers.appendChild(subscriberToListElem(subscriber));
    });

    var resources = server.resources;
    var listResources = newUnsignedList();

    resources.forEach(function (resource) {
        listResources.appendChild(resourceToListElem(resource));
    });

    const subscribed = subscribers.indexOf(USER_NAME) >= 0;
    var cbSubscribe = newLabeledCheckbox(server.name, subscribed, function() {
        subscribed
            ? unsubscribe(server.id)
            : subscribe(server.id);
    });

    var listResourcesAndSubscribers = newUnsignedList();

    var listSubscribersElem = newListElement();
    listSubscribersElem.appendChild(newLabel("subscribers"));
    listSubscribersElem.appendChild(listSubscribers);

    var listResourcesElem = newListElement();
    listResourcesElem.appendChild(newLabel("resources`"));
    listResourcesElem.appendChild(listResources);

    listResourcesAndSubscribers.appendChild(listSubscribersElem);
    listResourcesAndSubscribers.appendChild(listResourcesElem);

    var listElem = newListElement();
    listElem.appendChild(cbSubscribe);
    listElem.appendChild(listResourcesAndSubscribers);

    return listElem;
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
    var button = document.createElement("button");
    button.innerHTML = value;
    button.onclick = onclick;
    return button;
}

function newLabeledCheckbox(value, checked, onchange) {
    var checkbox = document.createElement("input");
    checkbox.type = "checkbox";
    checkbox.onchange = onchange;
    checkbox.checked = checked;

    var label = document.createElement("label");
    label.appendChild(checkbox);
    label.appendChild(document.createTextNode(value));

    return label;
}


/** @namespace resource.occupationInfo */
/** @namespace occupationInfo.occupationTime */
/** @namespace occupationInfo.userName */
function resourceToListElem(resource) {
    var occupationInfo = resource.occupationInfo;

    var action;
    var btnHistory = newButton("History", function() {
        console.debug("history of resource " + resource.id);
    });
    var btnAction = newButton("Action", function () {
        sendActionRequest(resource.id);
    });


    if (!occupationInfo) {
        action = newButton("Reserve", function() {
            reserve(resource.id);
        });
    } else if (occupationInfo.userName == USER_NAME) {
       action = newButton("Free", function() {
           free(resource.id);
       });

    } else {
       action = newLabel("Reserved by " + occupationInfo.userName + " on " + occupationInfo.occupationTime);
    }


    var listElem = newListElement();
    listElem.appendChild(newLabel(resource.name));
    listElem.appendChild(action);
    listElem.appendChild(btnHistory);
    listElem.appendChild(btnAction);

    return listElem;
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
    if( permission != "granted" ) return false;
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
    var xhttp = new XMLHttpRequest();
    xhttp.open("POST", "/teamnotifier/1.0/environment/application/actions/" + resourceId, true);
    xhttp.setRequestHeader("ActionDetails", action);
    xhttp.setRequestHeader("Authorization", "Bearer " + USER_TOKEN);
    xhttp.send();
}

window.onload = function () {
    authenticate();
    document.getElementById("btn.register").onclick = function() {
        sendRegisterRequest();
    };

    Notification.requestPermission( newMessage );

//  status = document.getElementById("status");
//  environment = document.getElementById("environment");
//  hist = document.getElementById("hist");
//
//  getStatus();
//
//  var websocket = new WebSocket(wsUri);
//
//  websocket.onopen = function(evt) {
//    console.debug(evt.data);
//  };
//
//  websocket.onclose = function(evt) {
//    console.debug(evt.data);
//  };
//
//  websocket.onmessage = function(evt) {
//    console.debug(evt.data);
//    status.innerHTML = evt.data;
//    getStatus();
//  };
//
//  websocket.onerror = function(evt) {
//    console.error(evt.data)
//  };
//
//
//  var subscribe = document.getElementById("subscribe");
//  var unsubscribe = document.getElementById("unsubscribe");
//  var reserve = document.getElementById("reserve");
//  var free = document.getElementById("free");
//  var input = document.getElementById("input");
//
//  subscribe.onclick = function() {
//    var xhr = new XMLHttpRequest();
//    xhr.withCredentials = true;
//    xhr.open("POST", "/teamnotifier/1.0/environment/server/subscribe/" + input.value, true);
//    xhr.setRequestHeader("Authorization", "Basic " + btoa(user + ":" + pass));
//    xhr.onreadystatechange = function() {reaskStatus(xhr);};
//    xhr.send();
//  }
//
//  unsubscribe.onclick = function() {
//    var xhr = new XMLHttpRequest();
//    xhr.withCredentials = true;
//    xhr.open("DELETE", "/teamnotifier/1.0/environment/server/subscribe/" + input.value, true);
//    xhr.setRequestHeader("Authorization", "Basic " + btoa(user + ":" + pass));
//    xhr.onreadystatechange = function() {reaskStatus(xhr);};
//    xhr.send();
//  }
//
//  reserve.onclick = function() {
//    var xhr = new XMLHttpRequest();
//    xhr.withCredentials = true;
//    xhr.open("POST", "/teamnotifier/1.0/environment/application/reserve/" + input.value, true);
//    xhr.setRequestHeader("Authorization", "Basic " + btoa(user + ":" + pass));
//    xhr.onreadystatechange = function() {reaskStatus(xhr);};
//    xhr.send();
//  }
//
//  free.onclick = function() {
//    var xhr = new XMLHttpRequest();
//    xhr.withCredentials = true;
//    xhr.open("DELETE", "/teamnotifier/1.0/environment/application/reserve/" + input.value, true);
//    xhr.setRequestHeader("Authorization", "Basic " + btoa(user + ":" + pass));
//    xhr.onreadystatechange = function() {reaskStatus(xhr);};
//    xhr.send();
//  }
//
//
//  var action_resource_id = document.getElementById("action_resource_id");
//  var action_descr = document.getElementById("action_descr");
//  var push_action = document.getElementById("push_action");
//
//  push_action.onclick = function() {
//    var xhr = new XMLHttpRequest();
//    xhr.withCredentials = true;
//    xhr.open("POST", "/teamnotifier/1.0/environment/application/actions/" + action_resource_id.value, true);
//    xhr.setRequestHeader("Authorization", "Basic " + btoa(user + ":" + pass));
//    xhr.setRequestHeader("ActionDetails", action_descr.value)
//    xhr.send();
//  }
//
//  var action_hist_from = document.getElementById("action_hist_from");
//  var action_hist_to = document.getElementById("action_hist_to");
//  var get_actions = document.getElementById("get_actions");
//
//  get_actions.onclick = function() {
//    var xhr = new XMLHttpRequest();
//    xhr.withCredentials = true;
//    xhr.open("GET", "/teamnotifier/1.0/environment/application/actions/" + action_resource_id.value, true);
//    xhr.setRequestHeader("Authorization", "Basic " + btoa(user + ":" + pass));
//    xhr.setRequestHeader("ActionsFrom", btoa(action_hist_from.value))
//    xhr.setRequestHeader("ActionsTo", btoa(action_hist_to.value))
//    xhr.onreadystatechange = function() {
//      if (xhr.readyState == 4) {
//        hist.innerHTML=xhr.responseText;
//      }
//    };
//    xhr.send();
//  }
};
