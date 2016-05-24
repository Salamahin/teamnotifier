function SideMenuView() {
	const that = this;

	this.user = undefined;
	this.avatarCreator = undefined;

	var environmentsMonitor;

	var serverNodes = [];
	var resourceNodes = [];

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

	function buildEnvironments(environments) {
		serverNodes = [];
		resourceNodes = [];

		for(var i = 0; i<environments.length; i++)
			buildServersForEnvironment(environments[i]);
	}

	function buildServersForEnvironment(environment) {
		var servers = environment.servers;
		for(var i = 0; i < servers.length; i++)
			appendServerNode(environment, servers[i]);
	}

	function appendServerNode(environment, server) {
		var node = createNodeForServer(environment, server);
		var serverList = document.querySelectorAll("#sidemenu .servers_list:nth-child(1)")[0];
		serverList.appendChild(node);

		serverNodes[server.id] = node;
	}

	function buildEnverironmentServerListNodeName(environment, server) {
		return environment.name + " " + server.name;
	}

	function createNodeForServer(environment, server) {
		var resourcesList = createNodesForResources(server);

		var selectionButton = document.createElement("a");
		selectionButton.href = "#workbench";
		selectionButton.classList.add("server_selection_button");
		selectionButton.innerHTML= buildEnverironmentServerListNodeName(environment, server);
		selectionButton.onclick = function() {
			showServerSelection(resourcesList);
			that.serverSelectionHandler(server);
		};

		var innerDiv = document.createElement("div");
		if(isUserSubscribedOnServer(server))
			innerDiv.classList.add("subscribed");
		innerDiv.appendChild(selectionButton);

		var outerDiv = document.createElement("div");
		outerDiv.appendChild(innerDiv);
		outerDiv.appendChild(resourcesList);

		var listElem = document.createElement("li");
		listElem.appendChild(outerDiv);

		return listElem;
	}

	function createNodesForResources(server) { 
		var list = document.createElement("ul");
		list.classList.add("resources_list");

		var resources = server.resources;
		for(var i = 0; i<resources.length; i++) {
			var res = resources[i];
			var node = createNewResourceNode(res);

			list.appendChild(node);
			resourceNodes[res.id] = node; 
		}

		return list;
	}

	function createNewResourceNode(resource) {
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

		var listItem = document.createElement("li");
		listItem.appendChild(outerDiv);

		return listItem;
	}
	
	function showResourceReservation(node, resource) {
		var avatar = node.querySelector("avatar");
		node.removeChild(avatar);

		if(resource.occupationInfo) {
			avatar = that.avatarCreator.getAvatarNode(resource.occupationInfo.userName);
			node.querySelector("li > div(1)").appendChild(avatar);
		}
	}

	function showOnlineStatus(node, server) {
	}

	SideMenuView.prototype.onReservationChanged = function(resource) {
		//TODO
	}

	SideMenuView.prototype.onSubscribersChanged = function(server) {
		//TODO
	}

	SideMenuView.prototype.onOnlineStatusChanged = function(server) {
		//TODO
	}

	SideMenuView.prototype.setEnvironmentsMonitor = function(monitor) {
		environmentsMonitor = monitor;
		environmentsMonitor.addListener(that);
	}
}

SideMenuView.prototype.serverSelectionHandler = function(server) {
	throw new Error("not binded");
}

SideMenuView.prototype.resourceSelectionHandler = function(resource) {
	throw new Error("not binded");
}
