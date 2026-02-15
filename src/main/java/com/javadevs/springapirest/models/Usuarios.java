package com.javadevs.springapirest.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "usuario")

public class Usuarios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;
    private String username;
    private String password;
    private String email;
    private String nombre;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)

    @JoinTable(name = "usuarios_roles", joinColumns = @JoinColumn(name = "usuario_id", referencedColumnName = "id_usuario")
            ,inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id_role"))
    private List<Roles> roles = new ArrayList<>();

    //Relación inversa con Boletos
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    @JsonIgnore // Para evitar recursión infinita en JSON
    private List<Boletos> boletos = new ArrayList<>();

}
