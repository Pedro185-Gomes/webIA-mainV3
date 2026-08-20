package com.example.webIA.Service;

import com.example.webIA.Model.Usuario;
import com.example.webIA.Repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder encoder;

    public void salvar (Usuario usuario){
        var senha = usuario.getSenha();
        usuario.setSenha(encoder.encode(senha));
        usuarioRepository.save(usuario);

    }
    public Usuario obterLogin(String usuario){
        return usuarioRepository.findByLogin(usuario);

    }

}
