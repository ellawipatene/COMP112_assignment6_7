import java.util.*;
import java.awt.Color;
import ecs100.*;

public class Individual
{
    private static int counter=0; // useful to assign each Blob's "index" field.
    
    public static final Region BOUNDARY = new Region(10, 10, 900, 450);
    
    // instance variables
    private int index;
    private double angle; 
    public Location location; 
    private Color colour; 
    public Region quarantine; 
    
    
    public boolean infected; // if they are infected 
    public boolean recovered; // if they are recovered 
    public boolean in_quarantine; // if they are in a quarantine
    public int infected_time = 0; // how long they have been infected for
    public int recovered_time = 0; // how long they have been recovered for

    
    public Individual(){
        this.index = this.counter++; 
        this.location = new Location(BOUNDARY);
        this.angle = Math.random() * 360;
        this.infected = false; 
        this.recovered = false;  
        this.in_quarantine = false; 
    }
    
    public void infected(){
        this.infected = true; 
    }
    
    public void recovered(){
        this.recovered = true; 
        this.infected = false; 
    }
    
    public void susceptible(){
        this.recovered = false; 
        this.infected = false; 
    }
    
    public void inQuarantine(Region r){
        this.quarantine = r;
        this.in_quarantine = true; 
    }
    
    public void leftQuarantine(){
        this.in_quarantine = false;
        this.quarantine = BOUNDARY; 
    }
    
    public void changeLocation(double x, double y){
        this.location.setLocation(x,y); 
    }
    
    public void draw() {
        if(this.recovered == true){
            UI.setColor(Color.blue); 
            recovered_time++;
        }else if (this.infected == false){
            UI.setColor(Color.green); 
        } else if (this.infected == true){
            UI.setColor(Color.red); 
            infected_time++; 
        }
        
        UI.fillOval(this.location.x, this.location.y, 5, 5);
    }
    
    public void moveRand(Region boundary){
        if (this.location.x == 10 || this.location.x == 900 || this.location.y == 10 || this.location.y == 450){
            this.angle = this.angle + 180;  
        } else {
            int plusOrMinus = Math.random() < 0.5? -1 : 1;
            this.angle = this.angle + (Math.floor(Math.random()*30) * plusOrMinus); 
        }
        
        this.location.moveInDirection(10, angle, boundary); 
    }
    
    public double getDist(Individual pp){
        double distance = 0; 
        if (this.index != pp.index){
            distance = this.location.distanceTo(pp.location); 
        }       
        return distance; 
    }
    
    public double getDistEvent(double targX, double targY){
        double distance = this.location.distanceTo(targX, targY); 
        return distance;
    }
    
    public void moveToEvent(double targX, double targY, double distance){
        this.location.moveTowards(distance, targX, targY, BOUNDARY); 
    }
    
    public void quarantine(Region quarantine){
        //ahhhh
    }
}
