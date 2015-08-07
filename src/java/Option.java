//package java;

public class Option {
    public String name;
    public double min_science;
    public boolean available; //true if available

    Option(String new_name, double new_min_science) {
        name        = new_name;
        min_science = new_min_science;
        if(min_science == 0.0) available = true;
        else                   available = false;
    }
    public void make_available() {
        available = true;
    }

}