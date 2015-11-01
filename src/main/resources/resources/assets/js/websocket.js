var wsUri = "ws://localhost:7998/ws";
var timevalue;

window.addEventListener("load", init, false);

function init() {
  timevalue = document.getElementById("timevalue");

  var websocket = new WebSocket(wsUri);

  websocket.onopen = function(evt) {
    console.debug(evt.data);
  };

  websocket.onclose = function(evt) {
    console.debug(evt.data);
  };

  websocket.onmessage = function(evt) {
    console.debug(evt.data);
    timevalue.innerHTML = evt.data;
  };

  websocket.onerror = function(evt) {
    console.error(evt.data)
  };
}

