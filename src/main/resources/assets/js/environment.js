var wsUri = "ws://hello:world@localhost:7998/state?credentials=Basic"+btoa("hello:world");
var status;

window.addEventListener("load", init, false);

function init() {
  status = document.getElementById("status");

  var websocket = new WebSocket(wsUri);

  websocket.onopen = function(evt) {
    console.debug(evt.data);
  };

  websocket.onclose = function(evt) {
    console.debug(evt.data);
  };

  websocket.onmessage = function(evt) {
    console.debug(evt.data);
    status.innerHTML = evt.data;
  };

  websocket.onerror = function(evt) {
    console.error(evt.data)
  };
}

