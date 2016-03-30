function toggleClass(elem, aClass) {
	if(elem.classList.contains(aClass)) 
		elem.classList.remove(aClass);
	else
		elem.classList.add(aClass);
}

function getResourcesList(serverNode) {
	return serverNode.parentNode.parentNode.childNodes[3];
}

function openResource(serverNode) {
	var resources = getResourcesList(serverNode);
	toggleClass(resources, "opened");
}

window.onload = function() {
	var buttons = document.querySelectorAll(".server_selection_button");


	for(var i = 0; i<buttons.length; i++) {
		const b=buttons[i];
		b.onclick = function() {
			openResource(b);
		}
	}
}
