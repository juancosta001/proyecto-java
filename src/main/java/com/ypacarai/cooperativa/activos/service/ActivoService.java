package com.ypacarai.cooperativa.activos.service;

import java.util.List;
import java.util.Optional;

import com.ypacarai.cooperativa.activos.dao.ActivoDAO;
import com.ypacarai.cooperativa.activos.dao.TipoActivoDAO;
import com.ypacarai.cooperativa.activos.dao.UbicacionDAO;
import com.ypacarai.cooperativa.activos.model.Activo;
import com.ypacarai.cooperativa.activos.model.TipoActivo;
import com.ypacarai.cooperativa.activos.model.Ubicacion;

/**
 * Servicio definitivo para la gestión de activos
 * Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA
 */
public class ActivoService {
    
    // Tipos de activo permitidos según el protocolo de delimitación
    private static final java.util.Set<String> TIPOS_PERMITIDOS = java.util.Set.of("PC", "Impresora");
    
    private final ActivoDAO activoDAO;
    private final TipoActivoDAO tipoActivoDAO;
    private final UbicacionDAO ubicacionDAO;
    
    public ActivoService() {
        this.activoDAO = new ActivoDAO();
        this.tipoActivoDAO = new TipoActivoDAO();
        this.ubicacionDAO = new UbicacionDAO();
    }
    
    /**
     * Obtiene todos los activos usando el método correcto de ActivoDAO
     */
    public List<Activo> obtenerTodosLosActivos() {
        return activoDAO.findAll();
    }
    
    /**
     * Busca activo por ID usando el método correcto
     */
    public Activo buscarActivoPorId(int id) {
        Optional<Activo> activo = activoDAO.findById(id);
        return activo.orElse(null);
    }
    
    /**
     * Busca activo por número usando el método correcto
     */
    public Activo buscarActivoPorNumero(String numero) {
        Optional<Activo> activo = activoDAO.findByNumero(numero);
        return activo.orElse(null);
    }
    
    /**
     * Obtiene activos por ubicación
     */
    public List<Activo> obtenerActivosPorUbicacion(int ubicacionId) {
        return activoDAO.findByUbicacion(ubicacionId);
    }
    
    /**
     * Obtiene activos por estado
     */
    public List<Activo> obtenerActivosPorEstado(Activo.Estado estado) {
        return activoDAO.findByEstado(estado);
    }
    
    /**
     * Crea un nuevo activo
     */
    public boolean crearActivo(Activo activo) {
        try {
            // Validaciones básicas
            if (activo.getActNumeroActivo() == null || activo.getActNumeroActivo().trim().isEmpty()) {
                throw new IllegalArgumentException("El número de activo es requerido");
            }
            
            // VALIDACIÓN: Restricción de tipos de activo (solo PC e Impresora)
            if (activo.getTipActId() > 0) {
                try {
                    Optional<TipoActivo> tipoOpt = tipoActivoDAO.buscarPorId(activo.getTipActId());
                    if (tipoOpt.isPresent()) {
                        String nombreTipo = tipoOpt.get().getNombre();
                        if (!TIPOS_PERMITIDOS.contains(nombreTipo)) {
                            throw new IllegalArgumentException(
                                "Tipo de activo no permitido. Solo se permiten: " + TIPOS_PERMITIDOS + 
                                ". Tipo recibido: " + nombreTipo
                            );
                        }
                    }
                } catch (java.sql.SQLException e) {
                    throw new RuntimeException("Error al validar tipo de activo", e);
                }
            }
            
            // Verificar que el número de activo no esté duplicado
            Optional<Activo> existente = activoDAO.findByNumero(activo.getActNumeroActivo());
            if (existente.isPresent()) {
                throw new IllegalArgumentException("Ya existe un activo con el número " + activo.getActNumeroActivo());
            }
            
            // Establecer estado inicial si no está definido
            if (activo.getActEstado() == null) {
                activo.setActEstado(Activo.Estado.Operativo);
            }
            
            // Establecer fecha de creación
            activo.setCreadoEn(java.time.LocalDateTime.now());
            activo.setActualizadoEn(java.time.LocalDateTime.now());
            
            Activo activoGuardado = activoDAO.save(activo);
            return activoGuardado != null && activoGuardado.getActId() > 0;
            
        } catch (Exception e) {
            System.err.println("Error al crear activo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Actualiza un activo existente
     */
    public void actualizarActivo(Activo activo) {
        // Validar que el activo existe
        if (activo.getActId() == 0) {
            throw new IllegalArgumentException("ID del activo es requerido para actualización");
        }
        
        Optional<Activo> existente = activoDAO.findById(activo.getActId());
        if (!existente.isPresent()) {
            throw new IllegalArgumentException("Activo no encontrado para actualizar");
        }
        
        activoDAO.update(activo);
    }
    
    /**
     * Actualiza el estado de un activo
     */
    public void actualizarEstadoActivo(int activoId, Activo.Estado nuevoEstado) {
        activoDAO.updateEstado(activoId, nuevoEstado);
    }
    
    /**
     * Actualiza ubicación y estado de un activo
     */
    public void trasladarActivo(int activoId, int nuevaUbicacion, Activo.Estado nuevoEstado) {
        activoDAO.updateUbicacionYEstado(activoId, nuevaUbicacion, nuevoEstado);
    }
    
    /**
     * Obtiene estadísticas básicas de activos
     */
    public String obtenerEstadisticasActivos() {
        try {
            List<Activo> todosLosActivos = activoDAO.findAll();
            
            int total = todosLosActivos.size();
            int operativos = (int) todosLosActivos.stream()
                .filter(a -> a.getActEstado() == Activo.Estado.Operativo).count();
            int enMantenimiento = (int) todosLosActivos.stream()
                .filter(a -> a.getActEstado() == Activo.Estado.En_Mantenimiento).count();
            int fueraServicio = (int) todosLosActivos.stream()
                .filter(a -> a.getActEstado() == Activo.Estado.Fuera_Servicio).count();
            int trasladados = (int) todosLosActivos.stream()
                .filter(a -> a.getActEstado() == Activo.Estado.Trasladado).count();
            
            StringBuilder stats = new StringBuilder();
            stats.append("=== ESTADÍSTICAS DE ACTIVOS ===\n");
            stats.append("Total de activos: ").append(total).append("\n");
            stats.append("Operativos: ").append(operativos).append("\n");
            stats.append("En mantenimiento: ").append(enMantenimiento).append("\n");
            stats.append("Fuera de servicio: ").append(fueraServicio).append("\n");
            stats.append("Trasladados: ").append(trasladados).append("\n");
            
            return stats.toString();
            
        } catch (Exception e) {
            return "Error al obtener estadísticas: " + e.getMessage();
        }
    }
    
    /**
     * Obtiene todos los tipos de activos
     */
    public List<TipoActivo> obtenerTodosTiposActivos() {
        try {
            return tipoActivoDAO.obtenerTodos();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener tipos de activos: " + e.getMessage(), e);
        }
    }
    
    /**
     * Obtiene todas las ubicaciones
     */
    public List<Ubicacion> obtenerTodasUbicaciones() {
        try {
            return ubicacionDAO.obtenerTodas();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener ubicaciones: " + e.getMessage(), e);
        }
    }
    
    /**
     * Busca tipo de activo por ID
     */
    public TipoActivo buscarTipoActivoPorId(int id) {
        try {
            Optional<TipoActivo> tipo = tipoActivoDAO.buscarPorId(id);
            return tipo.orElse(null);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar tipo de activo: " + e.getMessage(), e);
        }
    }
    
    /**
     * Busca ubicación por ID
     */
    public Ubicacion buscarUbicacionPorId(int id) {
        try {
            Optional<Ubicacion> ubicacion = ubicacionDAO.buscarPorId(id);
            return ubicacion.orElse(null);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar ubicación: " + e.getMessage(), e);
        }
    }

    /**
     * Genera un número de activo automático en formato ACT-YYYY-NNNN
     */
    public String generarNumeroActivoAutomatico() throws Exception {
        try {
            int year = java.time.LocalDate.now().getYear();
            String prefijo = "ACT-" + year + "-";
            
            // Buscar el último número usado en este año
            List<Activo> activos = activoDAO.findAll();
            int maxNumero = 0;
            
            for (Activo activo : activos) {
                String numero = activo.getActNumeroActivo();
                if (numero != null && numero.startsWith(prefijo)) {
                    try {
                        String numeroStr = numero.substring(prefijo.length());
                        int numeroInt = Integer.parseInt(numeroStr);
                        maxNumero = Math.max(maxNumero, numeroInt);
                    } catch (Exception e) {
                        // Ignorar números con formato incorrecto
                    }
                }
            }
            
            // Generar el siguiente número
            int siguienteNumero = maxNumero + 1;
            return prefijo + String.format("%04d", siguienteNumero);
            
        } catch (Exception e) {
            throw new Exception("Error al generar número automático: " + e.getMessage());
        }
    }

    /**
     * Verifica si ya existe un activo con el número dado
     */
    public boolean existeNumeroActivo(String numeroActivo) throws Exception {
        try {
            return activoDAO.findByNumero(numeroActivo).isPresent();
        } catch (Exception e) {
            throw new Exception("Error al verificar número de activo: " + e.getMessage());
        }
    }

    /**
     * Verifica si ya existe un activo con el número de serie dado
     */
    public boolean existeNumeroSerie(String numeroSerie) throws Exception {
        try {
            List<Activo> activos = activoDAO.findAll();
            for (Activo activo : activos) {
                if (numeroSerie.equals(activo.getActNumeroSerie())) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            throw new Exception("Error al verificar número de serie: " + e.getMessage());
        }
    }
}
