function Notifier() {
    this.connectionSuccessHandler = function() {};
    this.connectionErrorHandler = function() {};
    this.connectionSuccessHandler = function() {};
    this.eventHandler = function() {};
    this.token = "";
}

Notifier.prototype = {
    constructor: Notifier;
}

Notifier.prototype.requestPermissions = function() {
    function newMessage(permission) {
        return permission == "granted";
    }
    Notification.requestPermission(newMessage);
}

Notifier.prototype.connect = function() {
    var websocket = new WebSocket("ws://" + location.host + "/state/?token=" + token);

    websocket.onopen = function () {
        connectionSuccessHandler();
    };

    websocket.onclose = function () {
        connectionCloseHandler();
    };

    websocket.onmessage = function (evt) {
        var event = JSON.parse(evt.data);
        eventHandler(info);
    };

    websocket.onerror = function () {
        connectionErrorHandler();
    };
}


