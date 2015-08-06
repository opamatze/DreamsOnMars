import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Controller {

    public Model state;
    public String id;
    public List<Integer> event_stack;

    Controller(String username) {
            //initiate game
        event_stack = new ArrayList<>();

            //Startbedingungen: 1 x Modul: mothership
        Map<String, Resource> resources = new HashMap<>();
        Map<String, Module> modules   = new HashMap<>();
        Map<String, Option> options   = new HashMap<>();
            //alle Resourcen, die es gibt
        String[] resource_names    = new String[]{"food","labour","power","material","science" };
            //alle Module, die gebaut werden können mit minimum an Forschung (Fortschritt)
        Map<String,Double> option_cost = new HashMap<>();
        option_cost.put("greenhouse",0.0);
        option_cost.put("collector",0.0);
        option_cost.put("lab",0.0);
        option_cost.put("solarpanel",0.0);

            //alle Ereignisse, die eintreten können
        Map<Integer,Event> events = new HashMap<>();
        //durch Spieler oder andere Ereignisse verursachte Ereignisse (aber nicht zufällig)
        events.put(101, new Event(101,"Ein neues Modul ist in Betrieb gegangen.", 1.0, 1, 0));
        System.out.println("ok1");
        events.put(102, new Event(102,"Ein Modul wurde abgestoßen.", 1.0, 1, 0));
        events.put(103, new Event(103,"Eine neue Errungenschaft wurde entdeckt.", 1.0, 1, 0));
        events.put(104, new Event(104,"Eine neues Modul wird gebaut.", 1.0, 1, 0));
        events.put(111, new Event(111,"Ein Modul konnte nicht gebaut werden.",1.0,1,0));

        //reguläre ereignisse
        events.put(301, new Event(301,"Raumschifflieferungen von der Erde (+20 Nahrung, +20 Energie, +1 Kosmonaut", 1.0, 10, 0));
        events.get(301).add_effect("resource","food",20.0);
        events.get(301).add_effect("resource","power",20.0);
        events.get(301).add_effect("module","cosmonaut",1.0);

        //zufällige positive Ereignisse
        events.put(501, new Event(501,"Die Pflanzen sind gut gewachsen ( +10 Nahrung ).", 0.05, 1, 0));
        events.get(501).add_effect("resource","food",10.0);

        events.put(502, new Event(502,"Die Sonne scheint erstaunlich stark ( +5 Energie ).", 0.1, 1, 0));
        events.get(502).add_effect("resource","power",5.0);
        //zufällige negative Ereignisse
        events.put(601, new Event(601,"Die Ernte ist verdorben ( -5 Nahrung ).", 0.005, 1, 0));
        events.get(601).add_effect("resource","food",-5.0);

        events.put(602, new Event(602,"Material ist spurlos verschwunden ( -3 Material ).", 0.007, 1, 0));
        events.get(602).add_effect("resource","material",-3.0);
        
        
            //Anfangsresourcen
        for (String res : resource_names )
            resources.put(res, new Resource(res,20.0));
            //Anfangsoptionen
        for (Map.Entry<String,Double> opt : option_cost.entrySet())
            options.put(opt.getKey(), new Option(opt.getKey(),opt.getValue()));
            //Anfangsmodule
        modules.put("mothership", new Module("mothership"));
        modules.get("mothership").build_module(null); //aktiviert Modul ohne Konstruktionskosten

        //initialisiere Spielmodell
        state = new Model(resources,modules,options,events);
        id = username;
            //debug
        System.out.println("controller constructor aufgerufen");
    }
        //Benutzeraktion checken
    public void user_action(String new_module_name) {
        if(new_module_name != "") {
            if (state.add_module(new_module_name)) {
                event_stack.add(104);
                System.out.println("modul in Auftrag gegegen: " + new_module_name);

            }
            else {
                System.out.println("modul nicht gebaut: " + new_module_name);
                event_stack.add(111);
            }

        }
        else {}

    }
        //durchlaufe alle Module
    public void process_modules() {

            //gebe jedem Modul einen Aktionspunkt
        for(Map.Entry<String,Module> m : state.modules.entrySet()) m.getValue().action_points = 1;

            //aktualisiere Resourcen, da manche Module auf andere warten müssen, soll die Schleife solange laufen, solange
            //noch Module anspringen
        boolean ap_used;
        do {
            ap_used = false;
            for (Map.Entry<String, Module> m : state.modules.entrySet()) {
                //wenn es genügend resourcen gibt und das Modul noch nicht dran war diese Runde und aktiv ist
                if (state.check_stock(m.getValue().uses) && m.getValue().action_points > 0 && m.getValue().statusIndex == 1) {
                    state.update_resources(m.getValue().uses);
                    state.update_resources(m.getValue().produces);
                    m.getValue().action_points -= 1;
                    ap_used = true;
                }
                    //construct undone modules...
                else if(m.getValue().statusIndex == 3 && m.getValue().action_points > 0) {
                    m.getValue().action_points--;
                    event_stack.add(m.getValue().activate_module(state));
                }
            }
        }
        while(ap_used == true);

    }
        //Ereignisse checken
    public void events() {
        Event event_handler = new Event();
        event_handler.pick_event_by_stack(state, event_stack);
        event_handler.pick_event_by_chance(state);

    }
    public String pull_user_info() {

        String output = "";

            //resources
        for(Map.Entry<String,Resource> res : state.resources.entrySet())
            output += res.getKey()+": "+res.getValue().amount+"<br> ";

        output += "<br>---------------------------------<br>";
            //modules
        for(Map.Entry<String,Module> modu : state.modules.entrySet()) {
            output += modu.getValue().name + "= ";
            output += " uses: ";

            for(Map.Entry<String,Double> resUse: modu.getValue().uses.entrySet())
                output += resUse.getValue()+" "+resUse.getKey()+" ";
            output += "|| produces: ";

            for(Map.Entry<String,Double> resPrd: modu.getValue().produces.entrySet())
                output += resPrd.getValue()+" "+resPrd.getKey()+" ";
            output += "<br>";


        }

        return output;
    }
    public String pull_user_info_json() {
            View outputObj = new View(this.state);
            String output = outputObj.viewToString();
            return output;
    }

}