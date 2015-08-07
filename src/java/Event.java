//package java;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
public class Event {

    public int id;
    public String info;
    public double chance;
    public int time_interval;
    public int min_rounds_passed; //before event can happen
    public Map<String, Double> effect_on_resources;
    public Map<String, Integer> effect_on_modules; //1 = neues Modul, 2 = Modul entfernen
    public Map<String, Integer> effect_on_settler; //1 = neuer Siedler 2 = Siedler muss sterben

    Event() {
        System.out.println("Event() constructor is called");
    }

    Event(int id,String info, double chance, int ti, int mrp) {

        effect_on_modules = new HashMap<>();
        effect_on_resources = new HashMap<>();
        effect_on_settler = new HashMap<>();

        this.id = id;
        this.info = info;
        this.chance = chance;
        this.time_interval = ti;
        this.min_rounds_passed = mrp;

        System.out.print("Event(blabla) constructor is called");
    }

    public void add_effect(String type, String key, Double value) {
        System.out.println("add effect");
        if(type == "resource") effect_on_resources.put(key,value);
        else {}
        if(type == "module") effect_on_modules.put(key,value.intValue());
        else {}
        if(type == "settler") effect_on_settler.put(key,value.intValue());
        else {}
    }
    public void pick_event_by_stack(Model state, List<Integer> event_stack) {

        if(event_stack != null) {
            for (Integer pick : event_stack) {
                if(pick != 0) state.event_browserOutput_stack.add(pick);
            }
            event_stack.clear();
        }
    }
    public void pick_event_by_chance(Model state) {
        double coin;
        for( Map.Entry<Integer,Event> ev : state.events.entrySet()) {
            if(ev.getKey() > 300) {
                coin = Math.random();
                try {
                    if(coin < ev.getValue().chance &&
                    state.round > ev.getValue().min_rounds_passed &&
                        state.round % ev.getValue().time_interval == 0) {
                    trigger_event(state, ev.getKey());
                } }
                catch (Exception e) { System.out.println(" fehler aufgetreten"); }
            }
        }
    }
    private void trigger_event(Model state,int event_id) {

            //verändere resourcen
        if(!state.events.get(event_id).effect_on_resources.isEmpty()) {
            if(state.check_stock(state.events.get(event_id).effect_on_resources))
                state.update_resources(state.events.get(event_id).effect_on_resources);
            else {} //zu wenig Resourcen für Ereignis
        }
            //verändere module
        if(!state.events.get(event_id).effect_on_modules.isEmpty()) {
            for(Map.Entry<String,Integer> eom : state.events.get(event_id).effect_on_modules.entrySet()) {
                if (eom.getValue() == 1) state.add_module(eom.getKey());
                if (eom.getValue() == 2) state.remove_module(eom.getKey());
            }
        }
            //verändere settler
        if(!state.events.get(event_id).effect_on_settler.isEmpty()) {
            for(Map.Entry<String,Integer> eos : state.events.get(event_id).effect_on_settler.entrySet()) {
                if (eos.getValue() == 1) state.add_settler();
                if (eos.getValue() == 2) state.remove_settler(eos.getKey());
            }
        }      
            //verändere optionen
                //noch nicht geschrieben

        state.event_browserOutput_stack.add(event_id);
    }

}