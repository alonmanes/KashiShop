package orielmoznino.example.alonmanes.model;

import java.util.HashMap;

import orielmoznino.example.alonmanes.model.Youth;

public class Elderly extends Youth {
    public String phone;
    public String city;

    public Elderly(String uid, String email, String userName, String url, String phone, String city) {
        super(uid, email, userName, url);
        this.phone = phone;
        this.city = city;
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> map = super.toMap();
        map.put("phone",phone);
        map.put("city",city);
        return map;
    }

    public Elderly(HashMap<String, Object> map) {
        super(map);
        phone = String.valueOf(map.get("phone"));
        city = String.valueOf(map.get("city"));
    }
}
