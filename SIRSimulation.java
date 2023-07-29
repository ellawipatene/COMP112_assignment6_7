// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP102 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP-102-112 - 2021T1, Assignment 6_and_7
 * Name: Ella Wipatene
 * Username: wipateella
 * ID: 300558005
 */



// TO DO LIST:
// - fix event diameters
// - If on quarantine bounary 

import ecs100.*;
import java.util.*;
import java.awt.*; 

/**
 * SIRSimulation
 * An individual-based simulation program for the SIR (Susceptible-Infected-Recovered) model for spread of diseases.
 * The program creates a list of individuals each with some location within a defined region.
 * The individuals move around in some random way, and may come in contact with each other.
 * An individual may be susceptible to the disease, infected, or recovered.
 * The simulation should start with all the individuals in the Susceptible state.
 * If an infected individual comes in contact with a susceptible individual, then with some probability,
 * the susceptible individual may become infected.
 * After some period of time, an infected individual will recover.
 * The simulation should run as a loop with each cycle of the loop representing one time step,
 *  in which each individual moves, and may change their state, and then the whole set of individuals
 *  are redrawn in their new positions and new states.
 *  The program should let the user control the speed of the simulation (by changing the delay between time steps).
 * There are several numeric parameters controlling the simulation which the user should be able to
 *   modify (eg, using sliders). The parameters include at least the following: 
 *  - the population size
 *  - the distance between individuals at which they are considered "in contact"
 *  - the probability that a susceptible individual will be infected if they are in contact
 *    with an infected individual.
 *  - the time that an infected individual takes to recover
 * The program should allow the user to place new infected individuals into the simulation (using the mouse).
 * The program should also allow the user to place "Events" in the simulation (using the mouse)
 *  An event will attract all the individuals in its "attraction radius" to move to the event.
 *  The individuals will stay at the event until the event is stopped; then they will leave in random directions.
 * The program should also allow the user to place quarantine regions in the simulation.
 *  A quarantine region should be a rectangular region that individuals are not allowed to exit.
 *  (They may or may not be prevented from entering them).
 *  The user should be able to add multiple quarantine regions, and should be able to get rid of them.
 *
 * There are lots of different possible movement behaviours of the individuals.
 *  The more realistic the behaviour,  the more complicated it is to implement.
 *  Some kind of random motion, where individuals move in the same direction
 *  for some number of steps, then change to a different random direction is OK (except for
 *  events, where they should head straight for the event).
 *  It would be nice to have individuals moving mostly between home, work, and the shops,
 *   with random other trips, but this is much more complicated.
 */
public class SIRSimulation{
    // Constants
    /** Boundary of the simulation region. No individuals should be outside this region. */
    private static final Region BOUNDARY = new Region(10, 10, 900, 450);
    
    private static boolean rerun; // starting and stoping the program
    
    // Statistics of peoples states 
    private int n; // number of people
    private int susceptible_counter = 0; // number of susceptible people
    private int infected_counter = 0; // number of infected people 
    private int infected_adder = 0; 
    private int healed_counter = 0; // number of healed people
    
    // Infected people variables
    private boolean infected_people = false; // if there are any infected people present 
    private boolean clicked_infected = false; 
   
    // Slider values
    public double speed = 10; // speed of the program drawer
    public double infection_distance = 15; // distance at which someone can get infected
    public double infection_prob = 50; // probablility of someone getting infected 
    public double recovery_time = 50; // amount of rounds of draws because 
    public double imunity_time = 50; 
    
    // For Quarantines 
    private boolean clicked_quarantine = false;
    private boolean quarantine = false; // if there are quarantines 
    private double startX, startY, endX, endY; // cord of box
    
    // For events
    public double centerX, centerY, radiusX, radiusY, diameter; // size variables
    private boolean mouse_pressed = false; 
    private boolean mouse_released = true;   
    private boolean event = false; // if there is an event on
    
    private ArrayList<Individual> people = new ArrayList<Individual>();
    public ArrayList<Region> quarantines = new ArrayList<Region>();
    
    // Main
    public static void main(String[] arguments){
        new SIRSimulation().setupGUI();
        
    }    

    public void running(){
        while (true){
            if (this.rerun == true){
                break;
            }else{
                doOneStep(); 
            }
        }
    }
    
    public void SIRSimulationStart(double x){
        this.n = (int) x; 
        
        UI.println(this.n);
        UI.println(x);
        
        susceptible_counter = this.n; 
        infected_counter = 0;
        infected_adder = 0; 
        healed_counter = 0; 
        
        rerun = true; // this is to stop the drawer from running because otherwise it would get faster and faster 
        UI.sleep(100);
        people.clear(); // clears the people list 
        infected_people = false; 
        infected_counter = 0; 
        
        for (int i = 0; i < n; i++){ // creating the people 
            people.add(new Individual()); 
        }
        for (Individual p: this.people) {  // draws people initially  
            p.draw();
        }
        
        this.rerun = false;
    
        running(); 
    }
    
    public void addInfected(String action, double x, double y){
        if (action.equals("released")){
            if (clicked_infected == true){
                UI.sleep(100); 
        
                people.add(new Individual());  
                people.get(n + infected_adder).infected();  
                people.get(n + infected_adder).changeLocation(x,y); // changes location to where they clicked 
                people.get(n + infected_adder).draw(); 
                infected_people = true; // this is to stop the drawer on the simulator starter 
                
                infected_adder++; 
                infected_counter++; // counts another infected Individual
            }
        }
    }
    
    public void doOneStep(){
            UI.clearGraphics();
            getInfected(); // checks if people are infected or not
            
            for (Individual p: people){ 
                if (quarantine == true){ // if there are quarantines 
                    for (Region r: quarantines){
                        boolean in_quarantine = r.contains(p.location); // checks if the Individual is in the quarantine
                        if (in_quarantine == true && p.in_quarantine == false){
                            p.inQuarantine(r);
                        }
                    }
                } 
                
                if (p.in_quarantine == true){
                    p.moveRand(p.quarantine);
                } else{
                    p.moveRand(BOUNDARY); 
                }
                
                p.draw(); 
                
                if (p.infected == true && p.infected_time > recovery_time){ // checks if people have been sick for long enough to recover
                    p.recovered(); // makes them recovered
                    infected_counter--; 
                    healed_counter++; 
                } else if (p.recovered == true && p.recovered_time > imunity_time){
                    p.susceptible(); 
                    healed_counter--;
                    susceptible_counter++;
                }
                
                if (event == true){ // if there is an event on and the Individual is in the radius, go to the event. 
                    double distance = p.getDistEvent(centerX, centerY); // checks the Individuals distance to the event
                    if (distance <= diameter/2 && p.in_quarantine == false){
                        p.moveToEvent(centerX, centerY, distance); 
                    }
                }
                
                
            }
            
            if (event == true){
                drawEvent(); 
            }
            
            if (quarantine == true){ 
                for (Region r: quarantines){
                    r.draw(Color.black); 
                }
            }
            
            drawGraph();
    
            UI.repaintGraphics();
            UI.sleep(speed * 10); // controls speed of the program
         
    }
    
    
    /**
     * Checks if the people will get infected or not 
     */
    public void getInfected(){ 
        double distance = 0;
        double prob_num = 0; 
        for (int i = 0; i < people.size(); i++){ // goes around for every Individual
            prob_num = Math.random() * 100; // produces a random number between 0 and 100
            if (people.get(i).infected == false && people.get(i).recovered == false){ // only check if they are not infected or recovered
                for (int n = 0; n < people.size(); n++){ // checks that one Individual with everyone
                    if (people.get(n).infected == true){ // only check dist with those who are infected 
                        distance = people.get(i).getDist(people.get(n));
                        if (distance <= infection_distance && prob_num <= infection_prob){ 
                            people.get(i).infected(); 
                            infected_counter++; 
                            susceptible_counter--; 
                            break; 
                        }
                    }
                }
            }
        }
    
    }
    
    
    /*# Setting variables from sliders*/
    
    public void setSpeed(double x){
        this.speed = 0;
        UI.sleep(10); 
        this.speed = x; 
    }
    
    public void setDistance(double x){
        this.infection_distance = 0; 
        UI.sleep(10); 
        this.infection_distance = x;
    }
    
    public void setProbability(double x){
        this.infection_prob = 0; 
        UI.sleep(10); 
        this.infection_prob = x;
    }
    
    public void setRecoveryTime(double x){
        this.recovery_time = 0; 
        UI.sleep(10); 
        this.recovery_time = x;
    }
    
    public void setImunityTime(double x){
        this.imunity_time = 0; 
        UI.sleep(10); 
        this.imunity_time = x;
    }
    
    public void eventClicked(){
        if (event == true){
            event = false; 
        } else if (event == false){
            event = true; 
        }
    }
    
    public void booleanInfected(){
        if (clicked_infected == false){
            clicked_infected = true; 
        }
    }
    
    public void addEvent(String action, double x, double y){
        UI.println(action); 
        if (action.equals("pressed")){
            centerX = x; 
            centerY = y; 
            UI.println(centerX);
            UI.println(centerY);
            mouse_pressed = true; 
        } else if (action.equals("released")){
            radiusX = x;
            radiusY = y; 
            UI.println(radiusX);
            UI.println(radiusY);
            mouse_released = true; 
            
            diameter = (Math.hypot(radiusX - centerX, radiusY - centerY)) * 2; 
        
            UI.setColor(new Color(255, 200, 50, 70)); 
            UI.fillOval(centerX - (diameter/2), centerY - (diameter/2), diameter, diameter); 
            UI.setColor(new Color(255, 200, 50, 255)); 
            UI.fillOval(centerX - 8, centerY - 8, 20, 20); 
            
            event = true; 
        }
    }
    
    public void drawEvent(){
        UI.setColor(new Color(255, 200, 50, 70)); 
        UI.fillOval(centerX - (diameter/2), centerY - (diameter/2), diameter, diameter); 
        UI.setColor(new Color(255, 200, 50, 255)); 
        UI.fillOval(centerX - 8, centerY - 8, 20, 20); 
    }
    
    public void addQuarantine(String action, double x, double y){
        UI.println(action); 
        if (action.equals("pressed")){
            startX = 0; 
            startY = 0;
            endX = 0;
            endY = 0;
            diameter = 0; 
            
            startX = x; 
            startY = y; 
            UI.println(startX);
            UI.println(startY);
            mouse_pressed = true; 
        } else if (action.equals("released")){
            endX = x;
            endY = y; 
            UI.println(endX);
            UI.println(endY);
            mouse_released = true; 
            quarantine = true;
            
            quarantines.add(new Region(startX, startY, endX, endY));
            for (Region r: quarantines){
                r.draw(Color.black); 
            }
        }
    }
    
    public void removeQuarantine(){
        quarantines.clear(); 
        for (Individual p: people){
            p.leftQuarantine(); 
        }
        quarantine = false; 
    }
    
    public void clickedInfected(){
        clicked_infected = true; 
        UI.setMouseListener(this::addInfected); 
    }
    
    public void clickedQuarantine(){
        if (mouse_pressed == true && mouse_released == true){ // resets the mouse boolean variables 
            mouse_pressed = false;
            mouse_released = false;  
        }
        UI.setMouseListener(this::addQuarantine); 
    }
    
    public void clickedEvent(){
        if (mouse_pressed == true && mouse_released == true){ // resets the mouse boolean variables 
            mouse_pressed = false;
            mouse_released = false; 
            event = false; 
        }
        UI.setMouseListener(this::addEvent); 
    }
    
    
    
    // GRAPH FUNCTIONS
    
    public void drawGraph(){
        UI.setColor(Color.black);
        UI.drawLine(50, 650, 650, 650);
        UI.drawLine(50, 500, 50, 650);
        
        double tot_people_label = (double)n/3; 
        for(int i = 0; i < 4; i++){
            UI.drawLine(45, 500 + (i*50), 50, 500 + (i*50));
            if (i == 3){
                UI.drawString(String.valueOf(people.size()), 10, 655  - (i*50));
            } else {
                UI.drawString(String.valueOf((int)tot_people_label * i ), 10, 655  - (i*50));
            }
            UI.drawLine(50 + (200*i), 650, 50 + (200*i), 655);
        }
        
        UI.drawString("Susceptible", 110, 665);
        UI.drawString("Infected", 330, 665);
        UI.drawString("Healed", 530, 665); 
        
        UI.setColor(Color.green);
        UI.fillRect(75, 650 - (150 * ((double)susceptible_counter/(double)n)), 150, (150 * ((double)susceptible_counter/(double)n))); 
        UI.setColor(Color.black); 
        UI.drawString(String.valueOf(susceptible_counter), 135, 600); 
        
        UI.setColor(Color.red);
        UI.fillRect(275, 650 - (150 * ((double)infected_counter/(double)n)), 150,(150 * ((double)infected_counter/(double)n)));
        UI.setColor(Color.black); 
        UI.drawString(String.valueOf(infected_counter), 335, 600); 
        
        UI.setColor(Color.blue);
        UI.fillRect(475, 650 - (150 *((double)healed_counter/(double)n)), 150,(150 *((double)healed_counter/(double)n)));
        UI.setColor(Color.black); 
        UI.drawString(String.valueOf(healed_counter), 535, 600); 
    
    }
    
    
    
    public void setupGUI(){
        UI.setImmediateRepaint(false);  // good idea to avoid "flicker"...
        BOUNDARY.draw(Color.blue); 
        UI.initialise();
        
        //Buttons
        UI.addButton("Add Infected", this::clickedInfected); 
        UI.addButton("Event", this::clickedEvent); 
        UI.addButton("Add Quarantine", this::clickedQuarantine); 
        UI.addButton("Remove Quarantine", this::removeQuarantine); 
        
        // Slidders 
        UI.addSlider("Population Size", 10, 500, 50, this::SIRSimulationStart);
        UI.addSlider("Infection Distance", 0, 30, 15, this::setDistance); 
        UI.addSlider("Infection Probability", 0, 100, 50, this::setProbability); 
        UI.addSlider("Recovery Time", 0, 100, 80, this::setRecoveryTime); 
        UI.addSlider("Imunity Time", 0, 100, 50, this::setImunityTime); 
        UI.addSlider("Speed (1 = fast, 15 = slow)", 1, 15, 8, this::setSpeed); 
        
        UI.addButton("Quit",UI::quit);   
    }

}
