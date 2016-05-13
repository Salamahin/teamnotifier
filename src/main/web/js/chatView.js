function ChatView() {
	const that = this;

	const serversActions = [];
	const resourcesActions = [];
	const serversActionsDates = [];
	const resourcesActionsDates = [];
	const serverActionsOnceRequested = [];
	const resourceActionsOnceRequested=[];

	this.avatarCreator = undefined;
	this.currentUser = undefined;

	var selectedTarget;

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

	var makeActionButton = document.getElementById("make_action_button");
	var loadMoreButton = document.getElementById("load_more_button");
	var messagesHolder = document.getElementById("messages_holder");
	var inputBox = document.getElementById("action_input");

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
	
	function scrollMessagesToTop(target) {
		if(target.id == selectedTarget.id)
			messagesHolder.scrollTop = 0;
	}

	function scrollMessagesToBottom(target) {
		if(target.id == selectedTarget.id)
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

	function sortByDate(actions) {
		actions.sort(function(a, b) {
			return new Date(a.timestamp) - new Date(b.timestamp);
		});
	}

	function pushToBufferOneNode(notification, expectedType, buffer) {
		if(notification.type != expectedType)
			return;

		if(!buffer[notification.targetId])
			buffer[notification.targetId] = [];

		var node = getActionInfoNode(notification);
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
			var node = getActionInfoNode(notification.actions[i]);
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

		buffer[target.id] = firstMoment;
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

	ChatView.prototype.showNotification = function(notification) {
		notificationToNode(notification);
		rebuildChatForTarget(selectedTarget);
	}

	function buildConfirmationNotification(target, description) {
		var confirmation = new Object();

		if(target.type == "ServerInfo")
			confirmation.type = "ServerActionNotification";
		else if(target.type == "ResourceInfo")
			confirmation.type = "ResourceActionNotification";

		confirmation.actor = that.currentUser;
		confirmation.targetId = target.id;
		confirmation.timestamp = (new Date()).toISOString();
		confirmation.description = description;

		return confirmation;
	}

	ChatView.prototype.showServerAction = function(server, description) {
		that.showNotification(buildConfirmationNotification(server, description));
		scrollMessagesToBottom(server);
	}

	ChatView.prototype.showResourceAction = function(resource, description) {
		that.showNotification(buildConfirmationNotification(resource, description));
		scrollMessagesToBottom(resource);
	}

	ChatView.prototype.showServerActionsHistory = function(server, actions) {
		that.showNotification(actions);
		
		if(!serverActionsOnceRequested[server.id]) {
			scrollMessagesToBottom(server);
			serverActionsOnceRequested[server.id] = true;
		} else
			scrollMessagesToTop(server);
	}

	ChatView.prototype.showResourceActionsHistory = function(resource, actions) {
		that.showNotification(actions);
		
		if(!resourceActionsOnceRequested[resource.id]) {
			scrollMessagesToBottom(resource);
			resourceActionsOnceRequested[resource.id] = true;
		} else
			scrollMessagesToTop(resource);
	}

	loadMoreButton.onclick = function(e) {
		if(isDisabled(loadMoreButton))
			return;
	
		var buffer;

		if(selectedTarget.type == "ServerInfo")
			buffer = serversActionsDates;
		else
			buffer = resourcesActionsDates;
		
		var prevDate = buffer[selectedTarget.id];

		var fromDate = firstMomentOfDayBefore(prevDate);
		var toDate = lastMomentOfDate(fromDate);
		
		buffer[selectedTarget.id] = fromDate;
		callHistoryHandler(selectedTarget, fromDate, toDate);
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
