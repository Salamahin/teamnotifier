function View() {
    const that = this;

    this.login = undefined;
    var password;

    var selectedServer = undefined;
    var currentEnvironments = undefined;

    function jumpToAnchor(id) {
        window.location.hash = "#" + id;
    }

    this.jumpToEnvironment = function () {
        jumpToAnchor("environment");
    };

	function subDays(date, days) {
		var result = new Date(date);
		result.setDate(result.getDate() - days);
		return result;
	}

	function sendHistoryRequest(isResource, target, from, to) {
		if(isResource) 
			that.resourceActionsHistoryHandler(target, from, to);
		else
			that.serverActionsHistoryHandler(target, from, to);
	}

	function jumpToHistory (isResource, target) {
		var btnToday = document.getElementById("btn_hist_today");
		btnToday.onclick = function () {
			var now = new Date();
			sendHistoryRequest(isResource, target, subDays(now, 1), now);
		};

		document.getElementById("btn_hist_week").onclick = function () {
			var now = new Date();
			sendHistoryRequest(isResource, target, subDays(now, 7), now);
		};

		document.getElementById("btn_hist_month").onclick = function () {
			var now = new Date();
			sendHistoryRequest(isResource, target, subDays(now, 30), now);
		};

		btnToday.click();
		jumpToAnchor("history");
    };


	function jumpToServerActions () {
        jumpToAnchor("server_actions");
    };

	function jumpToResourceActions () {
        jumpToAnchor("resource_actions");
    };

    this.jumpToAuthentication = function () {
        jumpToAnchor("authentication");
    };

    function jumpToEnvironmentOnFocusLost(modal) {
        modal.addEventListener('click', function () {
            that.jumpToEnvironment();
        }, false);

        modal.children[0].addEventListener('click', function (e) {
            e.stopPropagation();
        }, false);
    }

    function jumpToEnvironmentOnEsc() {
        document.addEventListener('keyup', function (e) {
            if (e.keyCode == 27) {
                that.jumpToEnvironment();
            }
        });
    }

    function getInsertedData() {
        that.login = document.getElementById("ibox_username").value;
        password = document.getElementById("ibox_password").value;
    }

    function authenticationAttempt() {
        getInsertedData();
        that.authenticationAttemptHandler(that.login, password);
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
            that.registrationHandler(that.login, password);
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
        currentNameContainer.appendChild(document.createTextNode(selectedServer.name));
    }

    function isSubscribedOnServer() {
        var subscribers = selectedServer.subscribers;
        for (var i = 0; i < subscribers.length; i++)
            if (subscribers[i] == that.login)
                return true;

        return false;
    }

    function showCurrentSubscriptionStatus() {
        var subscriptionContainer = document.getElementById("subscription");
        removeAllChildren(subscriptionContainer);
        const subscribed = isSubscribedOnServer(selectedServer);
        var cbSubscribe = newLabeledCheckbox("subscribe", subscribed, function () {
            subscribed 
				? that.unsubscribeHandler(selectedServer)  
				: that.subscribeHandler(selectedServer);

        });
        subscriptionContainer.appendChild(cbSubscribe);
    }

    function getHistoryButton(isResource, target) {
        var btnHistory = newButton("", function () {
            var hist = document.getElementById("ul_hist");
            removeAllChildren(hist);
            jumpToHistory(isResource, target);
        });
        btnHistory.className = "round-button history-button tooltip";
        btnHistory.setAttribute("tip-text", "show history");
        return btnHistory;
    }

    function getActionButton(isResource, target) {
        var btnAction = newButton("", function () {
            showActionModal(isResource, target.id, target.name)
        });
        btnAction.className = "round-button action-button tooltip";
        btnAction.setAttribute("tip-text", "new action");
        return btnAction;
    }

    function getUniqueId() {
        return "id" + Math.random().toString(16).slice(2);
    }

    function newLabel(value) {
        var label = document.createElement("label");
        label.appendChild(document.createTextNode(value));
        return label;
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

    function getReservationCheckbox(resource, reserved) {
        return newLabeledCheckbox("reserve " + resource.name, reserved, function () {
                reserved ? that.freeHandler(resource) : that.reserveHandler(resource);
            }
        );
    }

    function sortFactory(prop) {
        return function(a,b){ return a[prop].localeCompare(b[prop]); };
    }

    function newResourceInfoElem(resource) {
        var occupationInfo = resource.occupationInfo;

        var btnHistory = getHistoryButton(true, resource);
        var btnAction = getActionButton(resource);
        var action;

        if (!occupationInfo) {
            action = getReservationCheckbox(resource, false);
        } else if (occupationInfo.userName == that.login) {
            action = getReservationCheckbox(resource, true);
        } else {
            action = decorateOccupationInfo(occupationInfo, resource.name);
        }

        var wrapper = document.createElement("div");
        wrapper.className = "resource";

        return decorateWith(wrapper, action, btnAction, btnHistory);
    }

    function showCurrentResourcesStatus() {
        var resourceFrame = document.getElementById("resources");
        removeAllChildren(resourceFrame);

        selectedServer.resources.forEach(function (resource) {
            resourceFrame.appendChild(newResourceInfoElem(resource))
        });
    }

    function showCurrentServerInfo() {
        showCurrentServerName();
        showCurrentSubscriptionStatus();
        showCurrentResourcesStatus();
    }

    function showNavigation() {
        var navigationElemsList = document.getElementById("navigation-elems");
        removeAllChildren(navigationElemsList);

        currentEnvironments.forEach(function (env) {
            var servers = env.servers;

            servers.forEach(function (srv) {
                var currentName = env.name +" "+ srv.name;
                var btn = newButton(currentName, function () {
                    selectedServer = srv;
                    showCurrentServerInfo();

                    that.serverSelectionHandler(srv);
                });
                navigationElemsList.appendChild(btn);
            });
        });
    }


	function chooseAServer() {
		selectedServer = Object.create(currentEnvironments[0].servers[0]);

		that.serverSelectionHandler(selectedServer);
	}

	function updateCurrentServerFromEnvironments() {
		for(var i = 0; i<currentEnvironments.length; i++) {
			for(var j = 0; j< currentEnvironments[i].servers.length; j++) {
				var updatedServer = currentEnvironments[i].servers[j];
				if(updatedServer.id == selectedServer.id) {
					selectedServer = Object.create(updatedServer);
					return;
				}
			}
		}
		throw new Error("current server is not found in status");
	}

    function updateCurrentServer() {
        if(selectedServer == undefined) 
			chooseAServer();
		 else 
			updateCurrentServerFromEnvironments();
    }

	function sortResourcesByName() {
		for(var i = 0; i<currentEnvironments.length; i++) 
			for(var j = 0;j<currentEnvironments[i].servers.length; j++) 
				currentEnvironments[i].servers[j].resources.sort(sortFactory('name'));
		
	}

    View.prototype.showStatus = function (environments) {
        currentEnvironments = Object.create(environments);
        updateCurrentServer();
		sortResourcesByName();

        showNavigation();
        showCurrentServerInfo();
    };

	View.prototype.showHistory = function(actions) {
		var hist = document.getElementById("ul_hist");
		actions.forEach(function (action) {
			hist.appendChild(decorateWith(document.createElement("li"), actionInfoToLabel(action)));
		});
	}
}

View.prototype.resourceActionsHistoryHandler = function(resource, from, to) {
	throw new Error("not bound");
}

View.prototype.serverActionsHistoryHandler = function(server, from, to) {
	throw new Error("not bound");
}

View.prototype.subscribeHandler = function (server) {
    throw new Error("not bound");
};

View.prototype.unsubscribeHandler = function (server) {
    throw new Error("not bound");
};

View.prototype.reserveHandler = function (resource) {
    throw new Error("not bound");
};

View.prototype.freeHandler = function (resource) {
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
