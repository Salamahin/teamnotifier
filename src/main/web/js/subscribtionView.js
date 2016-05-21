function SubscribtionView() {
	const that = this;

	this.user = undefined;
	this.avatarCreator = undefined;

	var selectedTarget = undefined;
	var currentEnvironment = undefined;
	var actionButton = document.getElementById("subscribe_button");
	
	function disableActionButton() {
		if(actionButton.classList.contains("disabled"))
			return;

		actionButton.classList.add("disabled");
	}

	function enableActionButton() {
		actionButton.classList.remove("disabled");
	}

	function updateButtonState() {
		if(!selectedTarget){
			disableActionButton();
			actionButton.innerHTML = "selected a target";
			return;
		}

		enableActionButton();
	}

	function currentUserReservedResource(target) {
		return target.type == "ResourceInfo" && target.occupationInfo && user == target.occupationInfo.userName;
	}

	function currentUserSubscribedOnServer(target) {
		return target.type == "ServerInfo" && target.subscribers && target.subscribers.contails(user);
	}

	function prepareToSubscribe(target) {
		actionButton.innerHTML = "subscribe";
		actionButton.onclick = function() {
			that.subscribeHandler(target);
		}
	}

	function prepareToUnsubscribe(target) {
		actionButton.innerHTML = "unsubscribe";
		actionButton.onclick = function() {
			that.unsubscribeHandler(target);
		}
	}

	function prepareToReserve(target) {
		actionButton.innerHTML = "reserve";
		actionButton.onclick = function() {
			that.reservationHandler(target);
		}
	}

	function prepareToFree(target) {
		actionButton.innerHTML = "free";
		actionButton.onclick = function() {
			that.freeHandler(target);
		}
	}

	function installButtonHandler() {
		if(selectedTarget.type == "ServerInfo") {
			currentUserSubscribedOnServer(selectedTarget) 
				? prepareToUnsubscribe(selectedTarget) 
				: prepareToSubscribe(selectedTarget);
		} else if(selectedTarget.type == "ResourceInfo") {
			currentUserReservedResource
				? prepareToFree(selectedTarget)
				: prepareToReserve(selectedTarget);
		}
	}

	function extractResourceInformation(resource, actualEnvironments) {
		for(var i = 0; i<actualEnvironments.length; i++) {
			var env = actualEnvironments[i];
			for(var j = 0; j < env.servers.length; j++) {
				var srv = env.servers[j];
				for(var k = 0; k<srv.resources.length; k++) {
					var res = srv.resources[k];
					if(resource.id == res.id)
						return res;
				}
			}
		}
		throw new Error("EnvironmentInfo does not contain information about selected resource");
	}

	function extractServerInformation(server, actualEnvironments) {
		for(var i = 0; i < actualEnvironments.length; i++) {
			var env = actualEnvironments[i];
			for(var j = 0; j<env.servers.lengh; j++) {
				var srv = env.servers[j];
				if(server.id == srv.id)
					return srv;
			}
		}
		throw new Error("EnvironmentInfo does not contain information of the selected server");
	}

	SubscribtionView.prototype.select = function(target) {
		selectedTarget = target;
		installButtonHandler();
		//TODO show reservation
	}

	SubscribtionView.prototype.update = function(environment) {
		if(!selectedTarget) 
			return;

		if(selectedTarget.type == "ServerInfo")
			selectedTarget = extractServerInformation(selectedTarget, environment);
		else if(selectedTarget == "ResourceInfo")
			selectedTarget = extractResourceInformation(selectedTarget, environment);

		installButtonHandler();
		//TODO show subscribers
	}

	updateButtonState();
}

SubscribtionView.prototype.subscribeHandler = function(server) {
	throw new Error("not binded");
}

SubscribtionView.prototype.unsubscribeHandler = function(server) {
	throw new Error("not binded");
}

SubscribtionView.prototype.reservationHandler = function(resource) {
	throw new Error("not binded");
}

SubscribtionView.prototype.freeHandler = function(resource) {
	throw new Error("not binded");
}
