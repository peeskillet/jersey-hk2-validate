
package com.underdog.jersey.hk2.validate;

import javax.validation.Valid;


public class ModelServiceImpl implements ModelService, Validatable {

    @Override
    public Model save(@Valid Model model) {
        model.setId(100);
        return model;
    } 

    @Override
    public void saveTwo(@Valid Model model1, @Valid Model model2) {
        // noop
    }
}
