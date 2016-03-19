function Storage() {
    this.tokenKey = "teamnotifier_token";
    this.loginKey = "teamnotifier_login";

    this.token = localstorage.getItem(this.tokenKey);
    this.login = localstorage.getItem(this.loginKey);
}

Storage.prototype.store = function(login, token) {
    localstorage.setItem(this.tokenKey, token);
    localstorage.setItem(this.loginKey, login);

    this.token = token;
    this.login = login;
};
