var time_interval;
var username = "luise";
var building = "";
var change_assignment = "";

function pull_new_status(new_username)
{
    username = new_username;
        //Zeile für opamatze
    //    $.post("http://localhost:8080/DreamsOnMars/webgate",
        //Zeile für simi
    $.post("http://localhost:8088/webgate",
            {"username":username,"new_module":building,"change_assignment":change_assignment},

            function(data,status) {
            try {
                    //local vars
                var myJson = $.parseJSON(data);
                var resources = myJson.resources[0];
                var events    = myJson.gameEvents;
                var modules   = myJson.modules[0];
                var options   = myJson.gameOptions[0];
                var amount_settlers = 0;
                var amount_settlers_food = 0;

                    //clear all container
                $("#event_container").html("");
                $("#resource_container").html("");
                $("#module_container").html("");
                $("#stats_container").html("");
                $("#debug").html("");
                //  $("#debug").html(data);

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
                    var module = obj[0];
                    if(module.name == "settler") {
                        amount_settlers++;
                        amount_settlers_food += module.uses.food;
                        return true; //same as continue
                    }
                        //buttons
                    if(module.statusIndex == 0) {
                        $("#module_container").append("<button class='btn btn-sm btn-default' " +
                            "type='button' id ='" + module.id + "' title='assign it'> Off </button>  ");
                        $("#" + module.id).click(function () {
                            this.blur();
                            change_assignment = "assign!" + module.id;
                        });
                    }
                    else if(module.statusIndex == 1 && module.automatic == false) {
                        $("#module_container").append("<button class='btn btn-sm btn-success'" +
                            "type='button' id ='" + module.id + "' title='unassign it'> OK </button>  ");
                        $("#" + module.id).click(function () {
                            this.blur();
                            change_assignment = "unassign!" + module.id;
                        });
                    }
                    else if(module.statusIndex == 1 && module.automatic == true) {
                        $("#module_container").append("<button class='btn btn-sm btn-primary'" +
                            "type='button' id ='" + module.id + "' title='works automatic'> AUTO </button>  ");
                    }
                    else if(module.statusIndex == 3) {
                        $("#module_container").append("<button class='btn btn-sm btn-warning'" +
                            "type='button' id ='" + module.id + "' title='under construction'> "+
                                module.constrRnds+" </button>  ");
                    }
                        //text
                    $("#module_container").append(module.name+"("+module.status+") | uses(");
                    var first = true;
                    $.each (module.uses, function(item, value) {
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
                    $.each (module.produces, function(item, value) {
                        if(first) {
                            $("#module_container").append(item+": "+value);
                            first = false;
                        }
                        else {
                            $("#module_container").append(","+item+": "+value);
                        }
                    });
                    $("#module_container").append(") assTo: "+module.assignedTo+"<br>");
                });

                    //write statistics
                $("#stats_container").append("STATISTICS: <br>" +
                        " Settlers total: "+amount_settlers+
                        " consume "+Math.abs(amount_settlers_food)+" food<br>");

                    //write
                $("#user_container").html("Baue neue Module: ");
                $.each(options, function(item,obj) {
                    var option = obj;
                        //available mit clickevent
                    if(option.available) {
                    $("#user_container").append("<button class='btn btn-sm btn-primary' " +
                        "type='button' id ='" + option.name + "' title='"+option.title+"'> "+option.label+" </button>");
                    $("#"+option.name).click(function() {
                        blur();
                        building = option.name;
                        });
                    }
                        //ohne event und ausgegraut
                    else {
                        $("#user_container").append("<button class='btn btn-sm btn-default' style='opacity:.4;' " +
                            "type='button' id ='" + option.name + "' title='"+option.title+"'> "+option.label+" </button>");
                    }

                });

            } catch (Exception) {$("#gameEvents").html(Exception);}

            building = ""; //reset
            change_assignment = ""; // reset

            })
           // .done(function() { $("#debug").html("")})
            .fail(function() { $("#debug").html("Fail - keine Verbindung zum Server")});


}

$(document).ready(function(){


    time_interval = setInterval(function(){
        if(username != "") pull_new_status(username);
    },3000)
})