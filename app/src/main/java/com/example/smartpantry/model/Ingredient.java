package com.example.smartpantry.model;

public class Ingredient{
    private final long id;
    private final String name;
    private final double quantity;
    private final String unit;
    private final String expiryDate; //yyy-mm-dd, or null

    public Ingredient(long id, String name, double quantity, String unit, String expiryDate){
        this.id=id;
        this.name=name;
        this.quantity=quantity;
        this.unit=unit;
        this.expiryDate=expiryDate;
    }

    public long getID(){return id;}
    public String getName(){return name;}
    public double getQuantity(){return quantity;}
    public String getUnit(){return unit;}
    public String getExpiryDate(){return expiryDate;}
}