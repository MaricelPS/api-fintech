package com.utn.fintech.service;

import com.utn.fintech.dto.CuentaRequest;
import com.utn.fintech.dto.CuentaResponse;
import com.utn.fintech.exception.CuentaNoEncontradaException;
import com.utn.fintech.exception.RecursoDuplicadoException;
import com.utn.fintech.exception.UsuarioNoEncontradoException;
import com.utn.fintech.mapper.CuentaMapper;
import com.utn.fintech.model.CajaAhorro;
import com.utn.fintech.model.Cuenta;
import com.utn.fintech.model.CuentaCorriente;
import com.utn.fintech.model.Usuario;
import com.utn.fintech.repository.CuentaRepository;
import com.utn.fintech.repository.UsuarioRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class CuentaServiceImpl implements CuentaService {

    private final CuentaRepository cuentaRepository;
    private final UsuarioRepository usuarioRepository;

    public CuentaServiceImpl(CuentaRepository cuentaRepository,
                             UsuarioRepository usuarioRepository) {
        this.cuentaRepository = cuentaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentaResponse> listar() {
        return cuentaRepository.findAll().stream()
                .map(CuentaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CuentaResponse buscarPorId(Long id) {
        return CuentaMapper.toResponse(obtenerCuenta(id));
    }

    @Override
    public CuentaResponse crear(CuentaRequest request) {
        if (cuentaRepository.existsByNumero(request.numero())) {
            throw new RecursoDuplicadoException("Ya existe una cuenta con el número " + request.numero());
        }

        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new UsuarioNoEncontradoException(request.usuarioId()));

        // Mini "factory": decide qué subclase crear según el tipo
        Cuenta cuenta = switch (request.tipo()) {
            case CAJA_AHORRO -> new CajaAhorro();
            case CUENTA_CORRIENTE -> new CuentaCorriente(request.limiteDescubierto());
        };
        cuenta.setNumero(request.numero());
        usuario.agregarCuenta(cuenta);

        return CuentaMapper.toResponse(guardar(cuenta));
    }

    @Override
    public CuentaResponse actualizar(Long id, CuentaRequest request) {
        Cuenta cuenta = obtenerCuenta(id);

        if (cuenta.getTipo() != request.tipo()) {
            throw new IllegalArgumentException("No se puede cambiar el tipo de una cuenta existente");
        }

        boolean cambiaNumero = !cuenta.getNumero().equals(request.numero());
        if (cambiaNumero && cuentaRepository.existsByNumero(request.numero())) {
            throw new RecursoDuplicadoException("Ya existe una cuenta con el número " + request.numero());
        }

        cuenta.setNumero(request.numero());

        if (cuenta instanceof CuentaCorriente corriente && request.limiteDescubierto() != null) {
            corriente.setLimiteDescubierto(request.limiteDescubierto());
        }

        return CuentaMapper.toResponse(guardar(cuenta));
    }

    @Override
    public void eliminar(Long id) {
        if (!cuentaRepository.existsById(id)) {
            throw new CuentaNoEncontradaException(id);
        }
        cuentaRepository.deleteById(id);
    }

    @Override
    public CuentaResponse depositar(Long id, BigDecimal monto) {
        Cuenta cuenta = obtenerCuenta(id);
        cuenta.depositar(monto);
        return CuentaMapper.toResponse(cuentaRepository.save(cuenta));
    }

    @Override
    public CuentaResponse extraer(Long id, BigDecimal monto) {
        Cuenta cuenta = obtenerCuenta(id);
        cuenta.extraer(monto); // polimorfismo: cada tipo de cuenta valida distinto
        return CuentaMapper.toResponse(cuentaRepository.save(cuenta));
    }

    // ---------- Métodos auxiliares ----------

    private Cuenta obtenerCuenta(Long id) {
        return cuentaRepository.findById(id)
                .orElseThrow(() -> new CuentaNoEncontradaException(id));
    }

    private Cuenta guardar(Cuenta cuenta) {
        try {
            return cuentaRepository.saveAndFlush(cuenta);
        } catch (DataIntegrityViolationException e) {
            throw new RecursoDuplicadoException("Ya existe una cuenta con el número " + cuenta.getNumero());
        }
    }
}
