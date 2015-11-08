window.onload = function() {

  var button = document.getElementById("submit");

  button.onclick = function() {

    var user = document.getElementById("login").value;
    var pass = document.getElementById("password").value;

    var xhr = new XMLHttpRequest();
    xhr.withCredentials = true;
    xhr.open("POST", "/teamnotifier/1.0/environment/users/register", true, user, pass);
    xhr.setRequestHeader("Authorization", "Basic " + btoa(user + ":" + pass));
    xhr.send();
  }
}