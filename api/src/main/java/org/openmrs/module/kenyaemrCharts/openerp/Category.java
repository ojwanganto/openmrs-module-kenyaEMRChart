package org.openmrs.module.kenyaemrCharts.openerp;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "category")
public class Category implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "category_id")
    protected long categoryId;

    @Column(name = "category_name")
    protected String name;

    @Column(name = "location_id")
    protected long location_id;

    @Column(name = "location_string")
    protected String locationString;

    @Column(name = "location_object")
    protected OdooLocation locationObject;

    @Column(name = "parent_category_id")
    protected long parent_category_id;

    @Column(name = "category_childs")
    @ElementCollection(targetClass=Long.class)
    protected List<Long> child_categories;

    public Category(){
        child_categories = new ArrayList<Long>();
    }

    public Category(long aCategoryId, String aCategoryName,long location_id){
        categoryId = aCategoryId;
        name = aCategoryName;
        child_categories = new ArrayList<Long>();
    }

    @JsonProperty("categoryId")
    public long getCategoryId() {return categoryId;}
    public void setCategoryId(long categoryId) {this.categoryId = categoryId;}

    @JsonProperty("name")
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    @JsonProperty("parentCategoryId")
    public void setParent_category_id(long parent_category_id) {this.parent_category_id = parent_category_id;}
    public long getParent_category_id() {return parent_category_id;}

    @JsonProperty("locationString")
    public void setLocationString(String locationString) {this.locationString = locationString;}
    public String getLocationString() {return locationString;}

    @JsonProperty("categoryChilds")
    public List<Long> getCategories() {return child_categories;}
    public void setCategories(List<Long> categories) {this.child_categories = categories;}

    @JsonProperty("location")
    public void setLocationObject(OdooLocation locationObject) {this.locationObject = locationObject;}
    public OdooLocation getLocationObject() {return locationObject;}


    @JsonProperty
    public void addCategory(long aCategoryId){
        child_categories.add(aCategoryId);
    }

    @JsonIgnore
    public long getLocation_id() {return location_id;}
    public void setLocation_id(long location_id) {this.location_id = location_id;}

    @JsonIgnore
    public long getId() {return id;}
    public void setId(long id) {this.id = id;}

    @Override
    public String toString(){
        return "Category: " + "id: " + String.valueOf(getCategoryId()) + " -Name: "+ getName() + " -LocationId: " + getLocation_id() + " -LocationString: " + getLocationString() + " -ParentId: " + getParent_category_id();
    }
}
