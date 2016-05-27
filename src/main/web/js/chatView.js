function ChatView() {
	const that = this;

	var serverHistoryMonitor;
	var resourceHistoryMonitor;

	this.avatarCreator = undefined;
	this.currentUser = undefined;

	var selectedTarget;
	var previousButtonClicks = 0;

	function disable(node) {
		if(!node.classList.contains("disabled"))
			node.classList.add("disabled");
	}

	function enable(node) {
		node.classList.remove("disabled");
	}

	function isDisabled(node) {
		return node.classList.contains("disabled");
	}

	const makeActionButton = document.getElementById("make_action_button");
	const loadMoreButton = document.getElementById("load_more_button");
	const messagesHolder = document.getElementById("messages_holder");
	const inputBox = document.getElementById("action_input");

	disable(makeActionButton);
	disable(loadMoreButton);

	function lastMomentOfDate(date) {
		var d = new Date(date.getTime());

		d.setHours(23);
		d.setMinutes(59);
		d.setSeconds(59);
		d.setMilliseconds(999);

		return d;
	}

	function firstMomentOfDate(date) {
		var d = new Date(date.getTime());

		d.setHours(0);
		d.setMinutes(0);
		d.setSeconds(0);
		d.setMilliseconds(0);

		return d;
	}

	function firstMomentOfDayBefore(date) {
		var d = firstMomentOfDate(date);

		d.setDate(date.getDate() - 1);

		return d;
	}

	function getSummaryText(action) {
		return "@" + action.actor + " on " + action.timestamp;
	}

	function getActionSummaryNode(action) {
		var label = document.createElement("label");
		label.innerHTML = getSummaryText(action);

		var summary = document.createElement("div");
		summary.appendChild(label);
		summary.classList.add("summary");

		return summary;
	}


	function getActionDescriptionNode(action) {
		var label = document.createElement("label");
		label.innerHTML = action.description;

		var details = document.createElement("div");
		details.appendChild(label);
		details.classList.add("details");
		
		return details;
	}

	function actionIsMadeByCurrentUser(actor) {
		return actor == that.currentUser;
	}

	function getActionDetailsNode(action, mine) {
		var innerHolder = document.createElement("div");
		innerHolder.classList.add("message_details");
		innerHolder.classList.add(mine? "right" : "left");
		innerHolder.appendChild(getActionSummaryNode(action));
		innerHolder.appendChild(getActionDescriptionNode(action));

		return innerHolder;
	}

	function getActionInfoNode(action) {		
		var actor = action.actor;

		var holder = document.createElement("div");	
		holder.classList.add("action_info_holder");
		holder.timestamp = action.timestamp;

		var avatar = that.avatarCreator.getAvatarNode(actor);

		if(actionIsMadeByCurrentUser(actor)) {
			var actionNode = getActionDetailsNode(action, false);
			
			holder.appendChild(actionNode);
			holder.appendChild(avatar);
		} else {
			var actionNode = getActionDetailsNode(action, true);
			
			holder.appendChild(avatar);
			holder.appendChild(actionNode);
		}

		return holder;
	}

	function removeMessagesChildren(parentNode) {
		var messages = parentNode.querySelectorAll(".action_info_holder");

		for(var i = 0; i<messages.length; i++)
			parentNode.removeChild(messages[i]);
	}
	
	function scrollMessagesToTop() {
		messagesHolder.scrollTop = 0;
	}

	function scrollMessagesToBottom() {
		messagesHolder.scrollTop = messagesHolder.scrollHeight;
	}

	function rebuildChatForTarget(target) {
		if(target.id != selectedTarget.id)
			return;

		removeMessagesChildren(messagesHolder);

		if(target.type == "ServerInfo" && serversActions[target.id]) 
			for(var i = 0; i < serversActions[target.id].length; i++)
				messagesHolder.appendChild(serversActions[target.id][i]);
		else if(target.type == "ResourceInfo" && resourcesActions[target.id])
			for(var i = 0; i < resourcesActions[target.id].length; i++)
				messagesHolder.appendChild(resourcesActions[target.id][i]);
	}

	function idToday(date) {
		return new Date().toDateString() == date.toDateString();
	}


	function callHistoryHandler(target, daysBeforeToday) {
		if(target.type == "ResourceInfo")
			resourceHistoryMonitor.loadHistorieForDay(target.id, daysBeforeToday);
		else if(target.type == "ServerInfo") 
			serverHistoryMonitor.loadHistorieForDay(target.id, daysBeforeToday);
	}

	function callActionHandler(from, to) {
		if(selectedTarget.type == "ResourceInfo")
			that.newResourceActionHandler(selectedTarget, from, to);
		else if(selectedTarget.type == "ServerInfo") 
			that.newServerActionHandler(selectedTarget, from, to);
	}


	ChatView.prototype.select = function(target) {
		selectedTarget = target;

		getHistoryIfNoData(target, "ServerInfo", serversActionsDates);
		getHistoryIfNoData(target, "ResourceInfo", resourcesActionsDates);

		rebuildChatForTarget(target);

		tryEnableMakeActionButton();
		enable(loadMoreButton);

		scrollMessagesToBottom(target);
	}

	function pushConfirmationToHistoryMonitor(target, description) {
		var confirmation = new Object();

		confirmation.actor = that.currentUser;
		confirmation.targetId = target.id;
		confirmation.timestamp = (new Date()).toISOString();
		confirmation.description = description;

		if(target.type == "ServerInfo")
			serverHistoryMonitor.parseActionsNotification(confirmation);
		else if(target.type == "ResourceInfo")
			resourceHistoryMonitor.parseActionsNotification(confirmation);
	}


	ChatView.prototype.setServerHistoryMonitor = function(monitor) {
		serverHistoryMonitor = monitor;

		serverHistoryMonitor.actionsHandled = function(targetId, from, actions) {
			if(targetId != selectedTarget.id)
				return;
		}
	}

	ChatView.prototype.setResourceHistoryMonitor = function(monitor) {
		resourceHistoryMonitor = monitor;

		resourceHistoryMonitor.actionsHandled = function(targetId, from, actions) {
			if(targetId != selectedTarget.id)
				return;
		}
	}

	loadMoreButton.onclick = function(e) {
		if(isDisabled(loadMoreButton))
			return;

		previousButtonClicks++;
			
	}

	makeActionButton.onclick = function() {
		if(isDisabled(makeActionButton)) {
			return;
		}

		var details = inputBox.value;
		callActionHandler(details);
		inputBox.value = "";
	}

	function tryEnableMakeActionButton() {
		var text = inputBox.value;
		if(text && selectedTarget)
			enable(makeActionButton);
		else
			disable(makeActionButton);
	}

	inputBox.onkeyup = function() {
		tryEnableMakeActionButton();
	}
}

ChatView.prototype.serverActionsHistoryHandler = function(server, fromDate, toDate) {
	throw new Error("not binded");
}

ChatView.prototype.resourceActionsHistoryHandler = function(resource, fromDate, toDate) {
	throw new Error("not binded");
}

ChatView.prototype.newResourceActionHandler = function(resource, details) {
	throw new Error("not binded");
}

ChatView.prototype.newServerActionHandler = function(server, details) {
	throw new Error("not binded");
}
