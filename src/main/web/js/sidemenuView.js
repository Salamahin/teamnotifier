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
	function selectServer(serverResourcesNode, server) {
		if(!serverResourcesNode.classList.contains("opened"))
			serverResourcesNode.classList.add("opened");
		
		removeClassFromOthers(getAllResourcesLists(), serverResourcesNode, "opened");
		removeClassFromOthers(getAllResourceSelectionButtonHolders(), undefined, "selected_resource");

		that.serverSelectionHandler(server);
	}

	/*
		Note, that selected_resource class affects not the resource_selection_button itself, but the parent div-node
	*/
	function selectResource(resourceNodeHolder, resource) {
		if(resourceNodeHolder.classList.contains("selected_resource"))
			return;

		resourceNodeHolder.classList.add("selected_resource");	

		removeClassFromOthers(getAllResourceSelectionButtonHolders(), resourceNodeHolder, "selected_resource");
		that.resourceSelectionHandler(resource);
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
			var serverList = document.querySelectorAll("#sidemenu .servers_list:nth-child(1)")[0];
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
		var resourcesList = createNewResourcesList(server.resources);

		var selectionButton = document.createElement("a");
		selectionButton.href = "#";
		selectionButton.classList.add("server_selection_button");
		selectionButton.innerHTML= buildEnverironmentServerListItemName(environment, server);
		selectionButton.onclick = function() {
			selectServer(resourcesList, server);
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
		var innerDiv = document.createElement("div");
		var resourceSelectionButton = document.createElement("a");

		innerDiv.appendChild(resourceSelectionButton);

		resourceSelectionButton.href = "#";
		resourceSelectionButton.innerHTML= resource.name;
		resourceSelectionButton.classList.add("resource_selection_button");
		resourceSelectionButton.onclick = function() {
			selectResource(innerDiv, resource);
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
		rebuildEnvironmentsView(env);
	}
}

SideMenulView.prototype.serverSelectionHandler = function(server) {
	throw new Error("not binded");
}

SideMenulView.prototype.resourceSelectionHandler = function(resource) {
	throw new Error("not binded");
}
