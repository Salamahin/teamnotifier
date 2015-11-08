var user="hello";
var pass="world";

var wsUri = "ws://hello:world@localhost:7998/state?credentials=Basic"+btoa(user + ":" + pass);
var status;
var environment;

window.addEventListener("load", init, false);

function init() {
  status = document.getElementById("status");
  environment = document.getElementById("environment");
  getStatus();

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
    getStatus();
  };

  websocket.onerror = function(evt) {
    console.error(evt.data)
  };
}

function getStatus() {
  var xhr = new XMLHttpRequest();
  xhr.withCredentials = true;
  xhr.open("GET", "/teamnotifier/1.0/environment", true, user, pass);
  xhr.setRequestHeader("Authorization", "Basic " + btoa(user + ":" + pass));
  xhr.onreadystatechange = showStatus;
  xhr.send();
}

function showStatus(xhr) {
//  if (xhr.readyState == 4)  {
    environment.innerHTML = xhr.responseText;
//  }
}
