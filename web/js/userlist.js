onload = function()
{
    updateList();
    $("#submit").click(submitUser);
};

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
        } else {
            $("#error").text("Unable to add user");
        }
    };
    request.setRequestHeader("Content-Type", "application/json");
    request.send(JSON.stringify(user));
}