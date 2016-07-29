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

var CONTENT;
var LOADING_SPINNER;
var WORKBENCH;
var NOTIFIER;
var AUTHENTICATOR;
var VIEW;
var SIDEMENU_VIEW;
var AUTHENTICATION_VIEW;
var AVATAR_NODE_CREATOR;
var CHAT_VIEW;
var SUBSCRIBTION_VIEW;
var ENVIRONMENT_MONITOR;
var RESOURCE_HISTORY_MONITOR;
var SERVER_HISTORY_MONITOR;
var HEADER;

function onRequestError(code) {
	window.alert("Request failed: " + code);
}

function onAuthenticationSuccess(login, token) {
    NOTIFIER.token = token;
    WORKBENCH.token = token;

    NOTIFIER.connect();
    
    VIEW.setCurrentUser(login);
    VIEW.mainMode();
}

function onServerChange(server) {
    WORKBENCH.server = server;
}

function onNotifierConnected() {
    console.log("notifier connected");
    WORKBENCH.status();
}

function onNotifierError(error) {
    console.log("notifier error: " + error.message);
}

function onNotifierDisconnect() {
    console.log("notifier disconnected");
    VIEW.authenticationMode();
}

function authenticationError() {
	VIEW.authenticationMode();
	VIEW.showAuthenticationError();
}

function showError() {
	console.log("not implemented");
}

function parseNotification(notification) {
	switch(notification.type) {
		case "ReservationNotification":
			VIEW.handleReservationNotification(notification);
			break;

		case "ResourceActionNotification":
		case "ServerActionNotification":
			VIEW.handleActionNotification(notification);
			break;

		case "ServerStateNotification":
			VIEW.handleServerStateNotification(notification);
			break;

		case "SubscriptionNotification":
			VIEW.handleSubscribtionNotification(notification);
			break;

		default:
			throw new Error("Unknown notification type: " + notification.type);
	}
}

function parseServerInfo(serverInfo) {
	ENVIRONMENT_MONITOR.updateServerInfo(serverInfo);
}


function init() {
    NOTIFIER.requestPermissions();

	VIEW.authenticationMode();
	loadingComplete();
}

function bind() {
    if(!WORKBENCH ||
		!NOTIFIER || 
		!AUTHENTICATOR || 
		!VIEW || 
		!SIDEMENU_VIEW || 
		!AUTHENTICATION_VIEW || 
		!AVATAR_NODE_CREATOR || 
		!CHAT_VIEW ||
		!SUBSCRIBTION_VIEW ||
		!ENVIRONMENT_MONITOR ||
		!SERVER_HISTORY_MONITOR ||
		!RESOURCE_HISTORY_MONITOR ||
		!HEADER)
        return;

    NOTIFIER.connectionSuccessHandler = onNotifierConnected;
    NOTIFIER.connectionCloseHandler = onNotifierDisconnect;
    NOTIFIER.errorHandler = onNotifierError;
    NOTIFIER.eventHandler = parseNotification;

    AUTHENTICATOR.authenticationSuccessHandler = onAuthenticationSuccess;
    AUTHENTICATOR.authenticationErrorHandler = authenticationError;
    AUTHENTICATOR.registrationErrorHandler = authenticationError;

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
	VIEW.serverActionHandler = WORKBENCH.newServerAction;
	VIEW.subscribeHandler = WORKBENCH.subscribe;
	VIEW.unsubscribeHandler = WORKBENCH.unsubscribe;
	VIEW.reserveHandler = WORKBENCH.reserve;
	VIEW.freeHandler = WORKBENCH.free;
	VIEW.requestAppTokenHandler = WORKBENCH.getAppToken;

    VIEW.setSideMenuView(SIDEMENU_VIEW);
	VIEW.setAuthenticationView(AUTHENTICATION_VIEW);
	VIEW.setChatView(CHAT_VIEW);
	VIEW.setSubcribtionView(SUBSCRIBTION_VIEW);
	VIEW.setAvatarCreator(AVATAR_NODE_CREATOR);
	VIEW.setEnvironmentMonitor(ENVIRONMENT_MONITOR);
	VIEW.setHistoryMonitors(SERVER_HISTORY_MONITOR, RESOURCE_HISTORY_MONITOR);
	VIEW.setHeader(HEADER);

 	WORKBENCH.reserveRequestSuccessHandler = VIEW.showReservationConfirmation;
 	WORKBENCH.freeRequestSuccessHandler = VIEW.showFreeConfirmation;
 	WORKBENCH.subscribeRequestSuccessHandler = parseServerInfo;
 	WORKBENCH.unsubscribeRequestSuccessHandler = VIEW.showUnsubscribtionConfirmation;
	WORKBENCH.serverActionRequestSuccessHandler = VIEW.showServerActionConfirmation;
	WORKBENCH.resourceActionRequestSuccessHandler = VIEW.showResourceActionConfirmation
	WORKBENCH.serverActionsHistoryRequestSuccessHandler = VIEW.showServerActionsHistory;
	WORKBENCH.resourceActionsHistoryRequestSuccessHandler = VIEW.showResourceActionsHistory;
	WORKBENCH.statusRequestSuccessHandler = VIEW.updateEnvironments;
	WORKBENCH.whoAmIErrorHandler = showError;
	WORKBENCH.requestErrorHandler = onRequestError;
	WORKBENCH.getAppTokenRequestSuccessHandler = VIEW.showAppToken;

    init();
}


function hide(node) {
	node.style.visibility = "hidden";
}

function show(node) {
	node.style.visibility = "visible";
}

function loadingStart() {
	hide(CONTENT);
	show(LOADING_SPINNER);
}

function loadingComplete() {
	hide(LOADING_SPINNER);
	show(CONTENT);
}

window.onload = function() {
	CONTENT = document.getElementById("content");
	LOADING_SPINNER = document.getElementById("loading_spinner");

	loadingStart();

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
	include("js/view.js", function () {
		VIEW = new View();
		bind();
	});
	include("js/sidemenuView.js", function () {
		SIDEMENU_VIEW = new SideMenuView();
		bind();
	});
	include("js/authenticationView.js", function() {
		AUTHENTICATION_VIEW = new AuthenticationView();
		bind();
	});
	include("js/avatarNodeCreator.js", function() {
		AVATAR_NODE_CREATOR = new AvatarNodeCreator();
		bind();
	});
	include("js/chatView.js", function() {
		CHAT_VIEW = new ChatView();
		bind();
	});
	include("js/subscribtionView.js", function() {
		SUBSCRIBTION_VIEW = new SubscribtionView();
		bind();
	});
	include("js/environmentMonitor.js", function() {
		ENVIRONMENT_MONITOR = new EnvironmentMonitor();
		bind();
	});
	include("js/historyMonitor.js", function() {
		RESOURCE_HISTORY_MONITOR = new HistoryMonitor();
		SERVER_HISTORY_MONITOR = new HistoryMonitor();
		bind();
	});
	include("js/header.js", function() {
		HEADER = new Header();
		bind();
	});
};

