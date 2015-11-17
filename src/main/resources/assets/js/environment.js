const TOKEN_COOKIE = "userToken";
const USER_COOKIE = "currentUser";

var USER_TOKEN;
var USER_NAME;
var SERVER_SUBSCRIBTION_CHECKBOXES;
var RESOURCE_RESERVE_BUTTONS;
var RESOURCE_HISTORY_BUTTONS;
var RESOURCE_FREE_BUTTONS;

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
        sendAuthRequest();
    };
}

function sendAuthRequest() {
    var username = document.getElementById("ibox.username").value;
    var password = document.getElementById("ibox.password").value;

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
        console.debug(evt.data);
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
    SERVER_SUBSCRIBTION_CHECKBOXES = [];
    RESOURCE_RESERVE_BUTTONS = [];
    RESOURCE_HISTORY_BUTTONS = [];
    RESOURCE_FREE_BUTTONS = [];

    var environments = status.environments;

    var envFrame = document.getElementById("frm.environment");

    var html = "<ul>";
    environments.forEach(function (env) {
        html += "<li>" + environmentToHtml(env) + "</li>";
    });
    html += "</ul>";

    envFrame.innerHTML = html;
    installCallbacks();
}

/** @namespace environment.servers */
function environmentToHtml(environment) {
    var servers = environment.servers;

    var html = environment.name + "<ul>";
    servers.forEach(function (server) {
        html += "<li>" + serverToHtml(server) + "</li>";
    });
    html += "</ul>";

    return html;
}

/** @namespace server.resources */
/** @namespace server.subscribers */
function serverToHtml(server) {
    var resources = server.resources;
    var subscribers = server.subscribers;

    var serverId = "server_" + server.id;

    SERVER_SUBSCRIBTION_CHECKBOXES[serverId] = server.id;

    var html = "";

    html += newCheckbox(serverId, server.name) + "<ul>";

    html += "<li>subscribers<ul>";
    subscribers.forEach(function (subscriber) {
        html += "<li>" + subscriberToHtml(subscriber) + "</li>";
    });
    html += "</ul></li><li>resources<ul>";
    resources.forEach(function (resource) {
        html += "<li>" + resourceToHtml(resource) + "</li>";
    });
    html += "</ul></li></ul>";

    return html;
}


function newButton(id, value) {
    return "<input id=\"" + id + "\" type=\"button\" value=\"" + value + "\"/>";
}

function newCheckbox(id, value) {
    return "<label><input id=\"" + id + "\" type=\"checkbox\"/>" + value + "</label>";
}

/** @namespace resource.occupationInfo */
/** @namespace occupationInfo.occupationTime */
/** @namespace occupationInfo.userName */
function resourceToHtml(resource) {
    var resourceReserveId = "resource_reserve_" + resource.id;
    var resourceFreeId = "resource_free_" + resource.id;
    var resourceHistoryId = "resource_hist_" + resource.id;

    var html = "";

    var occupationInfo = resource.occupationInfo;


    if (!occupationInfo) {
        html += resource.name + newButton(resourceReserveId, "Reserve");
        RESOURCE_RESERVE_BUTTONS[resourceReserveId] = resource.id;
    } else if (occupationInfo.userName == USER_NAME) {
        html += resource.name + newButton(resourceFreeId, "Free");
        RESOURCE_FREE_BUTTONS[resourceFreeId] = resource.id;
    } else {
        html += resource.name + " reserved by " + occupationInfo.userName + " on " + occupationInfo.occupationTime;
    }

    html += newButton(resourceHistoryId, "History");
    RESOURCE_HISTORY_BUTTONS[resourceHistoryId] = resource.id;

    return html;
}

function subscriberToHtml(subscriber) {
    return "<label>" + subscriber.name + "</label>"
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

function installCallbacks() {
    var key;
    for (key in RESOURCE_HISTORY_BUTTONS) {

        if (!RESOURCE_HISTORY_BUTTONS.hasOwnProperty(key))
            continue;

        const histResourceId = RESOURCE_HISTORY_BUTTONS[key];
        const btnHist = document.getElementById(key);

        btnHist.onclick = function () {
            showHist(histResourceId);
        };
    }
    for (key in SERVER_SUBSCRIBTION_CHECKBOXES) {

        if (!SERVER_SUBSCRIBTION_CHECKBOXES.hasOwnProperty(key))
            continue;

        const serverId = SERVER_SUBSCRIBTION_CHECKBOXES[key];
        const cbSubscribe = document.getElementById(key);

        cbSubscribe.onchange = function () {
            subscribe(serverId, cbSubscribe.value);
        };
    }
    for (key in RESOURCE_RESERVE_BUTTONS) {

        if (!RESOURCE_RESERVE_BUTTONS.hasOwnProperty(key))
            continue;

        const resResourceId = RESOURCE_RESERVE_BUTTONS[key];
        const btnReserve = document.getElementById(key);

        btnReserve.onclick = function () {
            reserve(resResourceId);
        };
    }
    for (key in RESOURCE_FREE_BUTTONS) {

        if (!RESOURCE_FREE_BUTTONS.hasOwnProperty(key))
            continue;

        const freeResourceId = RESOURCE_FREE_BUTTONS[key];
        const btnFree = document.getElementById(key);

        btnFree.onclick = function () {
            free(freeResourceId);
        };
    }
}

window.onload = function () {
    authenticate();

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
