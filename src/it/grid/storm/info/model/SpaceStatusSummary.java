package it.grid.storm.info.model;

import it.grid.storm.catalogs.ReservedSpaceCatalog;
import it.grid.storm.space.StorageSpaceData;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.stream.XMLStreamException;

import org.codehaus.jettison.AbstractXMLStreamWriter;
import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;
import org.codehaus.jettison.mapped.MappedXMLStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpaceStatusSummary {

	protected String saAlias;        /** defined in config/db (static value) **/
	protected long usedSpace;        /** info retrieved by sensors **/ //published by DIP
	protected long unavailableSpace; /** info retrieved by sensors **/
	protected long reservedSpace;    /** info retrieved from DB **/ //published by DIP SETTED TO ZERO BECAUSE CURRENTLY RETURN FAKE VALUES 
	protected long totalSpace;       /** defined in config/db (static value) **/ //published by DIP
    //For now do not consider the reserved space, a better management is needed

	private static final ReservedSpaceCatalog catalog = new ReservedSpaceCatalog();

	private static final Logger log = LoggerFactory.getLogger(SpaceStatusSummary.class);
	
	/*****************************
	 *  Constructors
	 */
	
	/**
	 * 
	 */
	public SpaceStatusSummary(String saAlias, long totalSpace) {
		this.saAlias = saAlias;
		this.totalSpace = totalSpace;
	}
	
	public SpaceStatusSummary(String saAlias) {
		this.saAlias = saAlias;
		this.totalSpace = -1;  // -1 means undefined;
	}
	
	private SpaceStatusSummary(String saAlias, long usedSpace, long unavailableSpace, long reservedSpace, long totalSpace) {
        this.saAlias = saAlias;
        this.usedSpace = usedSpace;
        this.unavailableSpace = unavailableSpace;
        this.reservedSpace = reservedSpace;
        this.totalSpace = totalSpace;
    } 
	
    /**
     * Produce a SpaceStatusSummary with fields matching exactly the ones available on the database
     * 
     * @param saAlias
     * @return
     * @throws IllegalArgumentException
     */
    public static SpaceStatusSummary createFromDB(String saAlias) throws IllegalArgumentException
    {
        StorageSpaceData storageSpaceData = catalog.getStorageSpaceByAlias(saAlias);
        if (storageSpaceData == null)
        {
            throw new IllegalArgumentException("Unable to find a storage space row for alias \'" + saAlias + "\' from storm Database");
        }
        else
        {
            if(!storageSpaceData.isInitialized())
            {
                log.warn("Building the SpaceStatusSummary from non initialized space with alias \'" + saAlias + "\'");
            }
            SpaceStatusSummary summary =  new SpaceStatusSummary(saAlias,
                                          storageSpaceData.getUsedSpaceSize().value(),
                                          storageSpaceData.getUnavailableSpaceSize().value(),
                                          storageSpaceData.getReservedSpaceSize().value(),
                                          storageSpaceData.getTotalSpaceSize().value());
//            summary.forceAvailableSpace(storageSpaceData.getAvailableSpaceSize().value());
//            summary.forceFreeSpace(storageSpaceData.getFreeSpaceSize().value());
//            summary.forceBusySpace(storageSpaceData.getBusySpaceSize().value());
            return summary;
        }
    }
    
	/*****************************
	 *  GETTER methods
	 ****************************/	
	
	/**
	 * @return the saAlias
	 */
	public String getSaAlias() {
		return saAlias;
	}
	
	/**
	 * busySpace = used + unavailable + reserved
	 * @return the busySpace
	 */
	public long getBusySpace() {
	        return this.usedSpace + this.reservedSpace + this.unavailableSpace;
	}
	
	/**
	 * availableSpace = totalSpace - busySpace
	 * @return
	 */
	public long getAvailableSpace()
    {
	    return this.totalSpace - this.getBusySpace();
    }
	
	/**
	 * @return the usedSpace
	 */
	public long getUsedSpace() {
		return usedSpace;
	}
	

    /**
	 * @return the unavailableSpace
	 */
	public long getUnavailableSpace() {
		return unavailableSpace;
	}
	
	/**
	 * @return the reservedSpace
	 */
	public long getReservedSpace() {
		return reservedSpace;
	}
	
	/**
	 * @return the totalSpace
	 */
	public long getTotalSpace() {
		return totalSpace;
	}
	
	/**
	 * Real One
	 * freeSpace = totalSpace - used - reserved
	 * For now...
	 * freeSpace = totalSpace - used
	 * @return the freeSpace
	 */
    public long getFreeSpace()
    {
        if (this.totalSpace >= 0)
        {
            //For now do not consider the reserved space, a better management is needed
//                this.freeSpace = this.totalSpace - this.usedSpace - this.reservedSpace;
            return this.totalSpace - this.usedSpace;
        }
        else
        {
            return -1;
        }
    }
	
	
	/*****************************
	 *  SETTER methods
	 ****************************/		
	
	/**
	 * @param usedSpace the usedSpace to set
	 */
	public void setUsedSpace(long usedSpace) {
		this.usedSpace = usedSpace;
	}
	/**
	 * @param unavailableSpace the unavailableSpace to set
	 */
	public void setUnavailableSpace(long unavailableSpace) {
		this.unavailableSpace = unavailableSpace;
	}
	/**
	 * @param reservedSpace the reservedSpace to set
	 */
	public void setReservedSpace(long reservedSpace) {
		this.reservedSpace = reservedSpace;
	} 
	
    
	/*******************************
	 *  JSON Building 
	 */
	

	
	/**
	 
	 String saAlias;     
	 long busySpace;        // busySpace = used + unavailable + reserved 
	 long usedSpace;        //info retrieved by sensors 
	 long unavailableSpace; // info retrieved by sensors 
	 long reservedSpace;    // info retrieved from DB 
	 long totalSpace;       // defined in config/db (static value) 
	 long freeSpace;        // freeSpace = totalSpace - used - reserved; 
	 */
	public String getJsonFormat() {
		String result = "";
		StringWriter strWriter = new StringWriter();
        Configuration config = new Configuration();
        MappedNamespaceConvention con = new MappedNamespaceConvention(config);

        try {
            AbstractXMLStreamWriter w = new MappedXMLStreamWriter(con, strWriter);
            w.writeStartDocument();
            // start main element
            w.writeStartElement("sa-status");
            // Alias
            w.writeStartElement("alias");
            w.writeCharacters(this.getSaAlias());
		    w.writeEndElement();
		    // busy space
            w.writeStartElement("busy-space");
            w.writeCharacters(""+this.getBusySpace());
		    w.writeEndElement();
		    // used space
            w.writeStartElement("used-space");
            w.writeCharacters(""+this.getUsedSpace());
		    w.writeEndElement();
		    // unavailable space
            w.writeStartElement("unavailable-space");
            w.writeCharacters(""+this.getUnavailableSpace());
		    w.writeEndElement();
		    // reserved space
            w.writeStartElement("reserved-space");
            w.writeCharacters(""+this.getReservedSpace());
		    w.writeEndElement();
		    // total space
            w.writeStartElement("total-space");
            w.writeCharacters(""+this.getTotalSpace());
		    w.writeEndElement();
		    // free space
            w.writeStartElement("free-space");
            w.writeCharacters(""+this.getFreeSpace());
		    w.writeEndElement();
            // available space
            w.writeStartElement("available-space");
            w.writeCharacters(""+this.getAvailableSpace());
            w.writeEndElement();		    
            // end main element
	        w.writeEndElement();
	        w.writeEndDocument();
	        w.close();   
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
		try {
			strWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result = strWriter.toString();
		return result;
	}

	public void generateRandomValues() {
		this.totalSpace = Long.MAX_VALUE;
		this.usedSpace = Math.round((Math.random()*32768*32768*32));
		this.unavailableSpace = Math.round((Math.random()*32768*32768*4));
		this.reservedSpace = Math.round((Math.random()*32768*32768*4));
	}

    @Override
    public String toString() {
        return "SpaceStatusSummary [getSaAlias()=" + getSaAlias() + ", getBusySpace()=" + getBusySpace() + ", getAvailableSpace()="
                + getAvailableSpace() + ", getUsedSpace()=" + getUsedSpace() + ", getUnavailableSpace()=" + getUnavailableSpace()
                + ", getReservedSpace()=" + getReservedSpace() + ", getTotalSpace()=" + getTotalSpace() + ", getFreeSpace()="
                + getFreeSpace() + "]";
    }

//    /* (non-Javadoc)
//     * @see java.lang.Object#toString()
//     */
//    @Override
//    public String toString()
//    {
//        return "SpaceStatusSummary [busySpace=" + busySpace + ", freeSpace=" + freeSpace + ", reservedSpace=" + reservedSpace
//                + ", saAlias=" + saAlias + ", totalSpace=" + totalSpace + ", unavailableSpace=" + unavailableSpace + ", usedSpace="
//                + usedSpace + "]";
//    }
    
    

//	public void retrieveFromDB(String saAlias) {
//		ReservedSpaceCatalog catalog = new ReservedSpaceCatalog();
//		StorageSpaceData ssd = catalog.getStorageSpaceByAlias(saAlias);
//		if (ssd!=null) {
//			this.usedSpace = ssd.getUsedSpaceSize().value();
//			this.freeSpace = ssd.getFreeSpaceSize().value();
//			this.busySpace = ssd.getBusySpaceSize().value();
//			this.reservedSpace = ssd.getReservedSpaceSize().value();
//			this.unavailableSpace = ssd.getUnavailableSpaceSize().value();
//		}
//	}
	
	
}