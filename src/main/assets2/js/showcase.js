function SidepanelViewController() {
	const that = this;

	var cachedEnvironments;
	var user;

	function isUserSubscribedOnServer(server) {
		return server.subscribers != undefined && server.subscribers.includes(user);
	}


	function openResources(resourcesNode, serverId) {
		if(resourcesNode.classList.contains("opened"))
			return;
		
		resourcesNode.classList.add("opened");
		
		deselectOtherResources();
		serverSelectionHandler(serverId);
	}

	function closeResources(resourcesNode) {
		resourcesNode.classList.remove("opened");
	}

	function selectResource(resourceNode, resourceId) {
		if(resourceNode.classList.contains("selected_resource"))
			return;

		resourceNode.classList.add("selected_resource");	
		deselectOtherResources(resourceNode);
		resourceSelectionHandler(resourceId);
	}

	function deselectOtherResources(resourceNode) {
		var resources = getAllResourceSelectionButtons();
		for(var i = 0; i<resources.length; i++)
			if(resources[i] != resourceNode)
				resources[i].classList.remove("selected_resource");
	}

	function getResourcesList(button) {
		return button.parentNode.parentNode.childNodes[3];
	}

	function getResourceNode(button) {
		return button.parentNode;
	}

	function getAllServerSelectionButtons() {
		return document.querySelectorAll(".server_selection_button");
	}

	function getAllResourceSelectionButtons() {
		return document.querySelectorAll(".resource_selection_button");
	}

	function rebuildEnvironmentsView(environments) {
		for(var i = 0; i<environments.length; i++)
			rebuildEnvironmentView(environments[i]);

		that.environments = environments;
	}

	function rebuildEnvironmentView(environment) {
		var servers = environment.servers;
		for(var i = 0; i < servers.length; i++)
			rebuildServerAndEnvironment(environment, servers[i]);
	}

	function rebuildServerAndEnvironment(environment, server) {
		var node = findPresentListElemForEnvironmentAndServer(environment, server);
		if(node) {
			//TODO
		} else {
			node = createNewListElemForEnvironmentAndServer(environment, server);
			var serverList = document.querySelectorAll("#environment_list .servers_list:nth-child(1)")[0];
			serverList.appendChild(node);
		}
	}

	function buildEnverironmentServerListItemName(environment, server) {
		return environment.name + " " + server.name;
	}

	function findPresentListElemForEnvironmentAndServer(environment, server) {
		var elems = document.querySelectorAll("#environments_list ul li");
		for(var i = 0; i<elems.length; i++) {
			var button = elems[i].querySelector("div div:nth-child(1)");
			if(button.innerHTML== buildEnverironmentServerListItemName(environment, server))
				return button;
		}
		return undefined;
	}

	function createNewListElemForEnvironmentAndServer(environment, server) {
		const serverId = server.id;

		var resourcesList = createNewResourcesList(server.resources);

		var selectionButton = document.createElement("a");
		selectionButton.href = "#";
		selectionButton.classList.add("server_selection_button");
		selectionButton.innerHTML= buildEnverironmentServerListItemName(environment, server);
		selectionButton.onclick = function() {
			openResources(resourcesList, serverId);
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

	function createNewResourcesList(resources) { var list = document.createElement("ul");
		list.classList.add("resources_list");

		for(var i = 0; i<resources.length; i++)
			list.appendChild(createNewResource(resources[i]));

		return list;
	}

	function getAvatarUrl(user) {
		return "https://robohash.org/teamnotifier_" + user;
	}

	function createNewResource(resource) {
		const resourceId = resource.id;

		var resourceSelectionButton = document.createElement("a");
		resourceSelectionButton.href = "#";
		resourceSelectionButton.innerHTML= resource.name;
		resourceSelectionButton.classList.add("resource_selection_button");
		resourceSelectionButton.onclick = function() {
			selectResource(resourceSelectionButton, resourceId);
		};
		
		var innerDiv = document.createElement("div");
		innerDiv.appendChild(resourceSelectionButton);

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
		innerDiv.style.backgroundImange = getAvatarUrl(user);
		
		var outerDiv = document.createElement("div");
		outerDiv.appendChild(innerDiv);

		return outerDiv;
	}

	SidepanelViewController.prototype.setUser = function(user) {
		this.user = user;
	}

	SidepanelViewController.prototype.setEnvironments = function(env) {
		rebuildEnvironmentsView(env);
	}
}

SidepanelViewController.prototype.serverSelectionHandler = function(serverId) {
	throw new Error("not binded");
}

SidepanelViewController.prototype.resourceSelectionHandler = function(resourceId) {
	throw new Error("not binded");
}

const jsonStr = '{"type":"EnvironmentsInfo","environments":[{"type":"EnvironmentInfo","name":"environment","servers":[{"type":"ServerInfo","id":1,"name":"server","isOnline":true,"resources":[{"type":"ResourceInfo","id":1,"name":"resource","occupationInfo":{"type":"OccupationInfo","userName":"user","occupationTime":"2015-11-05T23:44:40.220Z"}}],"subscribers":["user"]}]}]}';

window.onload = function() {
	var sidepanel = new SidepanelViewController();

	var env = JSON.parse(jsonStr); 
	sidepanel.setEnvironments(env.environments);
}
