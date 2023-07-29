// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP102 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP-102-112 - 2021T1, Assignment 6_and_7
 * Name: Ella Wipatene
 * Username: wipateella
 * ID: 300558005
 */

import ecs100.*;
import java.awt.Color;

/**
 * Location
 * Represents a position of something in a 2D space.
 * There are a collection of methods for
 *  - getting the x and y coordinates
 *  - computing the distance to another Location or a (x,y) position
 *  - moving the location some distance towards a target, but constrained to be within a region.
 *  - moving the location some distance in a given direction, but constrained to be within a region.
 * There are also (static) test methods for testing movement to a target and random movement constrained by a region.
 *  These may be helpful models for some components of your simulation.
 * 
 * You are permitted to modify this class if you wish.
 * You are not required to use this class.
 */

public class Location{
    private static final Region BOUNDARY = new Region(10, 10, 900, 450);
    
    //Fields of a location
    
    public double x;
    public double y;
    
    /**
     * Construct new Location 
     */
    public Location(double x, double y){ 
        this.x = x;
        this.y = y;
    }

    /**
     * Construct new random Location within a region.
     * - when making a new blob
     */
    public Location(Region boundary){
        this.x = boundary.west + Math.random()*(boundary.east - boundary.west);
        this.y = boundary.north + Math.random()*(boundary.south - boundary.north);
    }
    
    public void setLocation(double xCord, double yCord){
        this.x = xCord;
        this.y = yCord; 
    }
    
    public double getX(){ return this.x;}
    public double getY(){ return this.y;}

    
    /**
     * Returns the distance from this location to another.
     */
    public double distanceTo(Location other){
        return Math.hypot(other.x-this.x, other.y-this.y);
    }
    /**
     * Returns the distance from this location to (targX, targY).
     */
    public double distanceTo(double targX, double targY){
        return Math.hypot(targX-this.x, targY-this.y);
    }
    
    /**
     * Changes the location to be the specified distance from
     *  its current position towards a target location.
     * If the target is closer to this location than the distance, 
     *  then the location is only changed to the target.
     * The location cannot be moved outside the boundary if currently inside
     */
    public void moveTowards(double distance, Location target, Region boundary){
        double directionToTarget = Math.atan2((target.y-y), (target.x-x));
        distance = Math.min(distance, this.distanceTo(target));
        this.moveInDirectionRadians(distance, directionToTarget, boundary);
    }
    public void moveTowards(double distance, double targX, double targY, Region boundary){
        double directionToTarget = Math.atan2((targY-y), (targX-x));
        distance = Math.min(distance, this.distanceTo(targX, targY));
        this.moveInDirectionRadians(distance, directionToTarget, boundary);
    }

    /**
     * Returns a new location that is the specified distance away
     *  from this location in the specified direction
     * Directions are in degrees, clockwise from East = 0; direction South = 90
     * The location cannot be moved outside the boundary if currently inside
     */
    public void moveInDirection(double distance, double direction, Region boundary){
        this.moveInDirectionRadians(distance, direction* Math.PI/180, boundary);
    }

    // Private utility methods - can't be called externally

    /**
     * Changes the location to be the specified distance 
     * in the specified direction.
     * The location cannot be moved outside the boundary if currently inside
     */
    private void moveInDirectionRadians(double distance, double dirInRadians, Region boundary){
        double moveX =  distance*Math.cos(dirInRadians);
        double moveY =  distance*Math.sin(dirInRadians);

        if (boundary!=null && boundary.contains(this)){  //limit motion to within the boundary region
            // distance to East/West boundaries
            double distToEast = boundary.east-this.x;
            double distToWest = this.x - boundary.west;

            if (this.x + moveX > boundary.east){
                moveY = moveY * distToEast/moveX;
                moveX = distToEast;
            }
            else if (this.x + moveX < boundary.west){
                moveY = moveY * (-distToWest)/moveX;
                moveX = -distToWest;
            }

            // distance to North/South boundaries
            double distToNorth = this.y - boundary.north;
            double distToSouth = boundary.south-this.y;
            if (this.y + moveY > boundary.south){
                moveX = moveX * distToSouth/moveY;
                moveY = distToSouth;
            }
            else if (this.y + moveY < boundary.north){
                moveX = moveX * (-distToNorth)/moveY;
                moveY = -distToNorth;
            }
        }
        this.x = this.x+moveX;
        this.y = this.y+moveY;
    }
    

    /**
     * toString for debugging purposes
     */
    public String toString(){
        return String.format("(%.0f,%.0f)",this.x, this.y);
    }

    // Test code

    private static boolean running = false;
    private static Location targ = new Location(200, 390);
    /**
     * Static test method for testing movement of a location in a series
     * of straight line segments.
     * Gives quite good looking "random" motion in the region.
     * User can change the location with the mouse
     */

    public static void testMoveInRandomSegments(){
        Region boundary = new Region(10, 10, 500, 400);
        Location loc = new Location(100, 100);
        UI.setColor(Color.red);
        UI.fillRect(loc.getX()-4,loc.getY()-4, 8, 8);
        double dir =0;
        int stepCount = 0;
        running = true;
        // This line makes animation smoother, but requires explicit UI.repaintGraphics()
        UI.setImmediateRepaint(false);

            while (running){
                loc.moveInDirection(2.5, dir, boundary);
                stepCount++;
                if (boundary.onBoundary(loc)){
                    dir-= 150+(60*Math.random());
                    stepCount =0;
                }
                if (stepCount > 50){
                    dir += (270 * Math.random())-135;
                    stepCount = 0;
                }
                UI.clearGraphics();
                boundary.draw(Color.black);
                UI.setColor(Color.red);
                UI.fillRect(loc.getX()-4,loc.getY()-4, 8, 8);
                // if setImmediateRepaint(false) above, then need the next line to make things visible
                UI.repaintGraphics(); 
                UI.sleep(25);
                if (targ!=null){
                    loc=targ;
                    targ=null;
                }
            }
    }
    /**
     * Static test method for testing movement of a location towards a target.
     */
    public static void testMoveToTarget(){
        Region boundary = new Region(10, 10, 500, 400);
        Location loc = new Location(100, 100);
        UI.setColor(Color.red);
        UI.fillRect(loc.getX()-4,loc.getY()-4, 8, 8);
        UI.setColor(Color.blue);
        UI.fillRect(targ.getX()-2, targ.getY()-2, 4, 4);
        running = true;
        while(running && loc.distanceTo(targ)>0){
            loc.moveTowards(50, targ, boundary);
            UI.clearGraphics();
            boundary.draw(Color.black);
            UI.setColor(Color.red);
            UI.fillRect(loc.getX()-4,loc.getY()-4, 8, 8);
            UI.setColor(Color.blue);
            UI.fillRect(targ.getX()-2, targ.getY()-2, 4, 4);
            UI.sleep(500);
        }
    }
    
    /**
     * Main method for doing some testing.
     */
    public static void main(String[] args){
        UI.addButton("stop", ()->{running=false; UI.clearGraphics();});
        UI.setMouseListener((String a, double x, double y)->{targ = new Location(x,y);});
        UI.addButton("target", Location::testMoveToTarget);
        UI.addButton("random line segments", Location::testMoveInRandomSegments);
        UI.addButton("quit", UI::quit);
    }

}
