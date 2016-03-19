function Authenticator() {
    this.authenticationSuccessHandler = function(login, token) {};
    this.authenticationErrorHandler = function() {};
    this.whoAmHandler = function(xhttp) {};
}

Authenticator.prototype = {
    constructor: Authenticator;
}

Authenticator.prototype.whoAmI = function(token) {
    var xhttp = new XMLHttpRequest();
    xhttp.open("GET", "/teamnotifier/1.0/users/whoami", true);
    xhttp.setRequestHeader("Authorization", "Bearer " + token);
    xhttp.onreadystatechange = function () {
        whoAmHandler(xhttp);
    };
    xhttp.send();
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
    xhttp.setRequestHeader("Authorization", "x-Basic " + btoa(username + ":" + password));
    xhttp.onreadystatechange = function () {
        handleAuthentication(xhttp);
    };
    xhttp.send();
}