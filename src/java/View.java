//package java;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class View {

   // private JsonObject browserOutput;
    private JSONObject browserOutput;

    View(Model state) {

        JSONObject resources;
        JSONObject modules;
        JSONArray events;

        System.out.println("view constr called");
        System.out.println("state.resources: "+state.resources.toString());

        browserOutput = new JSONObject();

            //exists always
        resources = new JSONObject();
        for(Map.Entry<String,Resource> res : state.resources.entrySet()) {
            resources.put(res.getValue().name, res.getValue().amount);
        }
            //check if modules exist
        modules = new JSONObject();
        if(state.modules != null) {
            for(Map.Entry<String,Module> m : state.modules.entrySet()) {
                    //put the whole object
                JSONObject next_module = new JSONObject();
                next_module.put("name", m.getValue().name);
                next_module.put("id", m.getValue().id);
                next_module.put("assignedTo", m.getValue().assignedTo);
                next_module.put("statusIndex",m.getValue().statusIndex);
                next_module.put("automatic",m.getValue().automatic);
                next_module.put("constrRnds", m.getValue().constrRnds);
                next_module.put("status", m.getValue().statusText[m.getValue().statusIndex]);
                JSONObject constrObj = createDoubleMap(m.getValue().construction_cost);
                next_module.put("construction_cost", constrObj);
                JSONObject usesObj = createDoubleMap(m.getValue().uses);
                next_module.put("uses", usesObj);
                JSONObject prodObj = createDoubleMap(m.getValue().produces);
                next_module.put("produces", prodObj);
                next_module.put("construction_rounds_remain", m.getValue().constrRnds);
                modules.append(m.getKey(), next_module);
            }
        }
            //check if event_stack exist
        if(!state.event_browserOutput_stack.isEmpty()) {
            events = createEventList(state.event_browserOutput_stack, state);
            System.out.println("new JSONObject(state.event_browserOutput_stack);");

        }
        else {
            events = new JSONArray();
            System.out.println("new JSONObject();");
        }

        browserOutput.append("resources", resources);
        browserOutput.append("modules",modules);
        browserOutput.append("gameEvents",events);

        //state.event_browserOutput_stack.clear();
        System.out.println("browserOutput constructed: "+browserOutput.toString());

    }
    private JSONObject createResourceMap(Map<String,Resource> map) {
        JSONObject jsObj = new JSONObject();
        for(Map.Entry<String,Resource> entry : map.entrySet()) {
            jsObj.put(entry.getValue().name,entry.getValue().amount);
        }
        return jsObj;
    }
    private JSONObject createDoubleMap(Map<String,Double> map) {
        JSONObject jsObj = new JSONObject();
        for(Map.Entry<String,Double> entry : map.entrySet()) {
            jsObj.put(entry.getKey(),entry.getValue());
        }
        return jsObj;
    }
    /*
    private JSONObject createEventList(List<Integer> list, Model state) {
        JSONObject jsonObj = new JSONObject();
            //last elements first
        //for(Integer i = list.size() - 1; i>=0; i--) {
            for(Integer i = 0; i<list.size();i++) {
            jsonObj.put(i.toString(), "+++"+state.getEventByID(list.get(i)));
        }

        return jsonObj;
    }*/
    private JSONArray createEventList(List<Integer> list, Model state) {
        JSONArray jsonArr = new JSONArray();
    //    for(Integer i = 0; i<list.size();i++) {
        for(Integer i = list.size() - 1; i>=0; i--) {
            jsonArr.put("+++"+state.getEventByID(list.get(i)));
        }
        return jsonArr;
    }
    public String viewToString() {
        return browserOutput.toString();
    }
}