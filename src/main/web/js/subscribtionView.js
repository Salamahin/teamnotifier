function SubscribtionView() {
	const that = this;

	this.user = undefined;
	this.avatarCreator = undefined;

	var selectedTarget = undefined;
	var environmentMonitor = undefined;


	const actionButton = document.getElementById("subscribe_button");
	const usersHolder = document.getElementById("users_holder");
	
	function disableActionButton() {
		if(actionButtonIsDisabled())
			return;

		actionButton.classList.add("disabled");
	}

	function actionButtonIsDisabled() {
		return actionButton.classList.contains("disabled");
	}

	function enableActionButton() {
		actionButton.classList.remove("disabled");
	}

	function updateButtonState() {
		if(!selectedTarget){
			disableActionButton();
			actionButton.innerHTML = "select a target";
			return;
		}

		if(selectedTarget.type == "ResourceInfo" && selectedTarget.occupationInfo && !currentUserReservedResource(selectedTarget)) {
			disableActionButton();
			actionButton.innerHTML = "reserved by other user";
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
		actionButton.onclick = function(e) {
			actionButtonIsDisabled() 
				? e.preventDefault()
				: that.subscribeHandler(target);
		}
	}

	function prepareToUnsubscribe(target) {
		actionButton.innerHTML = "unsubscribe";
		actionButton.onclick = function(e) {
			actionButtonIsDisabled() 
				? e.preventDefault()
				: that.unsubscribeHandler(target);
		}
	}

	function prepareToReserve(target) {
		actionButton.innerHTML = "reserve";
		actionButton.onclick = function(e) {
			actionButtonIsDisabled() 
				? e.preventDefault()
				: that.reserveHandler(target);
		}
	}

	function prepareToFree(target) {
		actionButton.innerHTML = "free";
		actionButton.onclick = function(e) {
			actionButtonIsDisabled() 
				? e.preventDefault()
				: that.freeHandler(target);
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

	function showReserver() {
		removeChildren(usersHolder);
		if(!selectedTarget.occupationInfo)
			return;
		
		var sinceLabel = document.createElement("label");
		sinceLabel.innerHTML = "since " + selectedTarget.occupationInfo.occupationTime;
		
		var avatar = that.avatarCreator.getAvatarNode(selectedTarget.occupationInfo.userName);
		
		usersHolder.appendChild(avatar);
		usersHolder.appendChild(sinceLabel);
	}

	function showActualData() {
		if(selectedTarget.type == "ServerInfo") {
			selectedTarget = environmentMonitor.getServer(selectedTarget.id);
			showSubscribers();
		} else if(selectedTarget.type = "ResourceInfo") {
			selectedTarget = environmentMonitor.getResource(selectedTarget.id);
			showReserver();
		}
		
		installButtonHandler();
		updateButtonState();
	}


	SubscribtionView.prototype.select = function(target) {
		selectedTarget = target;
		
		showActualData();
		updateButtonState();
	}

	SubscribtionView.prototype.unsubcribtionSuccess = function(server) {
		environmentMonitor.setSubscribers(server.id, []);
	}

	SubscribtionView.prototype.reservationSuccess = function(resource) {
		environmentMonitor.reserve(resource.id, that.user);
	}

	SubscribtionView.prototype.freeSuccess = function(resource) {
		environmentMonitor.free(resource.id, that.user);
	}

	SubscribtionView.prototype.setEnvironmentMonitor = function(monitor) {
		environmentMonitor = monitor;
		environmentMonitor.addListener(that);
	}

	SubscribtionView.prototype.onSubscribersChanged = function(server) {
		if(selectedTarget && server.id == selectedTarget.id)
			showActualData(selectedTarget);
	}

	SubscribtionView.prototype.onReservationChanged = function(resource) {
		if(selectedTarget && resource.id == selectedTarget.id)
			showActualData(selectedTarget);
	}

	updateButtonState();
}

SubscribtionView.prototype.subscribeHandler = function(server) {
	throw new Error("not binded");
}

SubscribtionView.prototype.unsubscribeHandler = function(server) {
	throw new Error("not binded");
}

SubscribtionView.prototype.reserveHandler = function(resource) {
	throw new Error("not binded");
}

SubscribtionView.prototype.freeHandler = function(resource) {
	throw new Error("not binded");
}
