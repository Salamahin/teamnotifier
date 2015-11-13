window.onload = function() {

  var button = document.getElementById("submit");
    var token = document.getElementById("token");

  button.onclick = function() {

    var user = document.getElementById("login").value;
    var pass = document.getElementById("password").value;

    var xhr = new XMLHttpRequest();
    xhr.withCredentials = true;
    xhr.open("POST", "/teamnotifier/1.0/users/authenticate", true, user, pass);
    xhr.setRequestHeader("Authorization", "Basic " + btoa(user + ":" + pass));
    xhr.onreadystatechange = function() {
      if (xhr.readyState == 4) {
        token.innerHTML=xhr.responseText;
      }
    };
    xhr.send();
  }
}