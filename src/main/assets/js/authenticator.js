function Authenticator() {
    const that = this;
    
    function handleAuthentication(xhttp) {
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
            handleAuthentication(xhttp);
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