package com.example.vecinapp.placeholder;

import com.example.vecinapp.ModelData.Evento;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaceholderContent {

    public static List<Evento> ITEMS = new ArrayList<Evento>();

    public static Map<String, Evento> ITEM_MAP = new HashMap<String, Evento>();

    public void addItem(Evento item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    public void setListEvent(List<Evento> list) {
        ITEMS = list;
        for (Evento event: list ) {
            ITEM_MAP.put(event.id, event);
        }
    }
}