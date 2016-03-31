function openResources(resourcesNode) {
	if(resourcesNode.classList.contains("opened"))
		return;
	
	resourcesNode.classList.add("opened");
	
	var resourceSelectionbuttons = getAllResourceSelectionButtons();
	for(var i = 0; i<resourceSelectionbuttons.length; i++)
		deselectResource(getResourceNode(resourceSelectionbuttons[i]));
}

function closeResources(resourcesNode) {
	resourcesNode.classList.remove("opened");
}

function selectResource(resourceNode) {
	if(resourceNode.classList.contains("selected_resource"))
		return;

	resourceNode.classList.add("selected_resource");	
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

function installServerSelectionHandlers() {
	var buttons = getAllServerSelectionButtons();

	for(var i = 0; i<buttons.length; i++) {
		const b=buttons[i];
		b.onclick = function() {
			for(var j = 0; j<buttons.length; j++) {
                var resourcesNode = getResourcesList(buttons[j]);
				if(b == buttons[j])
					openResources(resourcesNode);
				else
					closeResources(resourcesNode);
			}
		}
	}
}

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
}


window.onload = function() {
	installServerSelectionHandlers();
	installResourceSelectionHandlers();
}
