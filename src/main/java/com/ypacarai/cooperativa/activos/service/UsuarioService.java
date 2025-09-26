package com.ypacarai.cooperativa.activos.service;

import com.ypacarai.cooperativa.activos.dao.UsuarioDAO;
import com.ypacarai.cooperativa.activos.model.Usuario;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de usuarios
 * Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA
 */
public class UsuarioService {
    
    private final UsuarioDAO usuarioDAO;
    
    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAO();
    }
    
    /**
     * Autentica un usuario con username y password
     */
    public Usuario autenticarUsuario(String username, String password) {
        try {
            Optional<Usuario> usuarioOpt = usuarioDAO.authenticate(username, password);
            return usuarioOpt.orElse(null);
        } catch (Exception e) {
            System.err.println("Error al autenticar usuario: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Obtiene todos los usuarios activos
     */
    public List<Usuario> obtenerTodosLosUsuarios() {
        try {
            return usuarioDAO.findAll();
        } catch (Exception e) {
            System.err.println("Error al obtener usuarios: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Busca un usuario por ID
     */
    public Usuario buscarUsuarioPorId(int id) {
        try {
            Optional<Usuario> usuarioOpt = usuarioDAO.findById(id);
            return usuarioOpt.orElse(null);
        } catch (Exception e) {
            System.err.println("Error al buscar usuario por ID: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Busca un usuario por nombre de usuario
     */
    public Usuario buscarUsuarioPorUsername(String username) {
        try {
            Optional<Usuario> usuarioOpt = usuarioDAO.findByUsername(username);
            return usuarioOpt.orElse(null);
        } catch (Exception e) {
            System.err.println("Error al buscar usuario por username: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Crea un nuevo usuario
     */
    public boolean crearUsuario(Usuario usuario) {
        try {
            // Validaciones básicas
            if (usuario.getUsuNombre() == null || usuario.getUsuNombre().trim().isEmpty()) {
                System.err.println("Error: El nombre del usuario es obligatorio");
                return false;
            }
            
            if (usuario.getUsuUsuario() == null || usuario.getUsuUsuario().trim().isEmpty()) {
                System.err.println("Error: El nombre de usuario es obligatorio");
                return false;
            }
            
            if (usuario.getUsuPassword() == null || usuario.getUsuPassword().trim().isEmpty()) {
                System.err.println("Error: La contraseña es obligatoria");
                return false;
            }
            
            // Verificar que no exista otro usuario con el mismo username
            Usuario existente = buscarUsuarioPorUsername(usuario.getUsuUsuario());
            if (existente != null) {
                System.err.println("Error: Ya existe un usuario con el username " + usuario.getUsuUsuario());
                return false;
            }
            
            // Establecer valores por defecto
            if (usuario.getUsuRol() == null) {
                usuario.setUsuRol(Usuario.Rol.Consulta);
            }
            
            usuario.setActivo(true);
            
            // Guardar en la base de datos
            Usuario usuarioGuardado = usuarioDAO.save(usuario);
            return usuarioGuardado != null && usuarioGuardado.getUsuId() > 0;
            
        } catch (Exception e) {
            System.err.println("Error al crear usuario: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Actualiza un usuario existente
     */
    public boolean actualizarUsuario(Usuario usuario) {
        try {
            // Validaciones básicas
            if (usuario.getUsuId() <= 0) {
                System.err.println("Error: ID del usuario es requerido para actualización");
                return false;
            }
            
            // Verificar que el usuario existe
            Usuario existente = buscarUsuarioPorId(usuario.getUsuId());
            if (existente == null) {
                System.err.println("Error: Usuario no encontrado para actualizar");
                return false;
            }
            
            if (usuario.getUsuNombre() == null || usuario.getUsuNombre().trim().isEmpty()) {
                System.err.println("Error: El nombre del usuario es obligatorio");
                return false;
            }
            
            // Actualizar en la base de datos
            usuarioDAO.update(usuario);
            return true;
            
        } catch (Exception e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Desactiva un usuario (eliminación lógica)
     */
    public boolean desactivarUsuario(int usuarioId) {
        try {
            // Verificar que el usuario existe
            Usuario usuario = buscarUsuarioPorId(usuarioId);
            if (usuario == null) {
                System.err.println("Error: Usuario no encontrado");
                return false;
            }
            
            // Desactivar usuario
            usuarioDAO.delete(usuarioId);
            return true;
            
        } catch (Exception e) {
            System.err.println("Error al desactivar usuario: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtiene estadísticas básicas de usuarios
     */
    public String obtenerEstadisticasUsuarios() {
        try {
            List<Usuario> todosLosUsuarios = usuarioDAO.findAll();
            
            int total = todosLosUsuarios.size();
            int activos = (int) todosLosUsuarios.stream().filter(Usuario::isActivo).count();
            int jefeInformatica = (int) todosLosUsuarios.stream()
                .filter(u -> u.getUsuRol() == Usuario.Rol.Jefe_Informatica).count();
            int tecnicos = (int) todosLosUsuarios.stream()
                .filter(u -> u.getUsuRol() == Usuario.Rol.Tecnico).count();
            int consulta = (int) todosLosUsuarios.stream()
                .filter(u -> u.getUsuRol() == Usuario.Rol.Consulta).count();
            
            StringBuilder stats = new StringBuilder();
            stats.append("=== ESTADÍSTICAS DE USUARIOS ===\n");
            stats.append("Total de usuarios: ").append(total).append("\n");
            stats.append("Usuarios activos: ").append(activos).append("\n");
            stats.append("Jefe de Informática: ").append(jefeInformatica).append("\n");
            stats.append("Técnicos: ").append(tecnicos).append("\n");
            stats.append("Consulta: ").append(consulta).append("\n");
            
            return stats.toString();
            
        } catch (Exception e) {
            return "Error al obtener estadísticas: " + e.getMessage();
        }
    }
    
    /**
     * Verifica si un usuario tiene permisos de administrador
     */
    public boolean esAdministrador(Usuario usuario) {
        return usuario != null && usuario.getUsuRol() == Usuario.Rol.Jefe_Informatica;
    }
    
    /**
     * Verifica permisos para operaciones específicas
     */
    public boolean tienePermiso(Usuario usuario, String operacion) {
        if (usuario == null || !usuario.isActivo()) {
            return false;
        }
        
        switch (operacion.toLowerCase()) {
            case "crear_usuario":
            case "editar_usuario":
            case "eliminar_usuario":
                return usuario.getUsuRol() == Usuario.Rol.Jefe_Informatica;
                
            case "ver_activos":
            case "crear_ticket":
                return true; // Todos los usuarios pueden ver activos y crear tickets
                
            case "crear_activo":
            case "editar_activo":
            case "eliminar_activo":
                return usuario.getUsuRol() == Usuario.Rol.Jefe_Informatica ||
                       usuario.getUsuRol() == Usuario.Rol.Tecnico;
                
            case "asignar_ticket":
            case "resolver_ticket":
                return usuario.getUsuRol() == Usuario.Rol.Jefe_Informatica ||
                       usuario.getUsuRol() == Usuario.Rol.Tecnico;
                
            default:
                return false;
        }
    }
}
