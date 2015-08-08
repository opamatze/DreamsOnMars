//package java;

import java.util.Map;

public class Option {
    public String name;
    public String label;
    public String title;
    public Integer min_science;
    public boolean available; //true if available

    Option(String name, String label, Integer min_science) {
        this.name        = name;
        this.label = label;
        this.min_science = min_science;
        this.available   = false;

        Module module = new Module(name);
        this.title = this.name+"  ";
        for(Map.Entry<String,Double> entry : module.construction_cost.entrySet()) {
            this.title += entry.getValue()+entry.getKey()+" ";
        }
        this.title += "(min. science: "+min_science+")";
    }
    public void make_available() {
        available = true;
    }

}