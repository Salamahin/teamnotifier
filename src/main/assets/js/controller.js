function include(url) {
    var script = document.createElement('script');
    script.src = url;
    document.getElementsByTagName('head')[0].appendChild(script);
}

include("workbench.js");
include("notifier.js");
include("authenticator.js");
include("storage.js");

const WORKBENCH = new Workbench();
const NOTIFIER = new Notifier();
const AUTHENTICATOR = new Authenticator();
const STORAGE = new Storage();

function jumpToAnchor(id) {
    window.location.hash = "#" + id;
}

function jumpToEnvironment() {
    jumpToAnchor("environment");
}

function jumpToEnvironmentOnFocusLost(modal) {
    modal.addEventListener('click', function () {
        jumpToEnvironment();
    }, false);

    modal.children[0].addEventListener('click', function (e) {
        e.stopPropagation();
    }, false);
}

function jumpToEnvironmentOnEsc() {
    document.addEventListener('keyup', function (e) {
        if (e.keyCode == 27) {
            jumpToEnvironment();
        }
    });
}

function onAuthenticationSuccess(login, token) {
    STORAGE.store(login, token);
    jumpToEnvironment();

    WORKBENCH.token = token;
    NOTIFIER.token = token;

    NOTIFIER.connect();
}

function onAuthenticationError() {
    var auth_box = document.getElementById("auth_box");
    auth_box.addEventListener("animationend", function() {
      auth_box.classList.remove(className);
    });
    auth_box.className += " invalid";
    document.getElementById("ibox_password").value = "";
}


AUTHENTICATOR.authenticationSuccessHandler = onAuthenticationSuccess;
AUTHENTICATOR.authenticationErrorHandler = onAuthenticationError;


window.onload = function () {
    jumpToEnvironmentOnEsc();
    jumpToEnvironmentOnFocusLost(document.querySelector("#resource_actions_modal"));
    jumpToEnvironmentOnFocusLost(document.querySelector("#server_actions_modal"));
    jumpToEnvironmentOnFocusLost(document.querySelector("#resource_actions_modal"));
    jumpToEnvironmentOnFocusLost(document.querySelector("#history_modal"));

    notifier.requestPermissions();
};
