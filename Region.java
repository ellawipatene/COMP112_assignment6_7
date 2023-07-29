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
 * Region
 * Represents a rectangular region, bounded on the west, north, east, and south.
 * Has methods to
 *  - determine whether a Location or (x,y) position is in the region
 *  - determine whether a Location or (x,y) position is on the boundary
 *  - return a new Region that is the intersection (overlapping region)
 *    of this and another region.
 *  - draw the region as a rectangle of a specified color
 * You are permitted to modify this class if you wish.
 * You are not required to use this class.
 */

public class Region{

    //boundaries:  final, and therefore can be public
    public final double west, north, east, south;
    private double width, height;  // for drawing

    /** Construct region. */
    public Region(double west, double north, double east, double south){
        this.west = west;
        this.north = north;
        this.east = east;
        this.south = south;
        this.width =  Math.max(0, this.east-this.west);
        this.height = Math.max(0, this.south-this.north);
    }

    public void draw(Color col){
        UI.setColor(col);
        UI.drawRect(this.west, this.north, this.width, this.height);
    }

    
    /**
     * Is a location within this region
     */
    public boolean contains(Location loc){
        return this.contains(loc.getX(),loc.getY());
    }

    /**
     * Is a position within this region
     */
    public boolean contains(double x, double y){
        return (x>=this.west && x<=this.east && y>=this.north && y<=this.south);
    }

    /**
     * Is a location on the boundary of this region
     */
    public boolean onBoundary(Location loc){
        return this.onBoundary(loc.getX(),loc.getY());
    }

    /**
     * Is a position on the boundary of this region
     */
    public boolean onBoundary(double x, double y){
        return (x==this.west  || x==this.east || y==this.north || y==this.south);
    }

    /**
     * Return the new region that is the intersection of this region and the other,
     * return an empty region if no such region
     */
    public Region intersection(Region other){
        return new Region(Math.max(this.west, other.west),
            Math.max(this.north, other.north),
            Math.min(this.east, other.east),
            Math.min(this.south, other.south));
    }

    public String toString(){
        return String.format("Reg[%.0f..%.0f,%.0f..%.0f]",
            this.west,this.east,this.north, this.south);
    }


}
