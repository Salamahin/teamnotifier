
var USER_TOKEN;

function loadToken() {
  var matches = document.cookie.match(new RegExp(
    "(?:^|; )" + "userToken".replace(/([\.$?*|{}\(\)\[\]\\\/\+^])/g, '\\$1') + "=([^;]*)"
  ));
  return matches ? decodeURIComponent(matches[1]) : undefined;
}

function storeToken() {
  document.cookie = "userToken=" + USER_TOKEN;
  console.debug("token stored: " + USER_TOKEN);
}

function removeToken() {
    var cookieString = 'userToken=';
    var expiryDate = new Date();
    expiryDate.setTime(expiryDate.getTime() - 86400 * 1000);
    cookieString += ';max-age=0';
    cookieString += ';expires=' + expiryDate.toUTCString();
    document.cookie = cookieString;
}

function authenticate() {
  var authForm = document.getElementById("frm.authentication");

  var loadedToken = loadToken();
  if(loadedToken != undefined) {
    USER_TOKEN = loadedToken;
    connectStatusSocket();
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
    console.debug("authentication success");
    authForm.classList.remove("authenticationFailed");
  } else {
    console.debug("authentication failed");
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
    connectStatusSocket();
    return;
  }

  showAuthenticationResult(false);
}

function getSocketUrl() {
    var servUrl = document.URL.replace(/.*?:\/\//g, "");
    return "ws://" + servUrl + "state/?token="+USER_TOKEN;
}

function connectStatusSocket() {
    var websocket = new WebSocket(getSocketUrl());

    websocket.onopen = function(evt) {
        console.debug(evt.data);
        showAuthenticationResult(true);
        getState();
    };

    websocket.onclose = function(evt) {
        console.debug(evt.data);
    };

    websocket.onmessage = function(evt) {
        console.debug(evt.data);
        getState();
    };

    websocket.onerror = function(evt) {
        removeToken();
        showAuthenticationResult(false);
    };
}

function handleStatus(XMLHttpRequest) {
    if (XMLHttpRequest.readyState != 4)
        return;

    if(XMLHttpRequest.status == 200) {
        var status = JSON.parse(XMLHttpRequest.responseText);
        showStatus(status);
    }
}

function showStatus(status) {
    var environments = status.environments;
    var envFrame = document.getElementById("frm.environment");

    var html = "<ul>"
    environments.forEach(function(env, i, environments) {
        html += "<li>" + environmentToHtml(env) + "</li>";
    });
    html += "</ul>";

    envFrame.innerHTML = html;
}

function environmentToHtml(environment) {
    var servers = environment.servers;
    var html = "<ul>"
    servers.forEach(function(server, i, servers) {
        html += "<li>" + serverToHtml(server) + "</li>";
    });
    html += "</ul>"

    return html;
}

function serverToHtml(server) {
    var resources = server.resources;


    var serverId = "server_" + server.id;
    var html = "<ul><label><input id=\"" + serverId + "\" type=\"checkbox\"/>" + server.name + "</label>";
    resources.forEach(function(resource, i, resources) {
        html += "<li>" + resourceToHtml(resource) + "</li>";
    });
    html += "</ul>"

    return html;
}

function resourceToHtml(resource) {
    var resourceId = "resource_" + resource.id;
    var html = "<input id=\"" + resourceId + "\" type=\"button\" value=\"" + resource.name  + "\"/>";
    return html;
}

function getState() {
    var xhttp = new XMLHttpRequest();
    xhttp.open("GET", "/teamnotifier/1.0/environment", true);
    xhttp.setRequestHeader("Authorization", "Bearer " + USER_TOKEN);
    xhttp.onreadystatechange = function() {
        handleStatus(xhttp);
    };
    xhttp.send();
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
