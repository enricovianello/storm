/**
 * 
 */
package it.grid.storm.griduser;

/*
 * @author zappi
 */
public interface SubjectAttribute {

    /**
     *
     * @param obj Object
     * @return boolean
     */
    public abstract boolean equals(Object obj);

    /**
     * Return the usual string representation of the FQAN.
     */
    public abstract String toString();

}