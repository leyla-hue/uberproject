package com.example.mynewproject.historyRecyclerView;

public class HistoryObject {
    private String rideId;
    private String time;
    private String destination;
    private String pickup;


    public HistoryObject(String time, String rideId, String destination, String pickup){
        this.rideId = rideId;
        this.time= time;
        this.destination = destination;
        this.pickup = pickup;

    }

    public String getRideId(){
        return rideId;
    }
    public void setRideId(String rideId){ this.rideId = rideId; }

    public String getTime(){ return time;}
    public void setTime(String time){ this.time=time;}

    public String getDestination(){return destination;}
    public void setDestination(String destination){this.destination=destination;}

    public String getPickup(){return pickup;}
    public void setPickup(String pickup){this.pickup=pickup;}


}
