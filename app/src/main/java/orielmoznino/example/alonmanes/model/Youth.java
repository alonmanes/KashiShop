package orielmoznino.example.alonmanes.model;

import java.util.HashMap;

public class Youth {
    public String uid;
    public String email;
    public String userName;
    public String url;

    public Youth(String uid, String email, String userName, String url) {
        this.uid = uid;
        this.email = email;
        this.userName = userName;
        this.url = url;
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("uid",uid);
        map.put("email",email);
        map.put("userName",userName);
        map.put("url",url);
        return map;
    }

    public Youth(HashMap<String, Object> map) {
        this.uid = String.valueOf(map.get("uid"));
        this.email = String.valueOf(map.get("email"));
        this.userName = String.valueOf(map.get("userName"));
        this.url = String.valueOf(map.get("url"));
    }
}
