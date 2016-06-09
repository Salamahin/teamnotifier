function ChatView() {
	const that = this;

	var serverHistoryMonitor;
	var resourceHistoryMonitor;

	this.avatarCreator = undefined;
	this.currentUser = undefined;

	var selectedTarget;
	const serverActionsDaysLoaded = [];
	const resourceActionsDaysLoaded = [];

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

	function getHistoryMonitor() {
		if(selectedTarget.type == "ResourceInfo")
			return resourceHistoryMonitor;
		if(selectedTarget.type == "ServerInfo")
			return serverHistoryMonitor;
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
	
	function messagesIsScrolledToBottom() {
		return messagesHolder.scrollTop === (messagesHolder.scrollHeight - messagesHolder.offsetHeight);
	}

	function scrollMessagesToBottom() {
		messagesHolder.scrollTop = messagesHolder.scrollHeight;
	}

	function showActions(targetId, actions, areCurrentActions) {
		if(!selectedTarget)
			return;

		if(targetId != selectedTarget.id)
			return;

		var scrolled = messagesIsScrolledToBottom();
		var oldScrollHeight = messagesHolder.scrollHeight;

		removeMessagesChildren(messagesHolder);

		for(var i = 0; i < actions.length; i++)
			messagesHolder.appendChild(getActionInfoNode(actions[i]));

		if(scrolled || areCurrentActions)
			scrollMessagesToBottom();
		else
			messagesHolder.scrollTop = messagesHolder.scrollHeight - oldScrollHeight;	
	}

	function callActionHandler(details) {
		if(selectedTarget.type == "ServerInfo")
			that.newServerActionHandler(selectedTarget, details);
		else if(selectedTarget.type == "ResourceInfo")
			that.newResourceActionHandler(selectedTarget, details);
	}

	ChatView.prototype.select = function(target) {
		selectedTarget = target;

 		getHistoryMonitor().loadHistoryForDay(selectedTarget);

		tryEnableMakeActionButton();
		enable(loadMoreButton);
	}

	function pushConfirmationToHistoryMonitor(target, description) {
		var confirmation = new Object();

		confirmation.actor = that.currentUser;
		confirmation.targetId = target.id;
		confirmation.timestamp = (new Date()).toISOString();
		confirmation.description = description;

		getHistoryMonitor().parseActionsNotification(confirmation);
	}

	ChatView.prototype.setServerHistoryMonitor = function(monitor) {
		serverHistoryMonitor = monitor;

		serverHistoryMonitor.actionsHandled = function(targetId, actions, areCurrentActions) {
			showActions(targetId, actions, areCurrentActions);
		}
	}

	ChatView.prototype.setResourceHistoryMonitor = function(monitor) {
		resourceHistoryMonitor = monitor;

		resourceHistoryMonitor.actionsHandled = function(targetId, actions, areCurrentActions) {
			showActions(targetId, actions, areCurrentActions);
		}
	}

	loadMoreButton.onclick = function(e) {
		if(isDisabled(loadMoreButton))
			return;

		getHistoryMonitor().loadMoreHistory(selectedTarget);
	}

	makeActionButton.onclick = function() {
		if(isDisabled(makeActionButton)) 
			return;

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


ChatView.prototype.newResourceActionHandler = function(resource, details) {
	throw new Error("not binded");
}

ChatView.prototype.newServerActionHandler = function(server, details) {
	throw new Error("not binded");
}
