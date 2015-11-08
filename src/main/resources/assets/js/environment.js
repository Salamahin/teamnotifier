function createEnvironmentTable(environmentJson) {
  var tbl     = document.createElement("table");
  var env = parseEnvironmens(environmentJson);

}

function parseEnvironmens(environmentJson) {
  return JSON.parse(environmentJson);
}