onload = function()
{
    $("#register").hide();
    $("#loggedin").hide();
    $("#showregister").click(function (){
       $("#register").show();
       $("#showregister").hide();
    });
    updateList();
    $("#submit").click(submitUser);
    $("#submitlogin").click(login);
};
function getUserId(name)
{
    var id = null;
    var request = new XMLHttpRequest();
    request.open("GET", "http://localhost:8080/webappsproject/api/users");
    request.onload = function() {
        if (request.status === 200) {
            var users = JSON.parse(request.responseText);
            for (var i = 0; i < users.length; i++) {
                if (users[i].name) {
                    getritten(users[i].id);
                }
                
            }
           
        } else {
            $("#error").text("Unable to login wrong username");
        }
    };
    request.send(null);
}
function updateList()
{
    var request = new XMLHttpRequest();
    request.open("GET", "http://localhost:8080/webappsproject/api/users");
    request.onload = function() {
        if (request.status === 200) {
            $("#users").empty();
            var users = JSON.parse(request.responseText);
            for (var i = 0; i < users.length; i++) {
                var item = $("<li>");
                if (users[i].name) {
                    item.text(users[i].name + " (" + users[i].email + ")");
                } else {
                    item.text(users[i].email);
                }
                $("#users").append(item);
            }
            $("#error").empty();
        } else {
            $("#error").text("Unable to load user list");
        }
    };
    request.send(null);
}
function login(){
    var username = $("#usernamelogin").val();
var password = $("#passwordlogin").val();  

getUserId(username);


}
function getritten(id){
    var username = $("#usernamelogin").val();
var password = $("#passwordlogin").val();  
    var request = new XMLHttpRequest();
    
    request.open("GET", "http://localhost:8080/webappsproject/api/users/" + id + "/ritten", true);
    request.setRequestHeader("Authorization", "Basic " + window.btoa(username + ":" + password));
    request.onload = function() {
        if (request.status === 200) {
            $("#ritten").empty();
            var ritten = JSON.parse(request.responseText);
            for (var i = 0; i < ritten.length; i++) {
                var item = $("<li>");
                item.text(ritten[i].title + " (" + ritten[i].afstand + (")"));
                $("#ritten").append(item);
            }
            $("#error").empty();
        } else {
            $("#error").text("Unable to login to server with " + id + username + password);
        }
    };
    request.send(null);
}

function submitUser()
{
    var user = {};
    user.name = $("#username").val();
    user.password = $("#password").val();
    user.email = $("#email").val();
    
    var request = new XMLHttpRequest();
    request.open("POST", "http://localhost:8080/webappsproject/api/users");
    request.onload = function() {
        if (request.status === 201) {
            $("#error").empty();
            updateList();
            $("#showregister").show();
            $("#register").hide();
        } else {
            $("#error").text("Unable to add user");
        }
    };
    request.setRequestHeader("Content-Type", "application/json");
    request.send(JSON.stringify(user));
}