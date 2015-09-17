
package com.underdog.jersey.hk2.validate;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;

public class Model {
    
    private int id;
    
    @NotNull
    @NotEmpty
    private String firstName;
    
    @NotNull
    @NotEmpty
    private String lastName;
    
    public Model(){}

    public Model(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }  
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
