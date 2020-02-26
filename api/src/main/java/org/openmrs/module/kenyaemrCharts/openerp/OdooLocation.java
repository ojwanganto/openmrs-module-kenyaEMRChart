

package org.openmrs.module.kenyaemrCharts.openerp;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "location")
public class OdooLocation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;

    @Column(name = "locationId")
    protected long locationId;

    @Column(name = "name")
    protected String name;

    @Column(name = "code")
    protected String code;

    public OdooLocation(){
    }

    @JsonIgnore
    public void setId(long id) {
        this.id = id;
    }
    public long getId() {
        return id;
    }


    @JsonProperty("locationId")
    public void setLocationId(long aLocationId){locationId = aLocationId;}
    public long getLocationId(){return locationId;}

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    @JsonProperty("code")
    public void setCode(String code) {this.code = code;}
    public String getCode() {return code;}

    @Override
    public String toString(){
        return "OdooLocation: " + "id: " + String.valueOf(getLocationId()) + " -Name: "+ getName() + " -Code: " + getCode();
    }


}