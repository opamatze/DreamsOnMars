//package java;

import java.util.HashMap;
import java.util.Map;


public class Module {
    public String id;
    public String name;
    public int statusIndex; //0 = inactive, 1 = active, 2 = marked for deletion, 3=under construction
    public String[] statusText;
    public String assignedTo;
    public boolean automatic; //benötigt kein Assignment
    public Map<String,Double> produces;
    public Map<String,Double> uses;
    public Map<String,Double> construction_cost;
    public double action_points;
    public int constrRnds;

    Module(String name) {
        this.constrRnds = 0;
        this.name   = name;
        this.statusText = new String[]{"not assigned","active","remove","under construction"};
        this.statusIndex = 0; //set to inactive (initial)
        this.assignedTo = "";
        this.automatic = false; //default
        this.produces = new HashMap<>();
        this.uses = new HashMap<>();
        this.construction_cost = new HashMap<>();
        this.action_points = 0;

        switch (name) {
            case "mothership" : this.new_mothership();
                break;
            case "settler"    : this.new_settler();
                break;
            case "greenhouse" : this.new_greenhouse();
                break;
            case "lab"        : this.new_lab();
                break;
            case "collector"  : this.new_collector();
                break;
            case "solarpanel" : this.new_solarpanel();
                break;
            default : System.out.println("unbekanntes Modul aufgerufen");
                      System.exit(1);
        }
        System.out.println(name+" wahrscheinlich gebaut");
    }
    public void build_module(Model state) {
        System.out.println("build_module(Model state)");

        if(!this.construction_cost.isEmpty()) {

            if(state.check_stock(construction_cost)) {
                state.update_resources(construction_cost);
                statusIndex = 3; // under construction
                constrRnds = construction_cost.get("time").intValue();
                System.out.println("constrRnds: " + constrRnds);
            }
            else statusIndex = 2; //marked for deletion, not enough construction resources

        }
        else {
            if(automatic) statusIndex = 1; //no construction cost and set to active
            else statusIndex = 0; // set to unassigned
        }
        System.out.println("build_module methode - check");
    }
    public int activate_module() {
        if(constrRnds > 0) {
            constrRnds--;
            return 0;
        }
        else {
            if(automatic) statusIndex = 1; //set to active
            else statusIndex = 0; //set to unassigned
        }
        return 101; //create event
    }
    public boolean assign(Model state) {
        for (Map.Entry<String, Settler> entry : state.settler.entrySet()) {
            String key = entry.getKey();
            Settler settler = entry.getValue();
            if (settler.assignment.isEmpty()) {
                if (this.statusIndex == 0) {
                    settler.assignment = this.id;
                    this.assignedTo = settler.id;
                    this.statusIndex = 1; //assigned
                    return true;
                }
            }
        }
        System.out.println("no free settlers or status something different than 'not assigned'");
        return false;
    }

    public boolean unassign(Model state) {
            //wenn es den settler gar nicht mehr gibt
        if(!state.settler.containsKey(this.assignedTo)) {
            System.out.println(this.assignedTo+" does not exist");
            this.assignedTo = ""; //free module
            this.statusIndex= 0;  //set to unassigned
            return false;
        }
            //wenn es ihn noch gibt
        else {
            state.settler.get(this.assignedTo).assignment = ""; //free settler
            this.assignedTo = ""; //free module
            this.statusIndex= 0;  //set to unassigned
            return true;
        }
    }

    private void new_mothership() {
        produces.put("power",4.0);
        produces.put("food",4.0);
    }
    private void new_settler() {
        uses.put("food",-3.0);
        this.automatic = true;
    }
    private void new_greenhouse() {
        construction_cost.put("material",-10.0);
        construction_cost.put("food",-5.0);
        construction_cost.put("time",5.0);

        uses.put("power",-1.0);

        produces.put("food",6.0);

    }
    private void new_lab() {
        construction_cost.put("material",-50.0);
        construction_cost.put("time",10.0);

        uses.put("power",-1.0);
        uses.put("material",-3.0);

        produces.put("science",2.0);
    }
    private void new_collector() {
        construction_cost.put("material",-10.0);
        construction_cost.put("time",5.0);

        uses.put("power",-1.0);
        produces.put("material",2.0);

        this.automatic = true;

    }
    private void new_solarpanel() {
        construction_cost.put("material",-10.0);
        construction_cost.put("time",5.0);
        
        produces.put("power",1.0);

        this.automatic = true;
    }

}