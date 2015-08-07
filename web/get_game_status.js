var time_interval;
var username = "luise";
var building = "";
var change_assignment = "";

function pull_new_status(new_username)
{
    username = new_username;
    $.post("http://localhost:8080/DreamOnMars/webgate",
            {"username":username,"new_module":building,"change_assignment":change_assignment},

            function(data,status) {
            try {//subData = data.substring(2);
               // $("#debug").html(data);
                    //clear all container
                $("#event_container").html("");
                $("#resource_container").html("");
                $("#module_container").html("");
                var myJson = $.parseJSON(data);
                var resources = myJson.resources[0];
                var events    = myJson.gameEvents;
                var modules   = myJson.modules[0];

                    //write resources
                $("#resource_container").append("<b>RESOURCEN:</b><br>")
                $.each( resources, function( item, value ) {
                    $("#resource_container").append(item+": "+value+"<br>");
                });
                    //write events
                $("#event_container").append("<b>EREIGNISSE:</b><br>")
                $.each( events, function( index, obj) {
                    $.each (events[index], function(key, value) {
                        $("#event_container").append(value+"<br>");
                    })
                });

                    //write modules
                $("#module_container").append("<b>MODULE:</b><br>")
                $.each( modules, function( index, obj) {
                    var modul = obj[0];
                        //buttons
                    if(modul.statusIndex == 0) {
                        $("#module_container").append("<button class='btn btn-sm btn-default' " +
                            "type='button' id ='" + modul.id + "' title='assign it'> Off </button>  ");
                        $("#" + modul.id).click(function () {
                            this.blur();
                            change_assignment = "assign!" + modul.id;
                        });
                    }
                    else if(modul.statusIndex == 1 && modul.automatic == false) {
                        $("#module_container").append("<button class='btn btn-sm btn-success'" +
                            "type='button' id ='" + modul.id + "' title='unassign it'> OK </button>  ");
                        $("#" + modul.id).click(function () {
                            this.blur();
                            change_assignment = "unassign!" + modul.id;
                        });
                    }
                    else if(modul.statusIndex == 1 && modul.automatic == true) {
                        $("#module_container").append("<button class='btn btn-sm btn-primary'" +
                            "type='button' id ='" + modul.id + "' title='works automatic'> AUTO </button>  ");
                    }
                    else if(modul.statusIndex == 3) {
                        $("#module_container").append("<button class='btn btn-sm btn-warning'" +
                            "type='button' id ='" + modul.id + "' title='under construction'> "+
                                modul.constrRnds+" </button>  ");
                    }
                        //text
                    $("#module_container").append(modul.name+"("+modul.status+") | uses(");
                    var first = true;
                    $.each (modul.uses, function(item, value) {
                        if(first) {
                            $("#module_container").append(item+": "+value);
                            first = false;
                        }
                        else {
                            $("#module_container").append(","+item+": "+value);
                        }
                    });
                    $("#module_container").append(") produces(");
                    first = true;
                    $.each (modul.produces, function(item, value) {
                        if(first) {
                            $("#module_container").append(item+": "+value);
                            first = false;
                        }
                        else {
                            $("#module_container").append(","+item+": "+value);
                        }
                    });
                    $("#module_container").append(") assTo: "+modul.assignedTo+"<br>");
                });



            } catch (Exception) {$("#gameEvents").html(Exception);}
               // myJson = JSON.parse
               // try {myJson = jQuery.parseJSON(data);} catch (Exception) {$("#gameEvents").html(Exception);}
               // $("#gameEvents").html(data.resources.food);
            building = ""; //reset

            })
       //     .done(function() { alert("done");})
            .fail(function() { $("#debug").html("Fail - keine Verbindung zum Server")});


}

$(document).ready(function(){


    time_interval = setInterval(function(){
        if(username != "") pull_new_status(username);
    },2000)
})