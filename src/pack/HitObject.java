package pack;

import java.util.HashMap;

public class HitObject {
	
	HashMap<String, String> parameters;
	
	public HitObject(String[] params) {
		parameters = new HashMap<>();
		
		for(String s : params) {
			parameters.put(s.split("=")[0], s.split("=")[1]);
		}
	}
	
	public String getParameter(String key) {
		return parameters.get(key);
	}
	
	public void setParameter(String key, String value) {
		parameters.put(key, value);
	}
	
	public HashMap<String, String> getParameters(){
		return parameters;
	}

}
