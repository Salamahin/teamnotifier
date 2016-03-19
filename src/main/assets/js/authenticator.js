function Authenticator() {
    this.authenticationSuccessHandler = function(login, token) {};
    this.authenticationErrorHandler = function() {};
}

Authenticator.prototype.authenticate = function(login, password) {
    function handleAuthentication(xhttp) {
         if (xhttp.readyState != 4)
            return;

        if (xhttp.status == 200) {
            var authInfo = JSON.parse(xhttp.responseText);
            authenticationSuccessHandler(login, authInfo.token);
            return;
        }

        authenticationErrorHandler();
    }

    var xhttp = new XMLHttpRequest();
    xhttp.open("GET", "/teamnotifier/1.0/users/authenticate", true);
    xhttp.setRequestHeader("Authorization", "x-Basic " + btoa(login+ ":" + password));
    xhttp.onreadystatechange = function () {
        handleAuthentication(xhttp);
    };
    xhttp.send();
};