function include(src, callback)
{
    var head = document.getElementsByTagName('head')[0];
    var script = document.createElement('script');
    script.type = 'text/javascript';
    script.onload = function () {
        callback();
    };
    script.src = src;
    head.appendChild(script);
}

var WORKBENCH;
var NOTIFIER;
var AUTHENTICATOR;
var STORAGE;
var VIEW;

var ENVIRONMENTS;
var CURRENT_SERVER;
var CURRENT_ENVIRONMENT;

function onAuthenticationSuccess(login, token) {
    STORAGE.store(login, token);
    VIEW.jumpToEnvironment();

    VIEW.login = login;
    NOTIFIER.token = token;
    WORKBENCH.token = token;

    NOTIFIER.connect();
}


function onStatus(xhttp) {
    if(xhttp.status != 200)
        return;

    ENVIRONMENTS = JSON.parse(xhttp.responseText).environments;
    rebuildNavigation();
}

function init() {
    VIEW.init();
    NOTIFIER.requestPermissions();

    if(!STORAGE.token || !STORAGE.login)
        VIEW.jumpToAuthentication();
}

function bind() {
    if(!WORKBENCH || !NOTIFIER || !AUTHENTICATOR || !STORAGE || !VIEW)
        return;

    AUTHENTICATOR.connectionSuccessHandler = onAuthenticationSuccess;

    init();
}

include("js/workbench.js", function () {
    WORKBENCH = new Workbench();
    bind();
});
include("js/notifier.js", function () {
    NOTIFIER = new Notifier();
    bind();
});
include("js/authenticator.js", function () {
    AUTHENTICATOR = new Authenticator();
    bind();
});
include("js/storage.js", function () {
    STORAGE = new Storage();
    bind();
});
include("js/view.js", function () {
    VIEW = new View();
    bind();
});
