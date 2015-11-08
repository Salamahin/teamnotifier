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
  //document.write(theUrl);
  var xmlHttp = null;
  xmlHttp = new XMLHttpRequest();
  xmlHttp.open("GET", "/teamnotifier/1.0/environment", true);
  xmlHttp.setRequestHeader("Authorization", "Basic " + btoa(user + ":" + pass));
  xmlHttp.onreadystatechange = handleReadyStateChange;
  xmlHttp.send(null);

  function handleReadyStateChange() {
    if (xmlHttp.readyState == 4) {
      if (xmlHttp.status == 200) {
        document.getElementById("environment").innerHTML=xmlHttp.responseText;
      }
    }
  }
}
