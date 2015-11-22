const TOKEN_COOKIE = "userToken";
var USER_TOKEN;
var USER_NAME;
var SELECTED_ENV_NAME;
var SELECTED_SRV_ID;
var CURRENT_STATUS;

Date.prototype.toLocalISOString = function(){
    // ISO 8601
    var d = this
        , pad = function (n){return n<10 ? '0'+n : n}
        , tz = d.getTimezoneOffset() //mins
        , tzs = (tz>0?"-":"+") + pad(parseInt(tz/60));

    if (tz%60 != 0)
        tzs += pad(tz%60);

    if (tz === 0) // Zulu time == UTC
        tzs = 'Z';

    return d.getFullYear()+'-'
        + pad(d.getMonth()+1)+'-'
        + pad(d.getDate())+'T'
        + pad(d.getHours())+':'
        + pad(d.getMinutes())+':'
        + pad(d.getSeconds()) + tzs
};

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

function sendWhoAmIRequest() {
    var xhttp = new XMLHttpRequest();
    xhttp.open("GET", "/teamnotifier/1.0/users/whoami", true);
    xhttp.setRequestHeader("Authorization", "Bearer " + USER_TOKEN);
    xhttp.onreadystatechange = function () {
        handleWhoAmI(xhttp);
    };
    xhttp.send();
}

function authenticate() {
    document.getElementById("btn_authenticate").onclick = function () {
        var username = document.getElementById("ibox_username").value;
        var password = document.getElementById("ibox_password").value;
        sendAuthRequest(username, password);
    };
    document.getElementById("btn_register").onclick = function () {
        sendRegisterRequest();
    };

    var loadedToken = loadCookie(TOKEN_COOKIE);
    if (loadedToken != undefined) {
        USER_TOKEN = loadedToken;
        sendWhoAmIRequest();
    } else {
        handleAuthenticationFailed(false);
    }
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

function handleAuthenticationFailed(success) {
    if (success) {
        console.debug("authentication success");
        jumpToAnchor("environment");
    } else {
        console.debug("authentication failed");
        jumpToAnchor("authentication");
    }
}

function handleWhoAmI(XMLHttpRequest) {
    if (XMLHttpRequest.readyState != 4)
        return;

    if (XMLHttpRequest.status == 200) {
        var userInfo = JSON.parse(XMLHttpRequest.responseText);
        USER_NAME = userInfo.name;
        connectStatusSocket();
        return;
    }

    handleAuthenticationFailed(false);
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
        connectStatusSocket();
        return;
    }

    handleAuthenticationFailed(false);
}

function getSocketUrl() {
    return "ws://" + location.host + "/state/?token=" + USER_TOKEN;
}

function connectStatusSocket() {
    var websocket = new WebSocket(getSocketUrl());

    websocket.onopen = function (evt) {
        console.debug(evt.data);
        handleAuthenticationFailed(true);
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
        handleAuthenticationFailed(false);
    };
}

/** @namespace XMLHttpRequest.responseText */
function handleStatus(XMLHttpRequest) {
    if (XMLHttpRequest.readyState != 4)
        return;

    if (XMLHttpRequest.status == 200) {
        CURRENT_STATUS = JSON.parse(XMLHttpRequest.responseText);
        rebuildNavigation();
    }

    if (XMLHttpRequest.status == 401) {
        handleAuthenticationFailed(false);
    }
}

function removeAllChildren(parent) {
    while (parent.firstChild) {
        parent.removeChild(parent.firstChild);
    }
}


function showCurrentResourcesStatus() {
    if (!SELECTED_ENV_NAME || !SELECTED_SRV_ID)
        return;

    var env = extractSelectedEnvironment(CURRENT_STATUS.environments);
    if (env == undefined)
        return;

    var srv = extractSelectedServer(env.servers);
    if (srv == undefined)
        return;

    var resourceFrame = document.getElementById("resources");
    removeAllChildren(resourceFrame);

    srv.resources.forEach(function (resource) {
        resourceFrame.appendChild(newResourceInfoElem(resource))
    });
}

function rebuildNavigation() {
    var navigationElemsList = document.getElementById("navigation-elems");
    removeAllChildren(navigationElemsList);

    var environments = CURRENT_STATUS.environments;
    environments.forEach(function (env) {
        var servers = env.servers;
        servers.forEach(function (srv) {
            var currentName = srv.name + "" + env.name;
            var btn = newButton(currentName, function () {
                handleCurrentChange(env, srv, currentName);
            });
            navigationElemsList.appendChild(btn);
        });
    });

    if (SELECTED_SRV_ID && SELECTED_ENV_NAME) {
        var env = extractSelectedEnvironment(environments);
        if (env == undefined)
            return;

        var srv = extractSelectedServer(env.servers);
        if (srv == undefined)
            return;

        showCurrentSubscriptionStatus(srv);
        showSubscribers(srv);
        showCurrentResourcesStatus(srv);
    } else {
        navigationElemsList.childNodes.item(0).click();
    }
}

function handleCurrentChange(env, srv, currentName) {
    SELECTED_ENV_NAME = env.name;
    SELECTED_SRV_ID = srv.id;

    showCurrentServerName(currentName);
    showCurrentSubscriptionStatus(srv);
    showSubscribers(srv);
    showCurrentResourcesStatus(srv);
}

function showCurrentServerName(currentName) {
    var currentNameContainer = document.getElementById("server");
    removeAllChildren(currentNameContainer);
    currentNameContainer.appendChild(document.createTextNode(currentName));
}

function showCurrentSubscriptionStatus(srv) {
    var subscriptionInfoContainer = document.getElementById("subscription");
    removeAllChildren(subscriptionInfoContainer);
    const subscribed = isSubscribedOnServer(srv);
    var cbSubscribe = newLabeledCheckbox("subscribe", subscribed, function () {
        subscribed ? unsubscribe(srv.id) : subscribe(srv.id);
    });
    subscriptionInfoContainer.appendChild(cbSubscribe);
}

function showSubscribers(srv) {
    var subscribersContainer = document.getElementById("subscribers");
    removeAllChildren(subscribersContainer);

    var subscribers = srv.subscribers;
    subscribers.forEach(function (s) {
        subscribersContainer.appendChild(decorateWith(document.createElement("li"), document.createTextNode(s)));
    });
}

function isSubscribedOnServer(srv) {
    var subscribers = srv.subscribers;
    for (var i = 0; i < subscribers.length; i++)
        if (subscribers[i] == USER_NAME)
            return true;

    return false;
}

function extractSelectedEnvironment(environments) {
    for (var i = 0; i < environments.length; i++) {
        if (environments[i].name == SELECTED_ENV_NAME)
            return environments[i];
    }
    return undefined;
}

function extractSelectedServer(servers) {
    for (var i = 0; i < servers.length; i++) {
        if (servers[i].id == SELECTED_SRV_ID)
            return servers[i];
    }
    return undefined;
}

function decorateWith() {
    var parent = arguments[0];
    for (var i = 1; i < arguments.length; i++) {
        if (arguments[i] == undefined)
            continue;
        parent.appendChild(arguments[i]);
    }
    return parent;
}

function newLabel(value) {
    var label = document.createElement("label");
    label.appendChild(document.createTextNode(value));
    return label;
}

function newButton(value, onclick) {
    var button = decorateWith(document.createElement("a"), document.createTextNode(value));
    button.onclick = onclick;
    return button;
}

function getUniqueId() {
    return "id" + Math.random().toString(16).slice(2);
}

function newLabeledCheckbox(value, checked, onchange) {
    var uniqueId = getUniqueId();

    var checkbox = document.createElement("input");
    checkbox.type = "checkbox";
    checkbox.onchange = onchange;
    checkbox.checked = checked;
    checkbox.className = "toggle toggle-round";
    checkbox.id = uniqueId;

    var label = document.createElement("label");
    label.htmlFor = uniqueId;

    var element = document.createElement("div");
    element.className = "labeled-checkbox";

    var text = newLabel(value);
    text.className = "toggle_text";
    return decorateWith(element, checkbox, label, text);
}


/** @namespace resource.occupationInfo */
/** @namespace occupationInfo.occupationTime */
function getHistoryButton(resource) {
    var btnHistory = newButton("", function () {
        showActionsHistoryModal(resource.id, resource.name);
    });
    btnHistory.className = "round-button history-button";
    return btnHistory;
}

function getActionButton(resource) {
    var btnAction = newButton("", function () {
        showActionModal(resource.id, resource.name)
    });
    btnAction.className = "round-button action-button";
    return btnAction;
}

function getReservationCheckbox(resource, reserved) {
    return newLabeledCheckbox(resource.name, reserved, function () {
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
function newResourceInfoElem(resource) {
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

    var wrapper = document.createElement("div");
    wrapper.className = "resource";

    return decorateWith(wrapper, action, btnAction, btnHistory);
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
        handleAuthenticationFailed(false);
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
    const username = document.getElementById("ibox_username").value;
    const password = document.getElementById("ibox_password").value;

    var xhttp = new XMLHttpRequest();
    xhttp.open("POST", "/teamnotifier/1.0/users/register", true);
    xhttp.setRequestHeader("Authorization", "Basic " + btoa(username + ":" + password));
    xhttp.onreadystatechange = function () {
        handleRegistration(xhttp, username, password);
    };
    xhttp.send();
}


function showActionModal(resourceId, caption) {
    document.getElementById("btn_deploy").onclick = function () {
        sendActionRequest(resourceId, "deploy");
        jumpToAnchor("environment");
    };

    document.getElementById("btn_polite").onclick = function () {
        sendActionRequest(resourceId, "polite");
        jumpToAnchor("environment");
    };

    document.getElementById("btn_other").onclick = function () {
        var action = document.getElementById("ibox_other").value;
        sendActionRequest(resourceId, action);
        jumpToAnchor("environment");
    };

    document.addEventListener('keyup', function(e) {
        if (e.keyCode == 27) {
            jumpToAnchor("environment");
        }
    });

    var header = document.getElementById("action_header");
    removeAllChildren(header);
    header.appendChild(document.createTextNode(caption));

    var modal = document.querySelector('#actions_modal');
    modal.addEventListener('click', function(e) {
        jumpToAnchor("environment");
    }, false);

    modal.children[0].addEventListener('click', function(e) {
        e.stopPropagation();
    }, false);

    jumpToAnchor("action");
}

function subDays(date, days) {
    var result = new Date(date);
    result.setDate(result.getDate() - days);
    return result;
}

function showActionsHistoryModal(resourceId, caption) {
    document.getElementById("btn_hist_today").onclick = function () {
        var now = new Date();
        sendHistRequest(resourceId, subDays(now, 1), now);
    };

    document.getElementById("btn_hist_week").onclick = function () {
        var now = new Date();
        sendHistRequest(resourceId, subDays(now, 7), now);
    };

    document.getElementById("btn_hist_month").onclick = function () {
        var now = new Date();
        sendHistRequest(resourceId, subDays(now, 30), now);
    };

    document.addEventListener('keyup', function(e) {
        if (e.keyCode == 27) {
            jumpToAnchor("environment");
        }
    });

    var header = document.getElementById("hist_header");
    removeAllChildren(header);
    header.appendChild(document.createTextNode(caption));

    var modal = document.querySelector('#hist_modal');
    modal.addEventListener('click', function () {
        jumpToAnchor("environment");
    }, false);

    modal.children[0].addEventListener('click', function(e) {
        e.stopPropagation();
    }, false);

    jumpToAnchor("history");
}

function sendActionRequest(resourceId, action) {
    var xhttp = new XMLHttpRequest();
    xhttp.open("POST", "/teamnotifier/1.0/environment/application/action/" + resourceId, true);
    xhttp.setRequestHeader("ActionDetails", action);
    xhttp.setRequestHeader("Authorization", "Bearer " + USER_TOKEN);
    xhttp.onreadystatechange = function () {
        handleRegistration(xhttp, username, password);
    };
    xhttp.send();
}

function showActionsInfo(actions) {
    var hist = document.getElementById("ul_hist");
    removeAllChildren(hist);
    actions.forEach(function(action) {
       hist.appendChild(decorateWith(document.createElement("li"), actionInfoToLabel(action)));
    });
}

function handleHistRequest(XMLHttpRequest) {
    if (XMLHttpRequest.readyState != 4)
        return;

    if (XMLHttpRequest.status == 200) {
        var actionsInfo = JSON.parse(XMLHttpRequest.responseText);
        showActionsInfo(actionsInfo.actions);
    }

    if (XMLHttpRequest.status == 401) {
        handleAuthenticationFailed(false);
    }
}

function actionInfoToLabel(info) {
    return newLabel(info.userName + " " + reformatDate(info.timestamp) + " " + info.description)
}

function toLocalIsoString(date) {
    var timeOffset = date.getTimezoneOffset() / 60;
    var localDate = new Date(date.getTime() - timeOffset * 3600 * 1000);
    return localDate.toISOString();
}

function sendHistRequest(resourceId, from, to) {
    var xhttp = new XMLHttpRequest();
    xhttp.open("GET", "/teamnotifier/1.0/environment/application/action/" + resourceId, true);
    var fromStr = toLocalIsoString(from);
    var toStr = toLocalIsoString(to);
    xhttp.setRequestHeader("ActionsFrom", btoa(fromStr));
    xhttp.setRequestHeader("ActionsTo", btoa(toStr));
    xhttp.setRequestHeader("Authorization", "Bearer " + USER_TOKEN);
    xhttp.onreadystatechange = function () {
        handleHistRequest(xhttp);
    };
    xhttp.send();
}

function jumpToAnchor(id) {
    window.location.hash = "#" + id;
}

window.onload = function () {
    Notification.requestPermission(newMessage);
    authenticate();
};