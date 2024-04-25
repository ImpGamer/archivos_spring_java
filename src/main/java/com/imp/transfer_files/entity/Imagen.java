package com.imp.transfer_files.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "FILES")
@NoArgsConstructor
@Data

public class Imagen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private String descripcion;
    private String URL;

    public Imagen(String titulo, String descripcion, String URL) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.URL = URL;
    }
}