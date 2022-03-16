package ru.anbn.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Teacher {
    /*
    {
        "name": "Ivan",
            "surname": "Ivanov",
            "favorite_music": [
        "Metallica",
                "Nirvana"
  ],
        "address": {
        "street": "Mira",
                "house": "10"
    }
    }
     */

    
    public String name;
    public String surname;
    @SerializedName("favorite_music")
    public List<String> favoriteMusic;
    public Address address;

}
