function ChatView() {
	const that = this;

	const serversActions = [];
	const resourcesActions = [];
	const serversActionsDates = [];
	const resourcesActionsDates = [];

	this.avatarCreator = undefined;
	this.user = undefined;

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

	function actionIsMadeByCurrentUser(actor) {
		return actor == that.user;
	}

	function getActionNode(action, mine) {
		var innerHolder = document.createElement("div");
		innerHolder.classList.add("message_details");
		innerHolder.classList.add(mine? "right" : "left");
		innerHolder.appendChild(getSummaryNode(action));
		innerHolder.appendChild(getDetailsNode(action));

		return innerHolder;
	}

	function newActionNode(action) {		
		var actor = action.actor;

		var holder = document.createElement("div");	
		holder.classList.add("action_info_holder");

		var avatar = that.avatarCreator.getAvatarNode(actor);

		if(actionIsMadeByCurrentUser(actor)) {
			var actionNode = getActionNode(action, true);
			
			holder.appendChild(avatar);
			holder.appendChild(actionNode);

		} else {
			var actionNode = getActionNode(action, false);
			
			holder.appendChild(actionNode);
			holder.appendChild(avatar);
		}

		return holder;
	}

	function removeMessagesChildren(parentNode) {
		var messages = parentNode.querySelectorAll(".action_info_holder");

		for(var i = 0; i<messages.length; i++)
			parentNode.removeChild(messages[i]);
	}

	function rebuildChatForTarget(target) {
		if(target.id != selectedTarget.id)
			return;

		var messagesHolder = document.getElementById("messages_holder");
		removeMessagesChildren(messagesHolder);

		if(target.type == "ServerInfo" && serversActions[target.id]) 
			for(var i = 0; i < serversActions[target.id].length; i++)
				messagesHolder.appendChild(serversActions[target.id][i]);
		else if(target.type == "ResourceInfo" && resourcesActions[target.id])
			for(var i = 0; i < resourcesActions[target.id].length; i++)
				messagesHolder.appendChild(resourcesActions[target.id][i]);

		messagesHolder.scrollTop = messagesHolder.scrollHeight;
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

		if(!buffer[notification.targetId])
			buffer[notification.targetId] = [];
	
		if(notification.actions.length == 0)
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

		if(buffer[target.id])
			return;

		var lastMoment = lastMomentOfDate(new Date());
		var firstMoment = firstMomentOfDate(lastMoment);

		callHistoryHandler(target, firstMoment, lastMoment);
	}

	ChatView.prototype.select = function(target) {
		selectedTarget = target;

		getHistoryIfNoData(target, "ServerInfo", serversActionsDates);
		getHistoryIfNoData(target, "ResourceInfo", resourcesActionsDates);

		rebuildChatForTarget(target);

		enable(loadMoreButton);
		enable(makeActionButton);
	}

	ChatView.prototype.showNotification = function(notification) {
		notificationToNode(notification);
		rebuildChatForTarget(selectedTarget);
	}

	ChatView.prototype.showServerAction = function(server, description) {
		throw new Error("not implemented");
	}

	ChatView.prototype.showResourceAction = function(resource, description) {
		throw new Error("not implemented");
	}

	ChatView.prototype.showServerActionsHistory = function(server, actions) {
		that.showNotification(actions);
	}

	ChatView.prototype.showResourceActionsHistory = function(resource, actions) {
		that.showNotification(actions);
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
