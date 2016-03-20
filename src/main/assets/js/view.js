function View() {
    const that = this;
    var login;
    var password;

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

    View.prototype.removeAllChildren = function (parent) {
        while (parent.firstChild) {
            parent.removeChild(parent.firstChild);
        }
    };

    View.prototype.showServerStatus = function (server) {

        function showCurrentServerName(serverName) {
            var currentNameContainer = document.getElementById("server");
            removeAllChildren(currentNameContainer);
            currentNameContainer.appendChild(document.createTextNode(serverName));
        }

        function isSubscribedOnServer(server) {
            var subscribers = server.subscribers;
            for (var i = 0; i < subscribers.length; i++)
                if (subscribers[i] == View.prototype.login)
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

        showCurrentServerName(server.name);
        showCurrentSubscriptionStatus(srv);
        showSubscribers(srv);
        showCurrentResourcesStatus(srv);
    };
}

View.prototype.authenticationAttemptHandler = function (login, password) {
    throw new Error("not bound");
};

View.prototype.registrationHandler = function (login, password) {
    throw new Error("not bound");
};