function Notifier() {
    const that = this;
    
    this.token = "";

    Notifier.prototype.requestPermissions = function () {
        function newMessage(permission) {
            return permission == "granted";
        }

        Notification.requestPermission(newMessage);
    };

    Notifier.prototype.connect = function () {
        var websocket = new WebSocket("ws://" + location.host + "/state/?token=" + that.token);

        websocket.onopen = function () {
            that.connectionSuccessHandler();
        };

        websocket.onclose = function () {
            that.connectionCloseHandler();
        };

        websocket.onmessage = function (evt) {
            var event = JSON.parse(evt.data);
            that.eventHandler(event);
        };

        websocket.onerror = function (error) {
            that.errorHandler();
        };
    };
}

Notifier.prototype.connectionSuccessHandler = function () {
    throw new Error("not bound");
};

Notifier.prototype.connectionCloseHandler = function () {
    throw new Error("not bound");
};

Notifier.prototype.errorHandler = function (error) {
    throw new Error("not bound");
};

Notifier.prototype.eventHandler = function () {
    throw new Error("not bound");
};