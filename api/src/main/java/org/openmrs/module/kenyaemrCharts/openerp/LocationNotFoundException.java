package org.openmrs.module.kenyaemrCharts.openerp;


public class LocationNotFoundException extends Exception{

    public LocationNotFoundException(final String aMessage){
        super(aMessage);
    }

    public LocationNotFoundException(final String aMessage, final Throwable aCause){
        super(aMessage,aCause);
    }
}
