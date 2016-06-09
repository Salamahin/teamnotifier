function AvatarNodeCreator() {
	const that = this;

	AvatarNodeCreator.prototype.getAvatarUrl = function(user) {
		return "https://robohash.org/teamnotifier_" + user;
	}

	function createNewAvatar(user) {
		var innerDiv = document.createElement("div");
		innerDiv.style.backgroundImage = "url('" + that.getAvatarUrl(user) + "')";
		
		var outerDiv = document.createElement("div");
		outerDiv.classList.add("avatar");
		outerDiv.appendChild(innerDiv);

		return outerDiv;
	}

	AvatarNodeCreator.prototype.getAvatarNode = function(user) {
		return createNewAvatar(user);
	}

	AvatarNodeCreator.prototype.getAvatarNodeWithSusbscribtion = function(user) {
		var node = createNewAvatar(user);

		var label = document.createElement("label");
		label.innerHTML = "@" + user;
		label.classList.add("avatar_subscribtion");

		var holder = document.createElement("div");
		holder.classList.add("avatar_holder");
		holder.appendChild(node);
		holder.appendChild(label);

		return holder;
	}
}
