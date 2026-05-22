/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dtos;

/**
 *
 * @author Kaleb
 */
public class ConfiguracionCamaraDTO {

    private String URLCAMARA = "http://192.168.1.13:8080/shot.jpg?t=";

    public ConfiguracionCamaraDTO() {
    }

    public String getURLCAMARA() {
        return URLCAMARA;
    }

    public void setURLCAMARA(String URLCAMARA) {
        this.URLCAMARA = URLCAMARA;
    }

}
