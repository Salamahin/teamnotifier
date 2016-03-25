function toggleClass(elem, aClass) {
	if(elem.classList.contains(aClass)) 
		elem.classList.remove(aClass);
	else
		elem.classList.add(aClass);
}

window.onload = function() {
	var button = document.getElementById("subscribtion_button"); 
	button.onclick = function() {
		toggleClass(button, "pressed");
	};
}
