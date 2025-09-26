package com.ypacarai.cooperativa.activos.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import com.ypacarai.cooperativa.activos.dao.UsuarioDAO;
import com.ypacarai.cooperativa.activos.model.Usuario;
import com.ypacarai.cooperativa.activos.model.Usuario.Rol;

/**
 * Servicio completo para gestión de usuarios y permisos por rol
 */
public class GestionUsuariosService {
    
    private final UsuarioDAO usuarioDAO;
    
    // Patrones para validaciones
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9._-]{3,20}$");
    private static final int MIN_PASSWORD_LENGTH = 6;
    
    public GestionUsuariosService() {
        this.usuarioDAO = new UsuarioDAO();
    }
    
    public GestionUsuariosService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }
    
    // ===== GESTIÓN DE USUARIOS =====
    
    /**
     * Crea un nuevo usuario con validaciones completas
     */
    public ResultadoOperacion crearUsuario(String nombre, String username, String password, 
                                         String email, Rol rol, Integer usuarioCreadorId) {
        
        // Validar datos de entrada
        ResultadoOperacion validacion = validarDatosUsuario(nombre, username, password, email, rol);
        if (!validacion.isExitoso()) {
            return validacion;
        }
        
        // Verificar que el username no existe
        if (usuarioDAO.findByUsername(username).isPresent()) {
            return new ResultadoOperacion(false, "El nombre de usuario ya existe");
        }
        
        // Verificar que el email no existe
        if (existeEmail(email)) {
            return new ResultadoOperacion(false, "El email ya está registrado");
        }
        
        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setUsuNombre(nombre);
        usuario.setUsuUsuario(username);
        usuario.setUsuPassword(hashPassword(password));
        usuario.setUsuEmail(email);
        usuario.setUsuRol(rol);
        usuario.setActivo(true);
        usuario.setCreadoEn(LocalDateTime.now());
        usuario.setActualizadoEn(LocalDateTime.now());
        
        Usuario usuarioGuardado = usuarioDAO.save(usuario);
        
        if (usuarioGuardado != null && usuarioGuardado.getUsuId() > 0) {
            registrarAuditoriaUsuario(usuario.getUsuId(), usuarioCreadorId, "USUARIO_CREADO", 
                                    "Usuario creado: " + username + " con rol " + rol);
            return new ResultadoOperacion(true, "Usuario creado exitosamente", usuario.getUsuId());
        } else {
            return new ResultadoOperacion(false, "Error al guardar usuario en base de datos");
        }
    }
    
    /**
     * Actualiza un usuario existente
     */
    public ResultadoOperacion actualizarUsuario(Integer usuarioId, String nombre, String email, 
                                              Rol rol, Integer usuarioModificadorId) {
        
        Optional<Usuario> usuarioOpt = usuarioDAO.findById(usuarioId);
        if (!usuarioOpt.isPresent()) {
            return new ResultadoOperacion(false, "Usuario no encontrado");
        }
        
        Usuario usuario = usuarioOpt.get();
        
        // Validar datos
        if (nombre == null || nombre.trim().isEmpty()) {
            return new ResultadoOperacion(false, "El nombre es obligatorio");
        }
        
        if (email != null && !email.trim().isEmpty() && !EMAIL_PATTERN.matcher(email).matches()) {
            return new ResultadoOperacion(false, "Email inválido");
        }
        
        // Verificar que el email no esté usado por otro usuario
        if (email != null && !email.equals(usuario.getUsuEmail()) && existeEmail(email)) {
            return new ResultadoOperacion(false, "El email ya está registrado por otro usuario");
        }
        
        // Actualizar datos
        String nombreAnterior = usuario.getUsuNombre();
        Rol rolAnterior = usuario.getUsuRol();
        
        usuario.setUsuNombre(nombre.trim());
        usuario.setUsuEmail(email != null ? email.trim() : null);
        usuario.setUsuRol(rol);
        usuario.setActualizadoEn(LocalDateTime.now());
        
        boolean actualizado = usuarioDAO.update(usuario);
        
        if (actualizado) {
            registrarAuditoriaUsuario(usuarioId, usuarioModificadorId, "USUARIO_MODIFICADO", 
                                    String.format("Datos actualizados. Nombre: %s -> %s, Rol: %s -> %s", 
                                                 nombreAnterior, nombre, rolAnterior, rol));
            return new ResultadoOperacion(true, "Usuario actualizado exitosamente");
        } else {
            return new ResultadoOperacion(false, "Error al actualizar usuario");
        }
    }
    
    /**
     * Cambia la contraseña de un usuario
     */
    public ResultadoOperacion cambiarPassword(Integer usuarioId, String passwordActual, 
                                            String passwordNueva, Integer usuarioModificadorId) {
        
        Optional<Usuario> usuarioOpt = usuarioDAO.findById(usuarioId);
        if (!usuarioOpt.isPresent()) {
            return new ResultadoOperacion(false, "Usuario no encontrado");
        }
        
        Usuario usuario = usuarioOpt.get();
        
        // Verificar contraseña actual
        if (!verificarPassword(passwordActual, usuario.getUsuPassword())) {
            return new ResultadoOperacion(false, "La contraseña actual es incorrecta");
        }
        
        // Validar nueva contraseña
        if (passwordNueva == null || passwordNueva.length() < MIN_PASSWORD_LENGTH) {
            return new ResultadoOperacion(false, "La contraseña debe tener al menos " + MIN_PASSWORD_LENGTH + " caracteres");
        }
        
        // Actualizar contraseña
        usuario.setUsuPassword(hashPassword(passwordNueva));
        usuario.setActualizadoEn(LocalDateTime.now());
        
        boolean actualizado = usuarioDAO.update(usuario);
        
        if (actualizado) {
            registrarAuditoriaUsuario(usuarioId, usuarioModificadorId, "PASSWORD_CAMBIADA", 
                                    "Contraseña cambiada exitosamente");
            return new ResultadoOperacion(true, "Contraseña actualizada exitosamente");
        } else {
            return new ResultadoOperacion(false, "Error al actualizar contraseña");
        }
    }
    
    /**
     * Resetea la contraseña de un usuario (solo admin)
     */
    public ResultadoOperacion resetearPassword(Integer usuarioId, String passwordNueva, 
                                             Integer adminId) {
        
        // Verificar que quien hace el reset es admin
        Optional<Usuario> adminOpt = usuarioDAO.findById(adminId);
        if (!adminOpt.isPresent() || adminOpt.get().getUsuRol() != Rol.Jefe_Informatica) {
            return new ResultadoOperacion(false, "No tienes permisos para resetear contraseñas");
        }
        
        Optional<Usuario> usuarioOpt = usuarioDAO.findById(usuarioId);
        if (!usuarioOpt.isPresent()) {
            return new ResultadoOperacion(false, "Usuario no encontrado");
        }
        
        Usuario usuario = usuarioOpt.get();
        
        // Validar nueva contraseña
        if (passwordNueva == null || passwordNueva.length() < MIN_PASSWORD_LENGTH) {
            return new ResultadoOperacion(false, "La contraseña debe tener al menos " + MIN_PASSWORD_LENGTH + " caracteres");
        }
        
        // Actualizar contraseña
        usuario.setUsuPassword(hashPassword(passwordNueva));
        usuario.setActualizadoEn(LocalDateTime.now());
        
        boolean actualizado = usuarioDAO.update(usuario);
        
        if (actualizado) {
            registrarAuditoriaUsuario(usuarioId, adminId, "PASSWORD_RESETEADA", 
                                    "Contraseña reseteada por administrador");
            return new ResultadoOperacion(true, "Contraseña reseteada exitosamente");
        } else {
            return new ResultadoOperacion(false, "Error al resetear contraseña");
        }
    }
    
    /**
     * Activa o desactiva un usuario
     */
    public ResultadoOperacion cambiarEstadoUsuario(Integer usuarioId, boolean activo, Integer adminId) {
        
        // Verificar permisos de admin
        Optional<Usuario> adminOpt = usuarioDAO.findById(adminId);
        if (!adminOpt.isPresent() || adminOpt.get().getUsuRol() != Rol.Jefe_Informatica) {
            return new ResultadoOperacion(false, "No tienes permisos para cambiar el estado de usuarios");
        }
        
        Optional<Usuario> usuarioOpt = usuarioDAO.findById(usuarioId);
        if (!usuarioOpt.isPresent()) {
            return new ResultadoOperacion(false, "Usuario no encontrado");
        }
        
        Usuario usuario = usuarioOpt.get();
        
        // No permitir que el admin se desactive a sí mismo
        if (usuarioId.equals(adminId) && !activo) {
            return new ResultadoOperacion(false, "No puedes desactivar tu propia cuenta");
        }
        
        usuario.setActivo(activo);
        usuario.setActualizadoEn(LocalDateTime.now());
        
        boolean actualizado = usuarioDAO.update(usuario);
        
        if (actualizado) {
            String accion = activo ? "USUARIO_ACTIVADO" : "USUARIO_DESACTIVADO";
            String descripcion = activo ? "Usuario activado" : "Usuario desactivado";
            
            registrarAuditoriaUsuario(usuarioId, adminId, accion, descripcion);
            return new ResultadoOperacion(true, descripcion + " exitosamente");
        } else {
            return new ResultadoOperacion(false, "Error al cambiar estado del usuario");
        }
    }
    
    // ===== AUTENTICACIÓN Y AUTORIZACIÓN =====
    
    /**
     * Autentica un usuario
     */
    public ResultadoAutenticacion autenticarUsuario(String username, String password) {
        
        Optional<Usuario> usuarioOpt = usuarioDAO.findByUsername(username);
        
        if (!usuarioOpt.isPresent()) {
            registrarIntentoAcceso(username, false, "Usuario no existe");
            return new ResultadoAutenticacion(false, "Credenciales inválidas");
        }
        
        Usuario usuario = usuarioOpt.get();
        
        if (!usuario.isActivo()) {
            registrarIntentoAcceso(username, false, "Usuario desactivado");
            return new ResultadoAutenticacion(false, "Usuario desactivado");
        }
        
        if (!verificarPassword(password, usuario.getUsuPassword())) {
            registrarIntentoAcceso(username, false, "Contraseña incorrecta");
            return new ResultadoAutenticacion(false, "Credenciales inválidas");
        }
        
        registrarIntentoAcceso(username, true, "Login exitoso");
        return new ResultadoAutenticacion(true, "Login exitoso", usuario);
    }
    
    /**
     * Verifica si un usuario tiene permisos para realizar una operación
     */
    public boolean tienePermiso(Usuario usuario, PermisoSistema permiso) {
        if (usuario == null || !usuario.isActivo()) {
            return false;
        }
        
        return verificarPermisosPorRol(usuario.getUsuRol(), permiso);
    }
    
    /**
     * Verifica permisos por rol
     */
    private boolean verificarPermisosPorRol(Rol rol, PermisoSistema permiso) {
        switch (rol) {
            case Jefe_Informatica:
                return true; // Admin tiene todos los permisos
                
            case Tecnico:
                return verificarPermisosTecnico(permiso);
                
            case Consulta:
                return verificarPermisosConsulta(permiso);
                
            default:
                return false;
        }
    }
    
    /**
     * Permisos para rol Técnico
     */
    private boolean verificarPermisosTecnico(PermisoSistema permiso) {
        switch (permiso) {
            // Consultas
            case CONSULTAR_ACTIVOS:
            case CONSULTAR_TICKETS:
            case CONSULTAR_MANTENIMIENTOS:
            case CONSULTAR_HISTORIAL_ACTIVOS:
                
            // Tickets
            case CREAR_TICKET_CORRECTIVO:
            case ACTUALIZAR_TICKETS_ASIGNADOS:
            case CERRAR_TICKETS_PROPIOS:
                
            // Mantenimientos
            case REGISTRAR_MANTENIMIENTO:
            case ACTUALIZAR_MANTENIMIENTO_PROPIO:
                
            // Traslados
            case CONSULTAR_TRASLADOS:
                return true;
                
            default:
                return false;
        }
    }
    
    /**
     * Permisos para rol Consulta
     */
    private boolean verificarPermisosConsulta(PermisoSistema permiso) {
        switch (permiso) {
            // Solo consultas básicas
            case CONSULTAR_ACTIVOS:
            case CONSULTAR_TICKETS:
            case CONSULTAR_HISTORIAL_ACTIVOS:
                return true;
                
            default:
                return false;
        }
    }
    
    // ===== CONSULTAS =====
    
    /**
     * Obtiene todos los usuarios
     */
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioDAO.findAll();
    }
    
    /**
     * Obtiene usuarios activos
     */
    public List<Usuario> obtenerUsuariosActivos() {
        return usuarioDAO.findAllActive();
    }
    
    /**
     * Obtiene usuarios por rol
     */
    public List<Usuario> obtenerUsuariosPorRol(Rol rol) {
        return usuarioDAO.findByRol(rol);
    }
    
    /**
     * Obtiene técnicos activos para asignaciones
     */
    public List<Usuario> obtenerTecnicosActivos() {
        return usuarioDAO.findByRolAndActive(Rol.Tecnico, true);
    }
    
    /**
     * Busca usuario por ID
     */
    public Optional<Usuario> obtenerUsuarioPorId(Integer id) {
        return usuarioDAO.findById(id);
    }
    
    /**
     * Busca usuario por username
     */
    public Optional<Usuario> obtenerUsuarioPorUsername(String username) {
        return usuarioDAO.findByUsername(username);
    }
    
    // ===== UTILIDADES PRIVADAS =====
    
    /**
     * Valida datos de usuario
     */
    private ResultadoOperacion validarDatosUsuario(String nombre, String username, 
                                                  String password, String email, Rol rol) {
        
        if (nombre == null || nombre.trim().isEmpty()) {
            return new ResultadoOperacion(false, "El nombre es obligatorio");
        }
        
        if (username == null || !USERNAME_PATTERN.matcher(username).matches()) {
            return new ResultadoOperacion(false, "Username inválido. Debe tener 3-20 caracteres alfanuméricos");
        }
        
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            return new ResultadoOperacion(false, "La contraseña debe tener al menos " + MIN_PASSWORD_LENGTH + " caracteres");
        }
        
        if (email != null && !email.trim().isEmpty() && !EMAIL_PATTERN.matcher(email).matches()) {
            return new ResultadoOperacion(false, "Email inválido");
        }
        
        if (rol == null) {
            return new ResultadoOperacion(false, "El rol es obligatorio");
        }
        
        return new ResultadoOperacion(true, "Validación exitosa");
    }
    
    /**
     * Verifica si existe un email
     */
    private boolean existeEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        List<Usuario> usuarios = usuarioDAO.findAll();
        return usuarios.stream()
                .anyMatch(u -> email.equals(u.getUsuEmail()));
    }
    
    /**
     * Genera hash SHA-256 de contraseña
     */
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al generar hash de contraseña", e);
        }
    }
    
    /**
     * Verifica contraseña contra hash
     */
    private boolean verificarPassword(String password, String hash) {
        return hashPassword(password).equals(hash);
    }
    
    /**
     * Registra auditoría de acciones de usuario
     */
    private void registrarAuditoriaUsuario(Integer usuarioId, Integer usuarioAccionId, 
                                         String accion, String descripcion) {
        try {
            System.out.println(String.format("AUDITORIA: Usuario %d - Acción: %s - Descripción: %s - Por: %d", 
                                            usuarioId, accion, descripcion, usuarioAccionId));
            // Aquí se podría implementar el guardado en tabla de auditoría
        } catch (Exception e) {
            System.err.println("Error al registrar auditoría: " + e.getMessage());
        }
    }
    
    /**
     * Registra intentos de acceso
     */
    private void registrarIntentoAcceso(String username, boolean exitoso, String descripcion) {
        try {
            System.out.println(String.format("ACCESO: Usuario %s - Exitoso: %s - Descripción: %s", 
                                            username, exitoso, descripcion));
            // Aquí se podría implementar el guardado en tabla de logs de acceso
        } catch (Exception e) {
            System.err.println("Error al registrar intento de acceso: " + e.getMessage());
        }
    }
    
    // ===== CLASES DE RESULTADO =====
    
    public static class ResultadoOperacion {
        private final boolean exitoso;
        private final String mensaje;
        private final Integer id;
        
        public ResultadoOperacion(boolean exitoso, String mensaje) {
            this(exitoso, mensaje, null);
        }
        
        public ResultadoOperacion(boolean exitoso, String mensaje, Integer id) {
            this.exitoso = exitoso;
            this.mensaje = mensaje;
            this.id = id;
        }
        
        public boolean isExitoso() { return exitoso; }
        public String getMensaje() { return mensaje; }
        public Integer getId() { return id; }
    }
    
    public static class ResultadoAutenticacion {
        private final boolean exitoso;
        private final String mensaje;
        private final Usuario usuario;
        
        public ResultadoAutenticacion(boolean exitoso, String mensaje) {
            this(exitoso, mensaje, null);
        }
        
        public ResultadoAutenticacion(boolean exitoso, String mensaje, Usuario usuario) {
            this.exitoso = exitoso;
            this.mensaje = mensaje;
            this.usuario = usuario;
        }
        
        public boolean isExitoso() { return exitoso; }
        public String getMensaje() { return mensaje; }
        public Usuario getUsuario() { return usuario; }
    }
    
    // ===== ENUMERACIÓN DE PERMISOS =====
    
    public enum PermisoSistema {
        // Consultas
        CONSULTAR_ACTIVOS,
        CONSULTAR_TICKETS,
        CONSULTAR_MANTENIMIENTOS,
        CONSULTAR_HISTORIAL_ACTIVOS,
        CONSULTAR_TRASLADOS,
        
        // Activos
        CREAR_ACTIVO,
        MODIFICAR_ACTIVO,
        ELIMINAR_ACTIVO,
        TRASLADAR_ACTIVO,
        
        // Tickets
        CREAR_TICKET_CORRECTIVO,
        CREAR_TICKET_PREVENTIVO,
        ACTUALIZAR_TICKETS_ASIGNADOS,
        CERRAR_TICKETS_PROPIOS,
        ASIGNAR_TICKETS,
        ELIMINAR_TICKETS,
        
        // Mantenimientos
        REGISTRAR_MANTENIMIENTO,
        ACTUALIZAR_MANTENIMIENTO_PROPIO,
        CONFIGURAR_MANTENIMIENTOS,
        
        // Administración
        GESTIONAR_USUARIOS,
        CONFIGURAR_SISTEMA,
        VER_REPORTES_COMPLETOS,
        AUDITAR_SISTEMA
    }
}
