function SideMenuView() {
	const that = this;

	var nodesRoot = document.querySelector("#sidemenu .servers_list:nth-child(1)");

	this.user = undefined;
	this.avatarCreator = undefined;

	var serverNodes = new Map();
	var resourceNodes = new Map();

	function isUserSubscribedOnServer(server) {
		return server.subscribers != undefined && server.subscribers.includes(that.user);
	}

	/*
		Note, that "opened" class affects not the server_selection_button itself but .resources_list
	*/
	function showServerSelection(serverResourcesNode) {
		if(!serverResourcesNode.classList.contains("opened"))
			serverResourcesNode.classList.add("opened");
		
		removeClassFromOthers(getAllResourcesLists(), serverResourcesNode, "opened");
		removeClassFromOthers(getAllResourceSelectionButtonHolders(), undefined, "selected_resource");
	}

	/*
		Note, that selected_resource class affects not the resource_selection_button itself, but the parent div-node
	*/
	function showResourceSelection(resourceNodeHolder) {
		if(!resourceNodeHolder.classList.contains("selected_resource"))
			resourceNodeHolder.classList.add("selected_resource");	

		removeClassFromOthers(getAllResourceSelectionButtonHolders(), resourceNodeHolder, "selected_resource");
	}

	function removeClassFromOthers(allNodes, node, aClass) {
		for(var i = 0; i < allNodes.length; i++)
			if(allNodes[i] != node)
				allNodes[i].classList.remove(aClass);
	}

	function getAllResourcesLists() {
		return document.querySelectorAll(".resources_list");
	}

	function getAllResourceSelectionButtonHolders() {
		//since there is no parent selector in css we will do this manualy

		var buttons = document.querySelectorAll(".resource_selection_button");

		var holders = new Array(buttons.length);
		for(var i = 0; i < buttons.length; i++)
			holders[i] = buttons[i].parentNode;
		
		return holders;
	}

	function appendServerNode(environment, server) {
		var node = createNodeForServer(environment, server);
		nodesRoot.appendChild(node);

		serverNodes.set(server.id, node);

	}

	function buildEnvironmentServerListNodeName(environment, server) {
		return environment.name + " " + server.name;
	}

	function createNodeForServer(environment, server) {
		var node = document.createElement("li");

		var selectionButton = document.createElement("a");
		selectionButton.href = "#workbench";
		selectionButton.classList.add("server_selection_button");
		selectionButton.innerHTML= buildEnvironmentServerListNodeName(environment, server);
		selectionButton.onclick = function() {
			showServerSelection(getResourcesListNode(node));
			that.serverSelectionHandler(server);
		};

		var innerDiv = document.createElement("div");
		innerDiv.appendChild(selectionButton);

		var outerDiv = document.createElement("div");
		outerDiv.appendChild(innerDiv);

		node.appendChild(outerDiv);

		showServerSubscription(node, server);	
		showServerOnlineStatus(node, server);

		return node;
	}

	function getResourcesListNode(serverNode) {
		var listHolder = serverNode.querySelector("div > div");
		var list = listHolder.querySelector("ul.resources_list");

		if(!list) {
			list = document.createElement("ul");
			list.classList.add("resources_list");
			listHolder.appendChild(list);
		}
		return list;
	}

	function appendResourceNodeToServerNode(serverNode, resource) {
		var resourcesList = getResourcesListNode(serverNode);
		var resourceNode = createNewResourceNode(resource);

		resourcesList.appendChild(resourceNode);
		resourceNodes.set(resource.id, resourceNode);
	}


	function createNewResourceNode(resource) {
		var node = document.createElement("li");

		var innerDiv = document.createElement("div");
		var resourceSelectionButton = document.createElement("a");

		innerDiv.appendChild(resourceSelectionButton);

		resourceSelectionButton.href = "#workbench";
		resourceSelectionButton.innerHTML= resource.name;
		resourceSelectionButton.classList.add("resource_selection_button");
		resourceSelectionButton.onclick = function() {
			showResourceSelection(innerDiv);
			that.resourceSelectionHandler(resource);
		};
		
		var outerDiv = document.createElement("div");
		outerDiv.appendChild(innerDiv);

		node.appendChild(outerDiv);
		showResourceReservation(node, resource);

		return node;
	}
	
	function showResourceReservation(node, resource) {
		var avatarHolder = node.querySelector("div > div");
		var avatar = avatarHolder.querySelector(".avatar");

		if(avatar)
			avatarHolder.removeChild(avatar);

		if(resource.occupationInfo) {
			avatar = that.avatarCreator.getAvatarNode(resource.occupationInfo.userName);
			avatarHolder.appendChild(avatar);
		}
	}

	function showServerSubscription(node, server) {
		var subscriptionInfoHolder = node.querySelector("div > div");
		if(isUserSubscribedOnServer(server) && !subscriptionInfoHolder.classList.contains("subscribed"))
			subscriptionInfoHolder.classList.add("subscribed");
		else
			subscriptionInfoHolder.classList.remove("subscribed");
	
	}

	function showServerOnlineStatus(node, server) {
		var onlineStatusHolder = node.querySelector("div > div");
		
		if(server.isOnline === null) {
			onlineStatusHolder.classList.remove("online");
			onlineStatusHolder.classList.remove("offline");	
			return;
		}

		if(server.isOnline) {
			onlineStatusHolder.classList.add("online");
			return;
		}

		onlineStatusHolder.classList.add("offline");
	}

	SideMenuView.prototype.onServerAdded = function(environment, server) {
		appendServerNode(environment, server);
	}

	SideMenuView.prototype.onResourceAdded = function(server, resource) {
		appendResourceNodeToServerNode(serverNodes.get(server.id), resource);
	}

	SideMenuView.prototype.onReservationChanged = function(resource) {
		var node = resourceNodes.get(resource.id);
		showResourceReservation(node, resource);
	}

	SideMenuView.prototype.onSubscribersChanged = function(server) {
		var node = serverNodes.get(server.id);
		showServerSubscription(node, server);
	}

	SideMenuView.prototype.onOnlineStatusChanged = function(server) {
		var node = serverNodes.get(server.id);
		showServerOnlineStatus(node, server);
	}

	SideMenuView.prototype.setEnvironmentMonitor = function(monitor) {
		monitor.addListener(that);
	}

	SideMenuView.prototype.selectResource = function(resource) {
		var serverNode = serverNodes.get(resource.serverId);
		showServerSelection(getResourcesListNode(serverNode));

		var resourceNode = resourceNodes.get(resource.id);
		var innerDiv = resourceNode.querySelector("div > div");
		showResourceSelection(innerDiv);
		that.resourceSelectionHandler(resource);
	}
}

SideMenuView.prototype.serverSelectionHandler = function(server) {
	throw new Error("not binded");
}

SideMenuView.prototype.resourceSelectionHandler = function(resource) {
	throw new Error("not binded");
}
