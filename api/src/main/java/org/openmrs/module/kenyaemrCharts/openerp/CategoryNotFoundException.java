package org.openmrs.module.kenyaemrCharts.openerp;


public class CategoryNotFoundException extends Exception{

    public CategoryNotFoundException(final String aMessage){
        super(aMessage);
    }

    public CategoryNotFoundException(final String aMessage, final Throwable aCause){
        super(aMessage,aCause);
    }
}
