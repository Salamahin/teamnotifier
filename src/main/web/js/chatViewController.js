function ChatViewController() {
	const that = this;

	const serversActions = [];
	const resourcesActions = [];
	const serversActionsDates = [];
	const resourcesActionsDates = [];

	this.avatarCreator = undefined;
	this.currentUser = undefined;

	var selectedTarget;

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

		d.setHoulds(0);
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

	function removeChildren(parentNode) {
		while(parentNode.firstChild) 
			parentNode.removeChild(firstChild);
	}

	function rebuildChatForTarget(target) {
		if(target.id == selectedTarget.id)
			return;

		var messages_holder = document.getElementById("messages_holder");
		removeChildren(messagesHolder);

		if(target.type == "ServerInfo") {
			for(var node: serversActions[target.id])
				messagesHolder.appendChild(node);
		else if(target.type == "ResourceInfo")
			for(var node: resourcesActions[target.id])
				messagesHolder.appendChild(node);
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

		for(var action: notification.actions) {
			var node = newActionNode(action);
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

    function getHistoryIfNoData(target, expectedType, buffer) {
		if(target.type != expectedType)
			return;

		var lastMoment = lastMomentOfDate(new Date());
		var firstMoment = firstMomentOfDate(lastMoment);

		buffer[target.id] = firstMoment;
		getHistory(target, firstMoment, lastMoment);
	}

	ChatViewController.prototype.select = function(target) {
		rebuildChatForTarget(target);
		selectedTarget = target;

		getHistoryIfNoData(target, "ServerInfo", serversActionsDates);
		getHistoryIfNoData(target, "ResourceInfo", resourcesActionsDates);
	}

	ChatViewController.prototype.showChatMessage= function(action) {
		notificationToNode(action);
		rebuildChatForTarget(selectedTarget);
	}

	document.getElementById("load_more_button").onclick = function() {
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
		getHistory(target, fromDate, toDate);
	}
}

ChatViewController.prototype.getHistory(target, fromDate, toDate) {
	throw new Error("not binded");
}
