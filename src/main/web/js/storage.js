function Storage() {
    var tokenKey = "teamnotifier_token";
    var loginKey = "teamnotifier_login";

    this.token = localStorage.getItem(tokenKey);
    this.login = localStorage.getItem(loginKey);

    Storage.prototype.store = function (login, token) {
        localStorage.setItem(tokenKey, token);
        localStorage.setItem(loginKey, login);

        Storage.prototype.token = token;
        Storage.prototype.login = login;
    };

}
