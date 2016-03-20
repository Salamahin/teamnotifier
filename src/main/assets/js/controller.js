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

function onAuthenticationSuccess(login, token) {
    STORAGE.store(login, token);
    VIEW.jumpToEnvironment();

    VIEW.login = login;
    NOTIFIER.token = token;
    WORKBENCH.token = token;

    NOTIFIER.connect();
    VIEW.jumpToEnvironment();
}

function onServerChange(server) {
    WORKBENCH.server = server;
}

function onNotifierConnected() {
    console.log("notifier connected");
    WORKBENCH.status();
}

function onNotifierError(error) {
    console.error("notifier error: " + error)
}

function onNotifierDisconnect() {
    console.log("notifier disconnected");
    VIEW.jumpToAuthentication();
}

function onNotifierEvent(event) {
    console.log("notifier event");
}

function init() {
    VIEW.init();
    NOTIFIER.requestPermissions();

    if(!STORAGE.token || !STORAGE.login)
        VIEW.jumpToAuthentication();
    else
        onAuthenticationSuccess(STORAGE.login, STORAGE.token);
}

function bind() {
    if(!WORKBENCH || !NOTIFIER || !AUTHENTICATOR || !STORAGE || !VIEW)
        return;

    NOTIFIER.connectionSuccessHandler = onNotifierConnected;
    NOTIFIER.connectionCloseHandler = onNotifierDisconnect;
    NOTIFIER.errorHandler = onNotifierError;
    NOTIFIER.eventHandler = onNotifierEvent;

    AUTHENTICATOR.authenticationSuccessHandler = onAuthenticationSuccess;
    AUTHENTICATOR.authenticationErrorHandler = VIEW.showAuthenticationError;
    AUTHENTICATOR.registrationErrorHandler = VIEW.showAuthenticationError;

    VIEW.authenticationAttemptHandler = AUTHENTICATOR.authenticate;
    VIEW.registrationHandler = AUTHENTICATOR.register;
    VIEW.serverSelectionHandler = onServerChange;

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
