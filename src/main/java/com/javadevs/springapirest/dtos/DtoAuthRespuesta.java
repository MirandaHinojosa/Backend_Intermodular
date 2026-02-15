package com.javadevs.springapirest.dtos;


import lombok.Data;

//Esta clase va a ser la que nos devolverá la información con el token y el tipo que tenga este
@Data
public class DtoAuthRespuesta {

    private String accessToken;
    private String tokenType = "Bearer ";

    private String role;

    public DtoAuthRespuesta(String accessToken, String role) {
        this.accessToken = accessToken;
        this.role = role;
    }
}
