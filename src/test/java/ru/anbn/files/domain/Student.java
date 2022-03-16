package ru.anbn.files.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Student {
    public String name;
    public String surname;
    @JsonProperty("favorite_music")
    public List<String> favorite_music;
    public Address address;
}