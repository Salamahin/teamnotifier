function UserServiceView() {
	const that = this;

	const usernameField = document.getElementById("username_field");
	const passwordField = document.getElementById("password_field");
	const form = document.getElementById("user_service_view");

	function resetPasswordText() {
		passwordField.value = "";
	}

	function doAuthenticate() {
		form.classList.remove("invalid");

		var username = usernameField.value;
		var password = passwordField.value;

		that.authenticationHandler(username, password);
		resetPasswordText();
	}

	function doRegister() {
		var username = usernameField.value;
		var password = passwordField.value;

		that.registrationHandler(username, password);
		resetPasswordText();
	}

	function toggleParentPressedClassOnFocus(input, holder) {
		input.onfocus = function() {
			holder.classList.add("pressed");
		}

		input.onblur = function() {
			holder.classList.remove("pressed");
		}
	}

	toggleParentPressedClassOnFocus(
		usernameField,
		document.getElementById("username_field_holder")
	);

	toggleParentPressedClassOnFocus(
		passwordField,
		document.getElementById("password_field_holder")
	);

	passwordField.onkeypress = function(e) {
		passwordField.classList.remove("invalid");

		if(e.which == 13) 
			doAuthenticate();
	};

	document.getElementById("login_button").onclick = doAuthenticate;
	document.getElementById("registration_button").onclick = doRegister;

	UserServiceView.prototype.showAuthenticationError = function() {
		form.classList.add("invalid");
		passwordField.classList.add("invalid");
	};
}

UserServiceView.prototype.authenticationHandler = function(username, password) {
	throw new Error("not binded");
};

UserServiceView.prototype.registrationHandler = function(username, password) {
	throw new Error("not binded");
};
