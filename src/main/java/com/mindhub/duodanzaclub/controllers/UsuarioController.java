package com.mindhub.duodanzaclub.controllers;

import com.mindhub.duodanzaclub.dtos.SolicitudDTO;
import com.mindhub.duodanzaclub.dtos.UsuarioDTO;
import com.mindhub.duodanzaclub.models.Clase;
import com.mindhub.duodanzaclub.models.Usuario;
import com.mindhub.duodanzaclub.models.UsuarioClase;
import com.mindhub.duodanzaclub.repositories.ClaseRepository;
import com.mindhub.duodanzaclub.repositories.UsuarioClaseRepository;
import com.mindhub.duodanzaclub.services.UsuarioService;
import com.mindhub.duodanzaclub.utils.UsuarioClaseUtils;
import com.mindhub.duodanzaclub.utils.UsuarioUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class UsuarioController {

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UsuarioService usuarioService;
    @Autowired
    ClaseRepository claseRepository;
    @Autowired
    UsuarioClaseRepository usuarioClaseRepository;

    @GetMapping("/usuarios")
    public List<UsuarioDTO> getUsuarios(){
        List<UsuarioDTO> usuarioDTOS = usuarioService.getUsuarios();
        return usuarioDTOS;
    }

    @GetMapping("/usuarios/{id}")
    public UsuarioDTO getUsuario(@PathVariable long id){
        UsuarioDTO usuarioDTO = usuarioService.getById(id);
        return usuarioDTO;
    }


    @GetMapping("/usuarios/current")
    public UsuarioDTO getCurrentUsuario(Authentication authentication){
        UsuarioDTO usuarioDTO = usuarioService.findByEmail(authentication.getName());
        return usuarioDTO;
    }

    @PostMapping("/usuarios")
    public ResponseEntity<Object> register(@RequestBody UsuarioDTO usuarioDTO){
        Usuario usuario = usuarioService.findUsuarioByEmail(usuarioDTO.getEmail());
        int anio = usuarioDTO.getAnio();
        int mes = usuarioDTO.getMes();
        int dia = usuarioDTO.getDia();
        LocalDate fechaNacimiento = LocalDate.of(anio, mes, dia);

        if(usuarioDTO.getNombre().isEmpty() || usuarioDTO.getApellido().isEmpty() || usuarioDTO.getEmail().isEmpty() ||
                usuarioDTO.getTelefono().isEmpty() || usuarioDTO.getPassword().isEmpty()){
            return new ResponseEntity<>("Campos faltantes", HttpStatus.FORBIDDEN);
        }
        if(anio < 1912 || anio > LocalDate.now().getYear() || mes < 1 || mes > 12 || dia <= 0 || dia > 31){
            return new ResponseEntity<>("Fecha de nacimiento no valida", HttpStatus.FORBIDDEN);
        }
        if(usuario != null){
            return new ResponseEntity<>("El usuario ya existe", HttpStatus.FORBIDDEN);
        }

        Usuario nuevoUsuario = new Usuario(usuarioDTO.getNombre(), usuarioDTO.getApellido(), usuarioDTO.getTelefono(),
                usuarioDTO.getEmail(), passwordEncoder.encode(usuarioDTO.getPassword()), fechaNacimiento);
        usuarioService.saveUsuario(usuario);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/usuarios/agregar")
    public ResponseEntity<Object> agregarContacto(Authentication authentication, @RequestBody SolicitudDTO solicitudDTO){
        Usuario usuario1 = usuarioService.findUsuarioByEmail(authentication.getName());
        Usuario usuario2 = usuarioService.getUsuarioById(solicitudDTO.getUsuario());
        List<Usuario> usuarios = new ArrayList<>(usuario1.getFollowing());

        if(usuario1 == null || usuario2 == null){
            return new ResponseEntity<>("Usuario invalido", HttpStatus.FORBIDDEN);
        }
        if(usuario1 == usuario2){
            return new ResponseEntity<>("Los usuarios son los mismos", HttpStatus.FORBIDDEN);
        }
        if(usuarios.size() > 0){
            Boolean sonContactos = UsuarioUtils.buscarEntreContactos(usuario2, usuarios);
            if(sonContactos){
                return new ResponseEntity<>("El usuario ya sigue a este usuario", HttpStatus.FORBIDDEN);
            }
        }

        usuario1.getFollowing().add(usuario2);
        usuario2.getFollowers().add(usuario1);
        if(usuario1.getFollowing().contains(usuario2) && usuario1.getFollowers().contains(usuario2)){
            usuario1.getContactos().add(usuario2.getId());
            usuario2.getContactos().add(usuario1.getId());
        }
        usuarioService.saveUsuario(usuario1);
        usuarioService.saveUsuario(usuario2);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PatchMapping("/usuarios/borrar")
    public ResponseEntity<Object> borrarContacto(Authentication authentication, @RequestBody SolicitudDTO solicitudDTO){
        Usuario usuario1 = usuarioService.findUsuarioByEmail(authentication.getName());
        Usuario usuario2 = usuarioService.getUsuarioById(solicitudDTO.getUsuario());
        List<Usuario> following = new ArrayList<Usuario>(usuario1.getFollowing());
        List<Long> contactos = usuario1.getContactos();


        if(usuario1 == null || usuario2 == null){
            return new ResponseEntity<>("Usuario invalido", HttpStatus.FORBIDDEN);
        }
        if(usuario1 == usuario2){
            return new ResponseEntity<>("El usuario destino es el usuario autentificado", HttpStatus.FORBIDDEN);
        }
        // Veo si el usuario sigue a otros usuarios
        if(following.size() < 1){
            return new ResponseEntity<>("No se sigue a ningún usuario", HttpStatus.FORBIDDEN);
        }
        else{
            // Compruebo que el usuario siga al otro usuario
            Boolean existeFollow = UsuarioUtils.buscarEntreContactos(usuario2, following);
            if(!existeFollow){
                return new ResponseEntity<>("No se sigue a ese usuario", HttpStatus.FORBIDDEN);
            }
        }
        //Borrar contacto si lo son
        if(contactos.size() > 0){
            Boolean existeContacto = UsuarioUtils.buscarEntreContactosPorID(usuario2.getId(), contactos);
            if(existeContacto){
                contactos.remove(usuario2.getId());
                usuario2.getContactos().remove(usuario1.getId());
            }
        }

        usuario1.getFollowing().remove(usuario2);
        usuario2.getFollowers().remove(usuario1);
        usuarioService.saveUsuario(usuario1);
        usuarioService.saveUsuario(usuario2);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}