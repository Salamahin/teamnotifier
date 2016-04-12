function SideMenulView() {
	const that = this;

	this.user = undefined;

	var cachedEnvironments;

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

	function rebuildView(environments) {
		for(var i = 0; i<environments.length; i++)
			rebuildEnvironmentView(environments[i]);

		that.environments = environments;
	}

	function rebuildEnvironmentView(environment) {
		var servers = environment.servers;
		for(var i = 0; i < servers.length; i++)
			rebuildServerAndEnvironmentNode(environment, servers[i]);
	}

	function rebuildServerAndEnvironmentNode(environment, server) {
		var node = findPresentListNodeForEnvironmentAndServer(environment, server);
		if(node) {
			//TODO
		} else {
			node = createNewListNodeForEnvironmentAndServer(environment, server);
			var serverList = document.querySelectorAll("#sidemenu .servers_list:nth-child(1)")[0];
			serverList.appendChild(node);
		}
	}

	function buildEnverironmentServerListNodeName(environment, server) {
		return environment.name + " " + server.name;
	}

	function findPresentListNodeForEnvironmentAndServer(environment, server) {
		var elems = document.querySelectorAll("#environments_list ul li");

		for(var i = 0; i<elems.length; i++) {
			var button = elems[i].querySelector("div div:nth-child(1)");
			if(button.innerHTML== buildEnverironmentServerListNodeName(environment, server))
				return button;
		}

		return undefined;
	}

	function createNewListNodeForEnvironmentAndServer(environment, server) {
		var resourcesList = createNewResourceNodesList(environment, server);

		var selectionButton = document.createElement("a");
		selectionButton.href = "#";
		selectionButton.classList.add("server_selection_button");
		selectionButton.innerHTML= buildEnverironmentServerListNodeName(environment, server);
		selectionButton.onclick = function() {
			showServerSelection(resourcesList);
			that.serverSelectionHandler(environment, server);
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

	function createNewResourceNodesList(environment, server) { var list = document.createElement("ul");
		list.classList.add("resources_list");

		var resources = server.resources;
		for(var i = 0; i<resources.length; i++)
			list.appendChild(createNewResourceNode(environmen, server, resources[i]));

		return list;
	}

	function getAvatarUrl(user) {
		return "https://robohash.org/teamnotifier_" + user;
	}

	function createNewResourceNode(environment, server, resource) {
		var innerDiv = document.createElement("div");
		var resourceSelectionButton = document.createElement("a");

		innerDiv.appendChild(resourceSelectionButton);

		resourceSelectionButton.href = "#";
		resourceSelectionButton.innerHTML= resource.name;
		resourceSelectionButton.classList.add("resource_selection_button");
		resourceSelectionButton.onclick = function() {
			showResourceSelection(innerDiv);
			that.resourceSelectionHandler(environment, server, resource);
		};
		
		var outerDiv = document.createElement("div");
		outerDiv.appendChild(innerDiv);
		if(resource.occupationInfo) {
			var avatar = createNewAvatar(resource.occupationInfo.userName);
			outerDiv.appendChild(avatar);
		}

		var listItem = document.createElement("li");
		listItem.appendChild(outerDiv);

		return listItem;
	}

	function createNewAvatar(user) {
		var innerDiv = document.createElement("div");
		innerDiv.style.backgroundImage = "url('" + getAvatarUrl(user) + "')";
		
		var outerDiv = document.createElement("div");
		outerDiv.classList.add("avatar");
		outerDiv.appendChild(innerDiv);

		return outerDiv;
	}

	SideMenulView.prototype.setEnvironments = function(env) {
		rebuildView(env);
	}
}

SideMenulView.prototype.serverSelectionHandler = function(environment, server) {
	throw new Error("not binded");
}

SideMenulView.prototype.resourceSelectionHandler = function(environment, server, resource) {
	throw new Error("not binded");
}
