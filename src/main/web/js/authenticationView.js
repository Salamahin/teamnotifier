function AuthenticationView() {
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
		form.classList.remove("invalid");
		
		var username = usernameField.value;
		var password = passwordField.value;

		that.registrationHandler(username, password);
		resetPasswordText();
	}

	passwordField.onkeypress = function(e) {
		passwordField.classList.remove("invalid");

		if(e.which == 13) 
			doAuthenticate();
	};

	document.getElementById("login_button").onclick = doAuthenticate;
	document.getElementById("registration_button").onclick = doRegister;

	AuthenticationView.prototype.showAuthenticationError = function() {
		form.classList.add("invalid");
		passwordField.classList.add("invalid");
	};
}

AuthenticationView.prototype.authenticationHandler = function(username, password) {
	throw new Error("not binded");
};

AuthenticationView.prototype.registrationHandler = function(username, password) {
	throw new Error("not binded");
};
