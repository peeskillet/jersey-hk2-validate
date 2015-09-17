/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.underdog.jersey.hk2.validate;

/**
 *
 * @author PaulSamsotha
 */
public interface ModelService {
    Model save(Model model);
    void saveTwo(Model model1, Model model2);
}
