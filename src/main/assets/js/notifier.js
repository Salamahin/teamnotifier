function Notifier() {
    this.connectionSuccessHandler = function() {};
    this.connectionErrorHandler = function() {};
    this.connectionSuccessHandler = function() {};
}

Notifier.prototype.requestPermissions = function() {
    function newMessage(permission) {
        return permission == "granted";
    }
    Notification.requestPermission(newMessage);
}

Notifier.prototype.setConnectionCloseHandler() = function(handler) {
    this.connectionCloseHandler = handler;
}

Notifier.prototype.setConnectionErrorHandler() = function(handler) {
    this.connectionErrorHandler = handler;
}

Notifier.prototype.setConnectionSuccessHandler() = function(handler) {
    this.connectionSuccessHandler = handler;
}

Notifier.prototype.setConnectionCloseHandler() = function(handler) {
    this.connectionCloseHandler = handler;
}

Notifier.prototype.connect = function(token) {
    var websocket = new WebSocket(getSocketUrl());

    websocket.onopen = function () {
        connectionSuccessHandler();
    };

    websocket.onclose = function () {
        connectionCloseHandler();
    };

    websocket.onmessage = function (evt) {
        var info = JSON.parse(evt.data);
        getState();

        if(info.event == "SUBSCRIBE" || info.event == "UNSUBSCRIBE")
         return;

        showNotification(JSON.parse(evt.data));
    };

    websocket.onerror = function () {
        connectionErrorHandler();
    };
}


