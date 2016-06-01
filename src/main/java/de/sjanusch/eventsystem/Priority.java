package de.sjanusch.eventsystem;

public enum Priority {

    Low(0),

    Normal(1),

    High(2),

    System_Level(3);

    private int important;
    
    private Priority( int important ) {
        this.important = important;
    }
    
    public int getImportantance(){
        return important;
    }

}

