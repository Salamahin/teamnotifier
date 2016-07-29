function Header() {
	const that = this;
	const selectionInfoNode = document.getElementById("selected_target");
	const resourceSelectionNodesHolder = document.querySelector("#reserved_resources_holder > ul");
	const reservationSummaryNode = document.querySelector("#reserved_resources_holder > label");
	const notEmptyIndicatorNode = document.getElementById("reserved_resources_holder");
	const currentUserHolder = document.getElementById("current_user_holder");

	this.avatarCreator = undefined;

	var resourceSelectionNodes = new Map();
	var reservationNodes = new Map();
	var serversByResourcesIds = new Map();
	var environmentsByServersIds = new Map();
	var user;

	function updateReserationSummary() {
		var count = resourceSelectionNodes.size;
		reservationSummaryNode.innerHTML = "reserved: " + count;
		if(count)
			notEmptyIndicatorNode.classList.add("not_empty");
		else
			notEmptyIndicatorNode.classList.remove("not_empty");
	}

	function currentUserHasResourceReserved(resource) {
		return resource.occupationInfo.userName == user;
	}

	function getTextForSelectedResource(resource) {
		var server = serversByResourcesIds.get(resource.id);
		var env = environmentsByServersIds.get(server.id);
		return env.name.toUpperCase() + " " + server.name.toUpperCase() + " " + resource.name;
	}

	function getTextForSelectedServer(server) {
		var env = environmentsByServersIds.get(server.id);
		return env.name.toUpperCase() + " " + server.name.toUpperCase();
	}

	function createResourceSelectionNode(resource) {
		var innerHref = document.createElement("a");
		innerHref.href = "#workbech";
		innerHref.innerHTML = getTextForSelectedResource(resource);
		innerHref.onclick = function(e) {
			that.resourceSelectedHandler(resource);
		}

		var outerLi = document.createElement("li");
		outerLi.appendChild(innerHref);

		return outerLi;
	}

	Header.prototype.select = function(target) {
		if(target == undefined) {
			selectionInfoNode.innerHTML = "";
			return;
		}

		if(target.type == "ResourceInfo")
			selectionInfoNode.innerHTML = getTextForSelectedResource(target);
		else if(target.type == "ServerInfo")
			selectionInfoNode.innerHTML = getTextForSelectedServer(target);
	}

    function shouldAddResourceSelectionNode(resource) {
    	return resource.occupationInfo && currentUserHasResourceReserved(resource);
    }

    function appendResourceSelectionNode(resource) {
    	var node = createResourceSelectionNode(resource);
		resourceSelectionNodes.set(resource.id, node);
		resourceSelectionNodesHolder.appendChild(node);

		updateReserationSummary();
    }

    function shouldRemoveResourceSelectionNode(resource) {
    	return !resource.occupationInfo && resourceSelectionNodes.get(resource.id);
    }

    function removeResourceSelectionNode(resource) {
    	resourceSelectionNodesHolder.removeChild(resourceSelectionNodes.get(resource.id));
		resourceSelectionNodes.delete(resource.id);

		updateReserationSummary();
    }

	Header.prototype.onReservationChanged = function(resource) {
		if(shouldAddResourceSelectionNode(resource))
			appendResourceSelectionNode(resource);
		else if(shouldRemoveResourceSelectionNode(resource))
			removeResourceSelectionNode(resource)
	}

	Header.prototype.onServerAdded = function(environment, server) {
		environmentsByServersIds.set(server.id, environment);
	}

	Header.prototype.onResourceAdded = function(server, resource) {
		serversByResourcesIds.set(resource.id, server);
		if(shouldAddResourceSelectionNode(resource))
			appendResourceSelectionNode(resource);

	}

	Header.prototype.setUser = function(currentUser) {
		user = currentUser;
		var avatar = that.avatarCreator.getAvatarNode(user);
		avatar.onclick = function() {
			that.requestAppToken();
		}
		currentUserHolder.appendChild(avatar);		
	}

	Header.prototype.setEnvironmentMonitor = function(monitor) {
		monitor.addListener(that);
	}

	selectionInfoNode.innerHTML = "";
	updateReserationSummary();
}

Header.prototype.requestAppToken = function() {
	throw new Error("not binded");
}

Header.prototype.resourceSelectedHandler = function(resource) {
	throw new Error("not binded");
}
