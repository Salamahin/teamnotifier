
var USER_TOKEN;

function loadToken() {
  var matches = document.cookie.match(new RegExp(
    "(?:^|; )" + "userToken".replace(/([\.$?*|{}\(\)\[\]\\\/\+^])/g, '\\$1') + "=([^;]*)"
  ));
  return matches ? decodeURIComponent(matches[1]) : undefined;
}

function storeToken() {
  document.cookie = "userToken="+USER_TOKEN;
  console.debug("token stored: " + USER_TOKEN);
}

function authenticate() {
  var authForm = document.getElementById("frm.authentication");

  if(loadToken() != undefined) {
    return;
  }

  var authenticate = document.getElementById("btn.authenticate");
  authenticate.onclick = function() {
    sendAuthRequest();
  };
}

function sendAuthRequest() {
  var username = document.getElementById("ibox.username").value;
  var password = document.getElementById("ibox.password").value;

  var xhttp = new XMLHttpRequest();
  xhttp.open("GET", "/teamnotifier/1.0/users/authenticate", true);
  xhttp.setRequestHeader("Authorization", "Basic " + btoa(username + ":" + password));
  xhttp.onreadystatechange = function() {
    handleAuthentication(xhttp);
  };
  xhttp.send();
}

function showAuthenticationResult(success) {
  var authForm = document.getElementById("frm.authentication");

  if(success) {
    authForm.classList.remove("authenticationFailed");
  } else {
    authForm.classList.add("authenticationFailed");
  }
}

function handleAuthentication(XMLHttpRequest) {
  if (XMLHttpRequest.readyState != 4)
    return;

  if(XMLHttpRequest.status == 200) {
    var authInfo = JSON.parse(XMLHttpRequest.responseText);
    USER_TOKEN = authInfo.token;
    storeToken();
    showAuthenticationResult(true);
    return;
  }

  showAuthenticationResult(false);
}

window.onload = function() {
  authenticate();

//  status = document.getElementById("status");
//  environment = document.getElementById("environment");
//  hist = document.getElementById("hist");
//
//  getStatus();
//
//  var websocket = new WebSocket(wsUri);
//
//  websocket.onopen = function(evt) {
//    console.debug(evt.data);
//  };
//
//  websocket.onclose = function(evt) {
//    console.debug(evt.data);
//  };
//
//  websocket.onmessage = function(evt) {
//    console.debug(evt.data);
//    status.innerHTML = evt.data;
//    getStatus();
//  };
//
//  websocket.onerror = function(evt) {
//    console.error(evt.data)
//  };
//
//
//  var subscribe = document.getElementById("subscribe");
//  var unsubscribe = document.getElementById("unsubscribe");
//  var reserve = document.getElementById("reserve");
//  var free = document.getElementById("free");
//  var input = document.getElementById("input");
//
//  subscribe.onclick = function() {
//    var xhr = new XMLHttpRequest();
//    xhr.withCredentials = true;
//    xhr.open("POST", "/teamnotifier/1.0/environment/server/subscribe/" + input.value, true);
//    xhr.setRequestHeader("Authorization", "Basic " + btoa(user + ":" + pass));
//    xhr.onreadystatechange = function() {reaskStatus(xhr);};
//    xhr.send();
//  }
//
//  unsubscribe.onclick = function() {
//    var xhr = new XMLHttpRequest();
//    xhr.withCredentials = true;
//    xhr.open("DELETE", "/teamnotifier/1.0/environment/server/subscribe/" + input.value, true);
//    xhr.setRequestHeader("Authorization", "Basic " + btoa(user + ":" + pass));
//    xhr.onreadystatechange = function() {reaskStatus(xhr);};
//    xhr.send();
//  }
//
//  reserve.onclick = function() {
//    var xhr = new XMLHttpRequest();
//    xhr.withCredentials = true;
//    xhr.open("POST", "/teamnotifier/1.0/environment/application/reserve/" + input.value, true);
//    xhr.setRequestHeader("Authorization", "Basic " + btoa(user + ":" + pass));
//    xhr.onreadystatechange = function() {reaskStatus(xhr);};
//    xhr.send();
//  }
//
//  free.onclick = function() {
//    var xhr = new XMLHttpRequest();
//    xhr.withCredentials = true;
//    xhr.open("DELETE", "/teamnotifier/1.0/environment/application/reserve/" + input.value, true);
//    xhr.setRequestHeader("Authorization", "Basic " + btoa(user + ":" + pass));
//    xhr.onreadystatechange = function() {reaskStatus(xhr);};
//    xhr.send();
//  }
//
//
//  var action_resource_id = document.getElementById("action_resource_id");
//  var action_descr = document.getElementById("action_descr");
//  var push_action = document.getElementById("push_action");
//
//  push_action.onclick = function() {
//    var xhr = new XMLHttpRequest();
//    xhr.withCredentials = true;
//    xhr.open("POST", "/teamnotifier/1.0/environment/application/actions/" + action_resource_id.value, true);
//    xhr.setRequestHeader("Authorization", "Basic " + btoa(user + ":" + pass));
//    xhr.setRequestHeader("ActionDetails", action_descr.value)
//    xhr.send();
//  }
//
//  var action_hist_from = document.getElementById("action_hist_from");
//  var action_hist_to = document.getElementById("action_hist_to");
//  var get_actions = document.getElementById("get_actions");
//
//  get_actions.onclick = function() {
//    var xhr = new XMLHttpRequest();
//    xhr.withCredentials = true;
//    xhr.open("GET", "/teamnotifier/1.0/environment/application/actions/" + action_resource_id.value, true);
//    xhr.setRequestHeader("Authorization", "Basic " + btoa(user + ":" + pass));
//    xhr.setRequestHeader("ActionsFrom", btoa(action_hist_from.value))
//    xhr.setRequestHeader("ActionsTo", btoa(action_hist_to.value))
//    xhr.onreadystatechange = function() {
//      if (xhr.readyState == 4) {
//        hist.innerHTML=xhr.responseText;
//      }
//    };
//    xhr.send();
//  }
}
