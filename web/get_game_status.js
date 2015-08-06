var time_interval;
var username = "luise";
var building = "";

function pull_new_status(new_username)
{
    username = new_username;
    $.post("http://localhost:8080/DreamsOnMars/webgate",
            {"username":username,"new_module":building},
            function(data,status) {
            try{//subData = data.substring(2);
               // $("#gameStatus").html(data);
                    var myJson = $.parseJSON(data);
                $("#gameStatus").html(myJson.resources[0].food);    }
                catch (Exception) {$("#gameEvents").html(Exception);}
               // myJson = JSON.parse
               // try {myJson = jQuery.parseJSON(data);} catch (Exception) {$("#gameEvents").html(Exception);}
               // $("#gameEvents").html(data.resources.food);
            building = ""; //reset

            })
       //     .done(function() { alert("done");})
            .fail(function() { $("#gameEvents").append("Fail - keine Verbindung zum Server")});


}

$(document).ready(function(){


    time_interval = setInterval(function(){
        if(username != "") pull_new_status(username);
    },3000)
})