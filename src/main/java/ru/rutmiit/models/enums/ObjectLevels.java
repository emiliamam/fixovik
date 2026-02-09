package ru.rutmiit.models.enums;

public enum ObjectLevels {
    ApartamentObject(1, "Апартаменты"), CommercialObject(2, "Коммерческий объект"), HouseObject(3, "Дом"), LoftObject(4, "Лофт");

    private int value;
    private String russian;

    ObjectLevels(int value, String russian) {this.value = value; this.russian = russian;}
    public int getValue() {return value;}

    public void setValue(int value) {
        this.value = value;
    }

    public String getRussian() {
        return russian;
    }

    public void setRussian(String russian) {
        this.russian = russian;
    }
}
