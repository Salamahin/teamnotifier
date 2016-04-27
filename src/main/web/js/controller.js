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
var SIDEMENU_VIEW;
var USER_SERVICE_VIEW;
var AVATAR_NODE_CREATOR;

var ENVIRONMENTS;
var CURRENT_SERVER;

function onStatus(environments) {
	SIDEMENU_VIEW.setEnvironments(environments);
}

function onAuthenticationSuccess(login, token) {
    STORAGE.store(login, token);
    VIEW.mainMode();

	SIDEMENU_VIEW.user = login;

    NOTIFIER.token = token;
    WORKBENCH.token = token;

    NOTIFIER.connect();
    VIEW.mainMode();
}

function onServerChange(server) {
    WORKBENCH.server = server;
}

function onNotifierConnected() {
    console.log("notifier connected");
    WORKBENCH.status();
}

function onInteractionComplete(xhttp) {
    if(xhttp.status != 204)
        throw new Error("Interaction failed: " + xhttp);
    WORKBENCH.status();
}

function onNotifierError(error) {
    console.log("notifier error: " + error)
}

function onNotifierDisconnect() {
    console.log("notifier disconnected");
    VIEW.authenticationMode();
}

function onNotifierEvent(event) {
    console.log("notifier event");
}

function init() {
    NOTIFIER.requestPermissions();

    if(!STORAGE.token || !STORAGE.login)
        VIEW.authenticationMode();
    else
        onAuthenticationSuccess(STORAGE.login, STORAGE.token);
}

function bind() {
    if(!WORKBENCH || !NOTIFIER || !AUTHENTICATOR || !STORAGE || !VIEW || !SIDEMENU_VIEW || !USER_SERVICE_VIEW || !AVATAR_NODE_CREATOR)
        return;

	SIDEMENU_VIEW.avatarCreator = AVATAR_NODE_CREATOR;

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
    VIEW.subscribeHandler = WORKBENCH.subscribe;
    VIEW.unsubscribeHandler = WORKBENCH.unsubscribe;
    VIEW.reserveHandler = WORKBENCH.reserve;
    VIEW.freeHandler = WORKBENCH.free;
	VIEW.serverActionsHistoryHandler = WORKBENCH.getServerActions;
	VIEW.resourceActionsHistoryHandler = WORKBENCH.getResourceActions;
	VIEW.resourceActionHandler = WORKBENCH.newResourceAction;

    VIEW.setSideMenuView(SIDEMENU_VIEW);
	VIEW.setUserServiceView(USER_SERVICE_VIEW);
    
    WORKBENCH.statusHandler = onStatus;
    WORKBENCH.interactionHandler = onInteractionComplete;
	WORKBENCH.historyHandler = VIEW.showHistory;

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
include("js/sidemenuView.js", function () {
    SIDEMENU_VIEW = new SideMenulView();
    bind();
});
include("js/userServiceView.js", function() {
	USER_SERVICE_VIEW = new UserServiceView();
	bind();
});
include("js/avatarNodeCreator.js", function() {
	AVATAR_NODE_CREATOR = new AvatarNodeCreator();
	bind();
});
