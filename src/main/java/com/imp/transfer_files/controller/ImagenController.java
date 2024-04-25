package com.imp.transfer_files.controller;

import com.imp.transfer_files.entity.Imagen;
import com.imp.transfer_files.repository.ImagenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@RestController
@RequestMapping("/files")
public class ImagenController {
    //Valor o carpeta donde se almacenaran los archivos locales
    @Value("${ruta.imagenes}")
    private String rutaArchivos;
    //URL que le pasaremos con el archivo al cliente para su consumo
    private final String rutaURL = "http://localhost:8080/images/";
    @Autowired
    private ImagenRepository imagenRepository;

    @PostMapping("/test")
    ResponseEntity<String> subirArchivo(@RequestPart(name = "file") MultipartFile file) {
        String message = "El archivo no pudo ser guardado";

        if (file != null) {
            try {
                String fileName = file.getOriginalFilename();
                byte[] bytesFile = file.getBytes();

                Path folderSave = Paths.get(rutaArchivos+fileName);
                if(Files.exists(folderSave)) {
                    message = "El archivo ya se encuentra guardado";
                    return new ResponseEntity<>(message,HttpStatus.NOT_ACCEPTABLE);
                }
                Files.write(folderSave,bytesFile);
                message = "El archivo se guardo correctamente";
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return ResponseEntity.ok(message);
    }

    @PostMapping("/uploadObject")
    ResponseEntity<?> subirArchivoComoObjeto(@RequestParam("titulo") String titulo,
                                                  @RequestParam("descripcion") String descripcion,
                                                  @RequestPart(name = "file") MultipartFile file) {
        //Validamos si el cliente nos pasa un archivo
        if (file != null) {
            try {
                //Conseguimos el nombre del archivo pasado por el cliente
                String fileName = file.getOriginalFilename();
                //Convertirmos los datos del archivo a Bytes
                byte[] bytesFile = file.getBytes();

                //Asignamos el espacio donde se creara el archivo (nuestro directorio local junto con)
                //nombre del archivo
                Path folderSave = Paths.get(rutaArchivos,fileName);
                //En caso que el archivo ya este existente en nuestro entorno local, se lo haremos saber al cliente
                if(Files.exists(folderSave)) {
                    return new ResponseEntity<>("El archivo ya se encuentra guardado"
                            ,HttpStatus.NOT_ACCEPTABLE);
                }
                //Objeto que almacenara los datos que posteriormente mandaremos al cliente
                Imagen imagen = new Imagen(titulo,descripcion,rutaURL+fileName);
                //Guardamos el archivo de manera local
                Files.write(folderSave,bytesFile);
                //Le regresamos como ResponseEntity el objeto que almacena todos los datos del cliente
                return ResponseEntity.ok(imagenRepository.save(imagen));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return new ResponseEntity<>("No agrego ningun archivo",HttpStatus.NO_CONTENT);
    }
    @GetMapping("/curso/{id}")
    ResponseEntity<?> pedirCurso(@PathVariable Long id) {
        Optional<Imagen> imagenBBDD = imagenRepository.findById(id);
        if(imagenBBDD.isEmpty()) {return new ResponseEntity<>("No se encontro el curso con ID: "+id,
                HttpStatus.NOT_FOUND);}
        return ResponseEntity.ok(imagenBBDD.get());
    }
}
