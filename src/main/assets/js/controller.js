function include(url) {
    var script = document.createElement('script');
    script.src = url;
    document.getElementsByTagName('head')[0].appendChild(script);
}

include("workbench.js");
include("notifier.js");
include("authenticator.js");
include("storage.js");
include("view.js");

const WORKBENCH = new Workbench();
const NOTIFIER = new Notifier();
const AUTHENTICATOR = new Authenticator();
const STORAGE = new Storage();
const VIEW = new View();

var ENVIRONMENTS;
var CURRENT_SERVER;
var CURRENT_ENVIRONMENT;



function jumpToEnvironment() {
    jumpToAnchor("environment");
}



function onAuthenticationSuccess(login, token) {
    STORAGE.store(login, token);
    jumpToEnvironment();

    NOTIFIER.token = token;
    WORKBENCH.token = token;

    NOTIFIER.connect();Authenticator.prototype.whoAmI = function(token) {
    var xhttp = new XMLHttpRequest();
    xhttp.open("GET", "/teamnotifier/1.0/users/whoami", true);
    xhttp.setRequestHeader("Authorization", "Bearer " + token);
    xhttp.onreadystatechange = function () {
        whoAmHandler(xhttp);
    };
    xhttp.send();
};
    WORKBENCH.status();
}

function onAuthenticationError() {
    var auth_box = document.getElementById("auth_box");
    auth_box.addEventListener("animationend", function() {
      auth_box.classList.remove(className);
    });
    auth_box.className += " invalid";
    document.getElementById("ibox_password").value = "";
}

function removeAllChildren(parent) {
    while (parent.firstChild) {
        parent.removeChild(parent.firstChild);
    }
}

function extractSelectedEnvironment() {
    for (var i = 0; i < ENVIRONMENTS.length; i++) {
        if (environments[i].id == CURRENT_ENVIRONMENT.id)
            return environments[i];
    }
    return undefined;
}

function extractSelectedServer(servers) {
    for (var i = 0; i < servers.length; i++) {
        if (servers[i].id == CURRENT_SERVER.id)
            return servers[i];
    }
    return undefined;
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

    srv.resources.sort(sortFactory('name'));
    srv.resources.forEach(function (resource) {
        resourceFrame.appendChild(newResourceInfoElem(resource))
    });
}

function rebuildNavigation() {
    var navigationElemsList = document.getElementById("navigation-elems");
    removeAllChildren(navigationElemsList);

    ENVIRONMENTS.forEach(function (env) {
        var servers = env.servers;

        servers.forEach(function (srv) {
            var currentName = env.name +" "+ srv.name;
            var btn = newButton(currentName, function () {
                handleCurrentChange(env, srv, currentName);
            });
            navigationElemsList.appendChild(btn);
        });
    });

    if (CURRENT_SERVER && CURRENT_ENVIRONMENT) {
        var env = extractSelectedEnvironment();
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

function onStatus(xhttp) {
    if(xhttp.status != 200)
        return;

    ENVIRONMENTS = JSON.parse(xhttp.responseText).environments;
    rebuildNavigation();
}

AUTHENTICATOR.authenticationSuccessHandler = onAuthenticationSuccess;
AUTHENTICATOR.authenticationErrorHandler = onAuthenticationError;

window.onload = function () {
    VIEW.init();
    NOTIFIER.requestPermissions();
};
