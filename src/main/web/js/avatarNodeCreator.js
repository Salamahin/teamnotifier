function AvatarNodeCreator() {
	function getAvatarUrl(user) {
		return "https://robohash.org/teamnotifier_" + user;
	}

	function createNewAvatar(user) {
		var innerDiv = document.createElement("div");
		innerDiv.style.backgroundImage = "url('" + getAvatarUrl(user) + "')";
		
		var outerDiv = document.createElement("div");
		outerDiv.classList.add("avatar");
		outerDiv.appendChild(innerDiv);

		return outerDiv;
	}

	AvatarNodeCreator.prototype.getAvatarNode = function(user) {
		return createNewAvatar(user);
	}
}
