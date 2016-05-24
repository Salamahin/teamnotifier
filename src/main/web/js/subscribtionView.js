function SubscribtionView() {
	const that = this;

	this.user = undefined;
	this.avatarCreator = undefined;

	var selectedTarget = undefined;
	var currentEnvironment = undefined;

	const actionButton = document.getElementById("subscribe_button");
	const usersHolder = document.getElementById("users_holder");
	
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
		return target.type == "ResourceInfo" && target.occupationInfo && that.user == target.occupationInfo.userName;
	}

	function currentUserSubscribedOnServer(target) {
		return target.type == "ServerInfo" && target.subscribers && target.subscribers.includes(that.user);
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
			currentUserReservedResource(selectedTarget)
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

	function removeChildren(parentNode) {
		while(parentNode.childNodes.length)
			parentNode.removeChild(parentNode.childNodes[0]);
	}

	function showSubscribers() {
		removeChildren(usersHolder);

		for(var i = 0; i<selectedTarget.subscribers.length; i++) {
			var avatarNode = that.avatarCreator.getAvatarNode(selectedTarget.subscribers[i]);
			usersHolder.appendChild(avatarNode);
		}
	}

	function updateSubscribtionInfo(subscribtionInfo) {
		if(selectedTarget.type == "ServerInfo" && selectedTarget.id == subscribtionInfo.serverId) {
			selectedTarget.subscribers = subscribtionInfo.subscribers;
			showSubscribers();
		}
	}

	function showReserver() {
		removeChildren(usersHolder);
		if(!selectedTarget.occupationInfo)
			return;
		
		var sinceLabel = document.createElement("label");
		label.value = "Since " + selectedTarget.occupationInfo.occupationTime;
		
		var avatar = avatarCreator.getAvatarNode(selectedTarget.occupationInfo.userName);
		
		usersHolder.appendChild(avatar);
		usersHolder.appendChild(sinceLabel);
	}

	function showUsersAvatars() {
		if(selectedTarget.type == "ServerInfo")
			showSubscribers();
		else if(selectedTarget.type == "ResourceInfo")
			showReserver();
	}

	SubscribtionView.prototype.select = function(target) {
		selectedTarget = target;
		installButtonHandler();
		showUsersAvatars();
		updateButtonState();
	}

	SubscribtionView.prototype.update = function(environment) {
		currentEnvironment = environment;

		if(!selectedTarget) 
			return;

		if(selectedTarget.type == "ServerInfo")
			selectedTarget = extractServerInformation(selectedTarget, environment);
		else if(selectedTarget == "ResourceInfo")
			selectedTarget = extractResourceInformation(selectedTarget, environment);

		installButtonHandler();
		showUsersAvatars();
	}

	SubscribtionView.prototype.subscribtionSuccess = function(subscribtionInfo) {
		updateSubscribtionInfo(subscribtionInfo);
		updateButtonState();
	}

	SubscribtionView.prototype.reservationSuccess = function(resource) {
		var r = extractResourceInformation(resource, currentEnvironment);
		r.occupationInfo = {};
		r.occupationInfo.userName = that.user;
		r.occupationInfo.occupationTime = new Date();
	}

	SubscribtionView.prototype.freeSuccess = function(resource) {
		var r = extractResourceInformation(resource, currentEnvironment);
		r.occupationInfo = undefined;
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
