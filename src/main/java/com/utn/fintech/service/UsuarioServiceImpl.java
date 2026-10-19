package com.utn.fintech.service;

import com.utn.fintech.dto.UsuarioRequest;
import com.utn.fintech.dto.UsuarioResponse;
import com.utn.fintech.exception.RecursoDuplicadoException;
import com.utn.fintech.exception.UsuarioNoEncontradoException;
import com.utn.fintech.mapper.UsuarioMapper;
import com.utn.fintech.model.Usuario;
import com.utn.fintech.repository.UsuarioRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(id));
        return UsuarioMapper.toResponse(usuario);
    }

    @Override
    public UsuarioResponse crear(UsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new RecursoDuplicadoException("Ya existe un usuario con el email " + request.email());
        }
        if (usuarioRepository.existsByDni(request.dni())) {
            throw new RecursoDuplicadoException("Ya existe un usuario con el DNI " + request.dni());
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre());
        usuario.setEmail(request.email());
        usuario.setDni(request.dni());

        try {
            return UsuarioMapper.toResponse(usuarioRepository.saveAndFlush(usuario));
        } catch (DataIntegrityViolationException e) {
            throw new RecursoDuplicadoException("El email o DNI ya está registrado");
        }
    }
}