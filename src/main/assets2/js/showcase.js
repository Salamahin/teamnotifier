function SidepanelViewController() {
	const that = this;

	var cachedEnvironments;
	var user;

	function isUserSubscribedOnServer(server) {
		return server.subscribers != undefined && server.subscribers.contains(user);
	}


	function openResources(resourcesNode, serverId) {
		if(resourcesNode.classList.contains("opened"))
			return;
		
		resourcesNode.classList.add("opened");
		
		var resourceSelectionbuttons = getAllResourceSelectionButtons();
		for(var i = 0; i<resourceSelectionbuttons.length; i++)
			deselectResource(getResourceNode(resourceSelectionbuttons[i]));

		serverSelectionHandler(serverId);
	}

	function closeResources(resourcesNode) {
		resourcesNode.classList.remove("opened");
	}

	function selectResource(resourceNode, resourceId) {
		if(resourceNode.classList.contains("selected_resource"))
			return;

		resourceNode.classList.add("selected_resource");	
		resourceSelectionHandler(resourceId);
	}

	function deselectResource(resourceNode) {
		resourceNode.classList.remove("selected_resource");
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

	//function installServerSelectionHandlers() {
	//	var buttons = getAllServerSelectionButtons();

	//	for(var i = 0; i<buttons.length; i++) {
	//		const b=buttons[i];
	//		b.onclick = function() {
	//			for(var j = 0; j<buttons.length; j++) {
	//				var resourcesNode = getResourcesList(buttons[j]);
	//				if(b == buttons[j])
	//					openResources(resourcesNode);
	//				else
	//					closeResources(resourcesNode);
	//			}
	//		}
	//	}
	//}

	function getAllResourceSelectionButtons() {
		return document.querySelectorAll(".resource_selection_button");
	}

	function installResourceSelectionHandlers() {
		var buttons = getAllResourceSelectionButtons();

		for(var i = 0; i<buttons.length; i++) {
			const b=buttons[i];
			b.onclick = function() {
				for(var j = 0; j<buttons.length; j++) {
					var resourceNode = getResourceNode(buttons[j]);
					if(b == buttons[j])
						selectResource(resourceNode);
					else
						deselectResource(resourceNode);
				}
			}
	
	}

	function rebuildEnvironmentView(environments) {
		for(var i = 0; i<environments.length; i++)
			rebuildEnvironmentView(environments[i]);

		that.environments = environments;
	}

	function rebuildEnvironmentView(environment) {
		var servers = environments.servers;
		for(var i = 0; i < servers.length; i++)
			rebuildServerAndEnvironment(environment, servers[i]);
		
	}

	function buildEnverironmentServerListItemName(environment, server) {
		return environment.name + " " + server.name;
	}

	function findPresentListElemForEnvironmentAndServer(environment, server) {
		var elems = document.querySelectorAll("#environments_list ul li");
		for(var i = 0; i<elems.length; i++) {
			var button = elems[i].querySelector("div div:nth-child(1)");
			if(button.nodeValue == buildEnverironmentServerListItemName(environment, server))
				return button;
		}
		return undefined;
	}

	function createNewListElemForEnvironmentAndServer(environemnt, server) {
		const serverId = server.id;

		var resourcesList = createNewResourcesList(server.resources);

		var selectionButton = document.createElement("a");
		selectionButton.href = "#";
		selectionButton.classList.add("server_selection_button");
		selectionButton.textValue = buildEnverironmentServerListItemName(environment, server);
		selectionButton.onclick = function() {
			openResources(resourcesList, serverId);
		};

		var innerDiv = document.createElement("div");
		if(isUserSubscribedOnServer(server))
			innerDiv.classList.add("subscribed");
		innerDiv.childNodes.add(selectionButton);
		innerDiv.childNodes.add(resourcesList);

		var outerDiv = document.createElement("div");
		outerDiv.childNodes.add(innerDiv);

		return outerDiv;
	}

	function createNewResourcesList(resources) {
		var list = document.createElement("ul");
		list.classList.add("resource_list");

		for(var i = 0; i<resources.length; i++)
			list.childNodes.add(createNewResource(resources[i]));

		return list;
	}

	function getAvatarUrl(user) {
		return "https://robohash.org/teamnotifier_" _ user;
	}

	function createNewResource(resource) {
		const resourceId = resource.id;

		var resourceSelectionButton = document.createElement("a");
		resourceSelectionButton.href = "#";
		resourceSelectionButton.textValue = resource.name;
		resourceSelectionButton.classList.add("resource_selection_button"):
		resourceSelectionButton.onclick = function() {
			selectResource(resourceSelectionButton, resourceId);
		};
		
		var innerDiv = document.createElement("div");
		innerDiv.childElements.add(resourceSelectionButton);

		var outerDiv = document.createElement("div");
		outerDiv.childElements.add(innerDiv);
		//TODO add avatar if reserved

		var listItem = document.createElement("li");
		listItem.childElements.add(outerDiv);

		return listItem;
	}

	function createNewAvatar(user) {
		var innerDiv = document.createElement("div");
		div.style.background-imange = getAvatarUrl(user);
		
		var outerDiv = document.createElement("div");
		outerDiv.childNodes.add(innerDiv);

		return outerDiv;
	}

	SidepanelViewController.prototype.init = function () {
		installServerSelectionHandlers();
		installResourceSelectionHandlers();
	}

	SidepanelViewController.prototype.setUser = function(user) {
		this.user = user;
	}
}

SidepanelViewController.prototype.serverSelectionHandler(serverId) {
	throw new Error("not binded");
}

SidepanelViewController.prototype.resourceSelectionHandler(resourceId) {
	throw new Error("not binded");
}


window.onload = function() {
	var sidepanel = new SidepanelViewController();
	sidepanel.init();
}
