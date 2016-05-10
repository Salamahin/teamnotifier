function Storage() {
    var tokenKey = "teamnotifier_token";

    this.token = localStorage.getItem(tokenKey);

    Storage.prototype.store = function (token) {
        localStorage.setItem(tokenKey, token);

        Storage.prototype.token = token;
    };

}
