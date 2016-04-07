function Authenticator() {
    const that = this;
    
    function handleAuthentication(login, xhttp) {
        if (xhttp.readyState != 4)
            return;

        if (xhttp.status == 200) {
            var authInfo = JSON.parse(xhttp.responseText);
            that.authenticationSuccessHandler(login, authInfo.token);
            return;
        }

        that.authenticationErrorHandler();
    }
    
    Authenticator.prototype.authenticate = function (login, password) {
        var xhttp = new XMLHttpRequest();
        xhttp.open("GET", "/teamnotifier/1.0/users/authenticate", true);
        xhttp.setRequestHeader("Authorization", "x-Basic " + btoa(login + ":" + password));
        xhttp.onreadystatechange = function () {
            handleAuthentication(login, xhttp);
        };
        xhttp.send();
    };

    function handleRegistration(xhttp, login, password) {
        if(xhttp.readyState != 4)
            return;

        if(xhttp.status == 204) {
            that.authenticate(login, password);
            return;
        }

        that.registrationErrorHandler();
    }
    
    Authenticator.prototype.register = function (login, password) {
        var xhttp = new XMLHttpRequest();
        xhttp.open("POST", "/teamnotifier/1.0/users/register", true);
        xhttp.setRequestHeader("Authorization", "Basic " + btoa(login + ":" + password));
        xhttp.onreadystatechange = function () {
            handleRegistration(xhttp, login, password);
        };
        xhttp.send();
    };
}

Authenticator.prototype.authenticationSuccessHandler = function (login, token) {
    throw new Error("not bound");
};

Authenticator.prototype.authenticationErrorHandler = function () {
    throw new Error("not bound");
};

Authenticator.prototype.registrationErrorHandler = function () {
    throw new Error("not bound");
};