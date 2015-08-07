//package java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Model {

    public Map<String, Resource> resources;
    public Map<String, Module>   modules;
    public Map<String, Option>   options;
    public Map<Integer, Event>    events;
    public Map<String, Settler>   settler;

    public List<Integer> event_browserOutput_stack;

    public int round;

    Model(Map<String, Resource> resources,Map<String, Module> modules, Map<String,Settler> settler,
            Map<String, Option> options, Map<Integer,Event> events) {

        event_browserOutput_stack = new ArrayList<>();

        this.resources = new HashMap<>(resources);
        this.modules   = new HashMap<>(modules);
        for(Map.Entry<String,Module> entry : modules.entrySet()) {
            entry.getValue().build_module(this);
            entry.getValue().id = entry.getKey();
        }
        this.settler   = new HashMap<>(settler);
        this.options   = new HashMap<>(options);
        this.events    = new HashMap<>(events);
        round     = 1;

        System.out.print("model constructor aufgerufen: ");
    }
        //addiere/subtrahiere resourcen
    public void update_resources(Map<String,Double> changes) {

        for(Map.Entry<String,Double> update : changes.entrySet()) {
            if(resources.containsKey(update.getKey())) {
                resources.get(update.getKey()).amount += update.getValue();
                if (resources.get(update.getKey()).amount < 0) {
                    System.out.print("eine resource ist kleiner null, ungültiger spielzustand");
                    System.exit(1);
                }
            }
        }

    }
        //wenn eines der resourcen nicht vorhanden, dann false
    public boolean check_stock(Map<String,Double> res) {
        for(Map.Entry<String,Double> having : res.entrySet()) {
            //wenn es die resource gibt (z.b. ist "time" unbegrenzt)
            if(resources.containsKey(having.getKey())) {
                if (resources.get(having.getKey()).amount + having.getValue() < 0) {
                    System.out.println(resources.get(having.getKey()).amount + " kleiner " + having.getValue());
                    return false;
                }
            }
        }
        return true;
    }
    public void add_option() {}
    public boolean add_module(String new_module) {

        Module newModule = new Module(new_module);
            //buy if enough resources
        newModule.build_module(this);
            //if successful (other than 2 = forget new module)
        if(newModule.statusIndex != 2) {
            int i = 0;
            while (true) {
                if(!modules.containsKey(newModule.name+i)) {
                    newModule.id = newModule.name+i;
                    modules.put(newModule.name + i, newModule);
                    return true;
                }
                else i++;

                if(i > 10000) {
                System.out.println("Ein Fehler ist in add_module aufgetreten");
                System.exit(1);
                }
            }
        }
        else return false; //wird nicht gebaut
    }
    public boolean add_settler() {
        Settler newSettler = new Settler();
        Module newModule = new Module("settler"); //jeder Settler hat ein Modul mit gleicher id
        newModule.build_module(this);
        int i = 1;
        while(true) {
            if(!settler.containsKey(newSettler.name + i)) {
                newSettler.id = newSettler.name + i;
                newModule.id  = newSettler.id;
                settler.put(newSettler.name + i, newSettler);
                modules.put(newSettler.name + i, newModule);
                return true;
            }
            else i++;

            if(i > 10000) {
                 System.out.println("Ein Fehler ist in add_settler aufgetreten");
                 System.exit(1);
             }
        }

    }
    public boolean remove_module(String del_module) {

        if (modules.containsKey(del_module)) {
            modules.remove(del_module);
            return true;
        } else return false;
    }
    public boolean remove_settler(String id) {
        if(settler.isEmpty()) {
            System.out.println("Keine Siedler mehr da");
            System.exit(1);
        }
            //remove random settler and take any id
        if(id.equals("settler")) {
            String arbKey = ""; //arbetrery key
            for(Map.Entry<String,Settler> entry : settler.entrySet()) {
                arbKey = entry.getKey();
                break; //end foreach after first element
            }
            id = arbKey; //override
        }

            //modul entfernen
        if(modules.containsKey(id)) {
            modules.get(id).statusIndex = 2; //ready for delete
        }
        //free module
        for(Map.Entry<String,Module> entry : modules.entrySet()) {
            Module module = entry.getValue();
            if(module.assignedTo.equals(id)) {
                module.unassign(this);
            }
        }
        //siedler entfernen
        if(settler.containsKey(id)) {
            settler.remove(id);
            return false;
        }

        System.out.println("settlerid: "+id+" existiert nicht, aber alle möglichen Überreste wurden entfernt.");
        return false;
    }
    public void garbageCollectorModules() {
        for(Map.Entry<String,Module> entry : modules.entrySet()) {
            Module module = entry.getValue();
            if(module.statusIndex == 2) modules.remove(module.id);
        }

    }
    public void increment_round() {
        round++;
    }

    public String getEventByID(int id) {
        return events.get(id).info;
    }



}