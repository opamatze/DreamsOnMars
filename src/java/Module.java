import java.util.HashMap;
import java.util.Map;


public class Module {
    public String name;
    public int statusIndex; //0 = inactive, 1 = active, 2 = marked for deletion, 3=under construction
    public String[] statusText;
    public Map<String,Double> produces;
    public Map<String,Double> uses;
    public Map<String,Double> construction_cost;
    public double action_points;
    public int constrRnds;

    Module(String name) {
        this.name   = name;
        this.statusText = new String[]{"inactive","active","remove","under construction"};
        this.statusIndex = 0; //set to inactive (initial)
        this.produces = new HashMap<>();
        this.uses = new HashMap<>();
        this.construction_cost = new HashMap<>();
        this.action_points = 1;

        switch (name) {
            case "mothership" : this.new_mothership();
                break;
            case "cosmonaut"  : this.new_cosmonaut();
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
        else statusIndex = 1; //no construction cost and set to active
    }
    public int activate_module(Model state) {
        if(constrRnds > 0) {
            constrRnds--;
            return 0;
        }
        else this.statusIndex = 1; //set to active
        return 101; //create event
    }
    private void new_mothership() {
        produces.put("power",4.0);
        produces.put("food",4.0);
        produces.put("labour",1.0);
    }
    private void new_cosmonaut() {
        uses.put("food",-2.0);
        produces.put("labour",1.0);
    }
    private void new_greenhouse() {
        construction_cost.put("material",-10.0);
        construction_cost.put("food",-5.0);
        construction_cost.put("time",5.0);

        uses.put("power",-1.0);
        uses.put("labour",-1.0);

        produces.put("food",6.0);

    }
    private void new_lab() {
        construction_cost.put("material",-50.0);
        construction_cost.put("time",10.0);

        uses.put("power",-1.0);
        uses.put("labour",-3.0);
        uses.put("material",-3.0);

        produces.put("science",2.0);
    }
    private void new_collector() {
        construction_cost.put("material",-10.0);
        construction_cost.put("time",5.0);

        uses.put("power",-1.0);
        produces.put("material",2.0);
    }
    private void new_solarpanel() {
        construction_cost.put("material",-10.0);
        construction_cost.put("time",5.0);
        
        produces.put("power",1.0);
    }

}