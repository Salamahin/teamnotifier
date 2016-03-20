function View() {
    const that = this;

    this.login = undefined;
    var password;

    var server = undefined;

    function jumpToAnchor(id) {
        window.location.hash = "#" + id;
    }

    this.jumpToEnvironment = function () {
        jumpToAnchor("environment");
    };

    this.jumpToHistory = function () {
        jumpToAnchor("history");
    };

    this.jumpToServerActions = function () {
        jumpToAnchor("server_actions");
    };

    this.jumpToResourceActions = function () {
        jumpToAnchor("resource_actions");
    };
    
    this.jumpToAuthentication = function () {
        jumpToAnchor("authentication");  
    };

    this.login = "";

    function jumpToEnvironmentOnFocusLost(modal) {
        modal.addEventListener('click', function () {
            View.prototype.jumpToEnvironment();
        }, false);

        modal.children[0].addEventListener('click', function (e) {
            e.stopPropagation();
        }, false);
    }

    function jumpToEnvironmentOnEsc() {
        document.addEventListener('keyup', function (e) {
            if (e.keyCode == 27) {
                View.prototype.jumpToEnvironment();
            }
        });
    }

    function getInsertedData() {
        login = document.getElementById("ibox_username").value;
        password = document.getElementById("ibox_password").value;
    }

    function authenticationAttempt() {
        getInsertedData();
        that.authenticationAttemptHandler(login, password);
        password = "";
    }

    function enterIsPressed(e) {
        return e.keyCode == 13;
    }

    function sendAuthenticationAttemptOnEnter() {
        document.getElementById("ibox_password").onkeydown=function(e) {
            if(enterIsPressed(e))
                authenticationAttempt();
        };

        document.getElementById("ibox_username").onkeydown=function(e) {
            if(enterIsPressed(e))
                authenticationAttempt();
        };
    }

    this.init = function () {
        jumpToEnvironmentOnEsc();
        jumpToEnvironmentOnFocusLost(document.querySelector("#resource_actions_modal"));
        jumpToEnvironmentOnFocusLost(document.querySelector("#server_actions_modal"));
        jumpToEnvironmentOnFocusLost(document.querySelector("#history_modal"));

        sendAuthenticationAttemptOnEnter();


        document.getElementById("btn_authenticate").onclick = function () {
            authenticationAttempt();
        };
        document.getElementById("btn_register").onclick = function () {
            getInsertedData();
            that.registrationHandler(login, password);
            password = "";
        };

    };

    function removeAllChildren (parent) {
        while (parent.firstChild) {
            parent.removeChild(parent.firstChild);
        }
    }
    
    View.prototype.showAuthenticationError = function () {
        var auth_box = document.getElementById("auth_box");
        auth_box.addEventListener("animationend", function() {
            auth_box.classList.remove("invalid");
        });
        auth_box.className += " invalid";
        document.getElementById("ibox_password").value = "";
    };

    function decorateWith() {
        var parent = arguments[0];
        for (var i = 1; i < arguments.length; i++) {
            if (arguments[i] == undefined)
                continue;
            parent.appendChild(arguments[i]);
        }
        return parent;
    }

    function newButton(value, onclick) {
        var button = decorateWith(document.createElement("a"), document.createTextNode(value));
        button.onclick = onclick;
        return button;
    }

    function showCurrentServerName() {
        var currentNameContainer = document.getElementById("server");
        removeAllChildren(currentNameContainer);
        currentNameContainer.appendChild(document.createTextNode(that.server.name));
    }

    function isSubscribedOnServer() {
        var subscribers = server.subscribers;
        for (var i = 0; i < subscribers.length; i++)
            if (subscribers[i] == login)
                return true;

        return false;
    }

    function showCurrentSubscriptionStatus(server) {
        var subscriptionContainer = document.getElementById("subscription");
        removeAllChildren(subscriptionContainer);
        const subscribed = isSubscribedOnServer(server);
        var cbSubscribe = newLabeledCheckbox("subscribe", subscribed, function () {
            subscribed ? unsubscribe(server.id) : subscribe(server.id);
        });
        subscriptionContainer.appendChild(cbSubscribe);
    }

    function showNavigation(environments) {
        var navigationElemsList = document.getElementById("navigation-elems");
        removeAllChildren(navigationElemsList);

        environments.forEach(function (env) {
            var servers = env.servers;

            servers.forEach(function (srv) {
                var currentName = env.name +" "+ srv.name;
                var btn = newButton(currentName, function () {
                    that.server = srv;
                    that.serverSelectionHandler(srv);
                });
                navigationElemsList.appendChild(btn);
            });
        });
    }

    View.prototype.showEnvironments = function (environments) {
        showNavigation(environments);
        if(server != undefined)
            showCurrentServerName();
    };
    
    // View.prototype.showServerStatus = function (server) {
    //
    //

    //

    //
    //     showCurrentServerName(server.name);
    //     showCurrentSubscriptionStatus(srv);
    //     showSubscribers(srv);
    //     showCurrentResourcesStatus(srv);
    // };
}

View.prototype.subscribtionHandler = function (subscribes) {
    throw new Error("not bound");
};

View.prototype.reservationHandler = function (reserve) {
    throw new Error("not bound");
};

View.prototype.serverSelectionHandler = function (server) {
    throw new Error("not bound");
};

View.prototype.authenticationAttemptHandler = function (login, password) {
    throw new Error("not bound");
};

View.prototype.registrationHandler = function (login, password) {
    throw new Error("not bound");
};