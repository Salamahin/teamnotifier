function Storage() {
    this.tokenKey = "teamnotifier_token";
    this.loginKey = "teamnotifier_login";

    this.token = localstorage.getItem(tokenKey);
    this.login = localstorage.getItem(loginKey);
}

Storage.prototype = {
    constructor: Storage;
}

Storage.prototype.store = function(login, token) {
    localstorage.setItem(tokenKey, token);
    localstorage.setItem(loginKey, login);

    this.token = token;
    this.login = login;
}
