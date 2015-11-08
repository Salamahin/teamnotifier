var user="hello";
var pass="world";

var wsUri = "ws://hello:world@localhost:7998/state?credentials=Basic"+btoa(user + ":" + pass);
var status;
var environment;

function getStatus() {
  var xmlHttp = null;
  xmlHttp = new XMLHttpRequest();
  xmlHttp.withCredentials = true;
  xmlHttp.open("GET", "/teamnotifier/1.0/environment", true);
  xmlHttp.setRequestHeader("Authorization", "Basic " + btoa(user + ":" + pass));
  xmlHttp.onreadystatechange = function() {
    if (xmlHttp.readyState == 4) {
      environment.innerHTML=xmlHttp.responseText;
    }
  };
  xmlHttp.send(null);
}

function reaskStatus(xmlHttp) {
  if (xmlHttp.readyState == 4) {
    getStatus();
  }
}

window.onload = function() {
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


  var subscribe = document.getElementById("subscribe");
  var unsubscribe = document.getElementById("unsubscribe");
  var reserve = document.getElementById("reserve");
  var free = document.getElementById("free");
  var input = document.getElementById("input");

  subscribe.onclick = function() {
    var xhr = new XMLHttpRequest();
    xhr.withCredentials = true;
    xhr.open("POST", "/teamnotifier/1.0/environment/server/subscribe/" + input.value, true);
    xhr.setRequestHeader("Authorization", "Basic " + btoa(user + ":" + pass));
    xhr.onreadystatechange = function() {reaskStatus(xhr);};
    xhr.send();
  }

  unsubscribe.onclick = function() {
    var xhr = new XMLHttpRequest();
    xhr.withCredentials = true;
    xhr.open("DELETE", "/teamnotifier/1.0/environment/server/subscribe/" + input.value, true);
    xhr.setRequestHeader("Authorization", "Basic " + btoa(user + ":" + pass));
    xhr.onreadystatechange = function() {reaskStatus(xhr);};
    xhr.send();
  }

  reserve.onclick = function() {
    var xhr = new XMLHttpRequest();
    xhr.withCredentials = true;
    xhr.open("POST", "/teamnotifier/1.0/environment/application/reserve/" + input.value, true);
    xhr.setRequestHeader("Authorization", "Basic " + btoa(user + ":" + pass));
    xhr.onreadystatechange = function() {reaskStatus(xhr);};
    xhr.send();
  }

  free.onclick = function() {
    var xhr = new XMLHttpRequest();
    xhr.withCredentials = true;
    xhr.open("DELETE", "/teamnotifier/1.0/environment/application/reserve/" + input.value, true);
    xhr.setRequestHeader("Authorization", "Basic " + btoa(user + ":" + pass));
    xhr.onreadystatechange = function() {reaskStatus(xhr);};
    xhr.send();
  }
}
