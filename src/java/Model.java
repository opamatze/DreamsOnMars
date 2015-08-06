import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Model {

    public Map<String, Resource> resources;
    public Map<String, Module>   modules;
    public Map<String, Option>   options;
    public Map<Integer, Event>    events;

    public List<Integer> event_browserOutput_stack;

    public int round;

    Model(Map<String, Resource> resources,Map<String, Module> modules, Map<String, Option> options, Map<Integer,Event> events) {

        event_browserOutput_stack = new ArrayList<>();

        this.resources = new HashMap<>(resources);
        this.modules   = new HashMap<>(modules);
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

        Module add_module = new Module(new_module);
        add_module.build_module(this);
        if(add_module.statusIndex != 2) {
            boolean inserted = false;
            int i = 0;
            while (!inserted) {
                System.out.println(modules.containsKey(add_module.name+i));
                if(!modules.containsKey(add_module.name+i)) {
                    modules.put(add_module.name + i, add_module);
                    inserted = true;
                }
                else i++;
            }
            return true;
        }
        else return false; //wird nicht gebaut
    }
    public boolean remove_module(String del_module) {

        if (modules.containsKey(del_module)) {
            modules.remove(del_module);
            return true;
        } else return false;
    }

    public void increment_round() {
        round++;
    }
    public void change_module_status() {

    }
    public String getEventByID(int id) {
        return events.get(id).info;
    }



}