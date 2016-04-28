function ChatView() {
	const that = this;

	const serversActions = [];
	const resourcesActions = [];
	const serversActionsDates = [];
	const resourcesActionsDates = [];

	this.avatarCreator = undefined;
	this.currentUser = undefined;

	var selectedTarget;

	function disable(node) {
		node.classList.add("disabled");
	}

	function enable(node) {
		node.classList.remove("disabled");
	}

	function isDisabled(node) {
		return node.classList.contains("disabled");
	}

	var makeActionButton = document.getElementById("make_action_button");
	var loadMoreButton = document.getElementById("load_more_button");

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

	function getSummaryNode(action) {
		var label = document.createElement("label");
		label.innerHTML = getSummaryText(action);

		var summary = document.createElement("div");
		summary.appendChild(label);
		summary.classList.add("summary");

		return summary;
	}


	function getDetailsNode(action) {
		var label = document.createElement("label");
		label.innerHTML = action.description;

		var details = document.createElement("div");
		details .appendChild(label);
		details.classList.add("details");

		return details;
	}

	function actionIsMadeByCurrentUser(action) {
		return action.actor == currentUser;
	}

	function getDetailsNode(action, left) {
		var innerHolder = document.createElement("div");
		innerHolder.classList.add("message_details");
		innerHolder.classList.add(left? "left" : "right");
		innerHolder.appendChild(getSummaryNode(action));
		innerHolder.appendChild(getDetailsNode(action));

		var outerHolder = document.createElement("div");
		outerHolder.appendChild(innerHolder);

		return outerHolder;
	}

	function newActionNode(action) {		
		var actor = action.actor;

		var holder = document.createElement("div");	
		holder.classList.add("action_info_holder");

		var avatar = that.avatarCreator.getAvatarNode(actor);

		if(actionIsMadeByCurrentUser(actor)) {
			var detailsNode = getDetailsNode(action, true);
			
			holder.appendChild(detailsNode);
			holder.appendChild(avatar);
		} else {
			var detailsNode = getDetailsNode(action, false);
			
			holder.appendChild(avatar);
			holder.appendChild(detailsNode);
		}

		return holder;
	}

	function removeMessagesChildren(parentNode) {
		var messages = document.getElemensByClassName("action_info_holder");

		while(messages[0].length) 
			messages[0].parentNode.removeChild(messages[0]);
	}

	function rebuildChatForTarget(target) {
		if(target.id == selectedTarget.id)
			return;

		var messages_holder = document.getElementById("messages_holder");
		removeMessagesChildren(messagesHolder);

		if(target.type == "ServerInfo") 
			for(var i = 0; i < serversActions[target.id].length; i++)
				messagesHolder.appendChild(serversActions[target.id][i]);
		else if(target.type == "ResourceInfo")
			for(var i = 0; i < resourcesActions[target.id].length; i++)
				messagesHolder.appendChild(resourcesActions[target.id][i]);
	}

	function sortByDate(actions) {
		actions.sort(function(a, b) {
			return new Date(a.timestamp) - new Date(b.timestamp);
		});
	}

	function pushToBufferOneNode(notification, expectedType, buffer) {
		if(notification.type != expectedType)
			return;

		var node = newActionNode(notification);
		buffer[notification.targetId].push(node);
		sortByDate(buffer[notification.targetId]);
	}

	function pushToBufferSeveralNodes(notification, expectedType, buffer) {
		if(notification.type != expectedType)
			return;

		for(var i = 0; i< notification.actions.length; i++) {
			var node = newActionNode(notification.actions[i]);
			buffer[notification.targetId].push(node);
		}

		sortByDate(buffer[notification.targetId]);
	}

	function notificationToNode(notification) {
		pushToBufferOneNode(notification, "ServerActionNotification", serversActions);
		pushToBufferOneNode(notification, "ResourceActionNotification", resourcesActions);
		pushToBufferSeveralNodes(notification, "ServerActionsHistory", serversActions);
		pushToBufferSeveralNodes(notification, "ResourceActionsHistory", resourcesActions);
	}

	function callHistoryHandler(target, from, to) {
		if(target.type == "ResourceInfo")
			that.resourceActionsHistoryHandler(target, from, to);
		else if(target.type == "ServerInfo") 
			that.serverActionsHistoryHandler(target, from, to);
	}

	function callActionHandler(from, to) {
		if(selectedTarget.type == "ResourceInfo")
			that.newResourceActionHandler(selectedTarget, from, to);
		else if(selectedTarget.type == "ServerInfo") 
			that.newServerActionHandler(selectedTarget, from, to);
	}

    function getHistoryIfNoData(target, expectedType, buffer) {
		if(target.type != expectedType)
			return;

		var lastMoment = lastMomentOfDate(new Date());
		var firstMoment = firstMomentOfDate(lastMoment);

		buffer[target.id] = firstMoment;
		callHistoryHandler(target, firstMoment, lastMoment);
	}

	ChatView.prototype.select = function(target) {
		selectedTarget = target;
		rebuildChatForTarget(target);

		getHistoryIfNoData(target, "ServerInfo", serversActionsDates);
		getHistoryIfNoData(target, "ResourceInfo", resourcesActionsDates);

		enable(loadMoreButton);
		enable(makeActionButton);
	}

	ChatView.prototype.showChatMessage= function(action) {
		notificationToNode(action);
		rebuildChatForTarget(selectedTarget);
	}

	loadMoreButton.onclick = function(e) {
		if(isDisabled(loadMoreButton)) {
			return;
		}

		var target = that.selectedTarget;
		var buffer;

		if(target.type == "ServerInfo")
			buffer = serversActionsDates;
		else
			buffer = resourcesActionsDates;
		
		var prevDate = buffer[target.id];

		var fromDate = firstMomentOfDayBefore(prevDate);
		var toDate = lastMomentOfDate(fromDate);
		
		buffer[target.id] = fromDate;
		callHistoryHandler(target, fromDate, toDate);
	}


	makeActionButton.onclick = function() {
		if(isDisabled(loadMoreButton)) {
			return;
		}

		var details = document.getElementById("action_input").value;
		callActionHandler(details);
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
