function openResources(resourcesNode) {
	if(resourcesNode.classList.contains("opened"))
		return;
	
	resourcesNode.classList.add("opened");
}

function closeResources(resourcesNode) {
	resourcesNode.classList.remove("opened");
}

function getResourcesList(serverNode) {
	return serverNode.parentNode.parentNode.childNodes[3];
}


window.onload = function() {
	var buttons = document.querySelectorAll(".server_selection_button");

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
