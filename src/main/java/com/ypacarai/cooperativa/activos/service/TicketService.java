package com.ypacarai.cooperativa.activos.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.ypacarai.cooperativa.activos.dao.ActivoDAO;
import com.ypacarai.cooperativa.activos.dao.TicketDAO;
import com.ypacarai.cooperativa.activos.dao.UsuarioDAO;
import com.ypacarai.cooperativa.activos.model.Activo;
import com.ypacarai.cooperativa.activos.model.Ticket;
import com.ypacarai.cooperativa.activos.model.Usuario;

/**
 * Servicio completo para gestión de tickets de mantenimiento
 * Incluye funcionalidades para creación manual y automática
 * Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA
 */
public class TicketService {

    private final TicketDAO ticketDAO;
    private final ActivoDAO activoDAO;
    private final UsuarioDAO usuarioDAO;

    public TicketService() throws Exception {
        this.ticketDAO = new TicketDAO();
        this.activoDAO = new ActivoDAO();
        this.usuarioDAO = new UsuarioDAO();
    }

    /**
     * Obtener todos los tickets del sistema
     * @return Lista de tickets
     */
    public List<Ticket> obtenerTodosLosTickets() throws Exception {
        return ticketDAO.obtenerTodos();
    }

    /**
     * Buscar un ticket por su ID
     * @param ticketId ID del ticket
     * @return Ticket encontrado o null
     */
    public Ticket buscarTicketPorId(Integer ticketId) throws Exception {
        Optional<Ticket> ticketOpt = ticketDAO.buscarPorId(ticketId);
        return ticketOpt.orElse(null);
    }

    /**
     * Obtener tickets por estado específico
     * @param estado Estado del ticket
     * @return Lista de tickets filtrados por estado
     */
    public List<Ticket> obtenerTicketsPorEstado(Ticket.Estado estado) throws Exception {
        List<Ticket> todosTickets = ticketDAO.obtenerTodos();
        List<Ticket> ticketsFiltrados = new ArrayList<>();
        
        for (Ticket ticket : todosTickets) {
            if (ticket.getTickEstado() == estado) {
                ticketsFiltrados.add(ticket);
            }
        }
        
        return ticketsFiltrados;
    }

    /**
     * Obtener tickets por activo específico
     * @param activoId ID del activo
     * @return Lista de tickets del activo
     */
    public List<Ticket> obtenerTicketsPorActivo(int activoId) throws Exception {
        List<Ticket> todosTickets = ticketDAO.obtenerTodos();
        List<Ticket> ticketsActivo = new ArrayList<>();
        
        for (Ticket ticket : todosTickets) {
            if (ticket.getActId() == activoId) {
                ticketsActivo.add(ticket);
            }
        }
        
        return ticketsActivo;
    }

    /**
     * Crear un nuevo ticket
     * @param ticket Datos del ticket a crear
     * @return true si se creó exitosamente
     */
    public boolean crearTicket(Ticket ticket) throws Exception {
        // Validaciones básicas
        if (ticket == null) {
            throw new IllegalArgumentException("El ticket no puede ser nulo");
        }
        
        if (ticket.getActId() <= 0) {
            throw new IllegalArgumentException("Debe especificar un activo válido para el ticket");
        }
        
        if (ticket.getTickTitulo() == null || ticket.getTickTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("El título del ticket es obligatorio");
        }
        
        if (ticket.getTickDescripcion() == null || ticket.getTickDescripcion().trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción del ticket es obligatoria");
        }

        // Generar número automático si no está especificado
        if (ticket.getTickNumero() == null || ticket.getTickNumero().trim().isEmpty()) {
            ticket.setTickNumero(generarNumeroTicketAutomatico());
        }

        // Establecer valores por defecto
        if (ticket.getTickEstado() == null) {
            ticket.setTickEstado(Ticket.Estado.Abierto);
        }
        
        if (ticket.getTickFechaApertura() == null) {
            ticket.setTickFechaApertura(LocalDateTime.now());
        }
        
        if (ticket.getTickPrioridad() == null) {
            ticket.setTickPrioridad(Ticket.Prioridad.Media);
        }

        // Guardar en base de datos
        Ticket ticketGuardado = ticketDAO.guardar(ticket);
        return ticketGuardado != null && ticketGuardado.getTickId() > 0;
    }

    /**
     * Actualizar un ticket existente
     * @param ticket Datos actualizados del ticket
     */
    public void actualizarTicket(Ticket ticket) throws Exception {
        if (ticket == null || ticket.getTickId() <= 0) {
            throw new IllegalArgumentException("El ticket debe tener un ID válido para actualizar");
        }

        ticketDAO.actualizar(ticket);
    }

    /**
     * Cambiar el estado de un ticket
     * @param ticketId ID del ticket
     * @param nuevoEstado Nuevo estado del ticket
     * @param usuarioId ID del usuario que realiza el cambio
     */
    public void cambiarEstadoTicket(Integer ticketId, Ticket.Estado nuevoEstado, Integer usuarioId) throws Exception {
        Optional<Ticket> ticketOpt = ticketDAO.buscarPorId(ticketId);
        if (!ticketOpt.isPresent()) {
            throw new IllegalArgumentException("Ticket no encontrado con ID: " + ticketId);
        }

        Ticket ticket = ticketOpt.get();
        Ticket.Estado estadoAnterior = ticket.getTickEstado();
        
        // Validar transición de estado
        if (!esTransicionEstadoValida(estadoAnterior, nuevoEstado)) {
            throw new IllegalArgumentException(
                String.format("Transición de estado no válida: %s -> %s", estadoAnterior, nuevoEstado));
        }

        // Actualizar estado y fechas
        ticket.setTickEstado(nuevoEstado);
        ticket.setActualizadoEn(LocalDateTime.now());

        // Si se cierra el ticket, establecer fecha de cierre
        if (nuevoEstado == Ticket.Estado.Cerrado || nuevoEstado == Ticket.Estado.Resuelto) {
            ticket.setTickFechaCierre(LocalDateTime.now());
            
            // Calcular tiempo de resolución
            if (ticket.getTickFechaApertura() != null) {
                long minutos = java.time.Duration.between(ticket.getTickFechaApertura(), LocalDateTime.now()).toMinutes();
                ticket.setTickTiempoResolucion((int) minutos);
            }
        }

        ticketDAO.actualizar(ticket);
    }

    /**
     * Asignar un ticket a un técnico
     * @param ticketId ID del ticket
     * @param tecnicoId ID del técnico
     */
    public void asignarTicket(Integer ticketId, Integer tecnicoId) throws Exception {
        Optional<Ticket> ticketOpt = ticketDAO.buscarPorId(ticketId);
        if (!ticketOpt.isPresent()) {
            throw new IllegalArgumentException("Ticket no encontrado con ID: " + ticketId);
        }

        // Verificar que el técnico existe y es válido
        if (tecnicoId != null) {
            Optional<Usuario> tecnicoOpt = usuarioDAO.findById(tecnicoId);
            if (!tecnicoOpt.isPresent()) {
                throw new IllegalArgumentException("Técnico no encontrado con ID: " + tecnicoId);
            }
            
            Usuario tecnico = tecnicoOpt.get();
            if (tecnico.getUsuRol() != Usuario.Rol.Tecnico && tecnico.getUsuRol() != Usuario.Rol.Jefe_Informatica) {
                throw new IllegalArgumentException("El usuario no tiene permisos de técnico");
            }
        }

        Ticket ticket = ticketOpt.get();
        ticket.setTickAsignadoA(tecnicoId);
        ticket.setActualizadoEn(LocalDateTime.now());

        // Si se asigna y está abierto, cambiar a En_Proceso
        if (tecnicoId != null && ticket.getTickEstado() == Ticket.Estado.Abierto) {
            ticket.setTickEstado(Ticket.Estado.En_Proceso);
        }

        ticketDAO.actualizar(ticket);
    }

    /**
     * Generar tickets de mantenimiento preventivo automáticamente
     * @return Número de tickets generados
     */
    public int generarTicketsPreventivos() throws Exception {
        List<Activo> activosOperativos = activoDAO.findByEstado(Activo.Estado.Operativo);
        List<Ticket> ticketsExistentes = ticketDAO.obtenerTodos();
        int ticketsGenerados = 0;
        
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime hace6Meses = ahora.minusMonths(6);
        
        for (Activo activo : activosOperativos) {
            // Verificar si ya tiene mantenimiento preventivo reciente
            boolean tieneMantenimientoReciente = ticketsExistentes.stream()
                .anyMatch(t -> t.getActId() == activo.getActId() &&
                             t.getTickTipo() == Ticket.Tipo.Preventivo &&
                             t.getTickFechaApertura().isAfter(hace6Meses) &&
                             (t.getTickEstado() == Ticket.Estado.Abierto || 
                              t.getTickEstado() == Ticket.Estado.En_Proceso));
            
            if (!tieneMantenimientoReciente) {
                Ticket ticketPreventivo = crearTicketPreventivoAutomatico(activo);
                if (crearTicket(ticketPreventivo)) {
                    ticketsGenerados++;
                }
            }
        }
        
        return ticketsGenerados;
    }

    /**
     * Obtener tickets vencidos
     * @return Lista de tickets vencidos
     */
    public List<Ticket> obtenerTicketsVencidos() throws Exception {
        List<Ticket> todosTickets = ticketDAO.obtenerTodos();
        List<Ticket> ticketsVencidos = new ArrayList<>();
        LocalDateTime ahora = LocalDateTime.now();
        
        for (Ticket ticket : todosTickets) {
            if (ticket.getTickFechaVencimiento() != null && 
                ticket.getTickFechaVencimiento().isBefore(ahora) &&
                (ticket.getTickEstado() == Ticket.Estado.Abierto || 
                 ticket.getTickEstado() == Ticket.Estado.En_Proceso)) {
                ticketsVencidos.add(ticket);
            }
        }
        
        return ticketsVencidos;
    }

    /**
     * Obtener tickets por prioridad crítica
     * @return Lista de tickets críticos
     */
    public List<Ticket> obtenerTicketsCriticos() throws Exception {
        List<Ticket> todosTickets = ticketDAO.obtenerTodos();
        List<Ticket> ticketsCriticos = new ArrayList<>();
        
        for (Ticket ticket : todosTickets) {
            if (ticket.getTickPrioridad() == Ticket.Prioridad.Critica &&
                (ticket.getTickEstado() == Ticket.Estado.Abierto || 
                 ticket.getTickEstado() == Ticket.Estado.En_Proceso)) {
                ticketsCriticos.add(ticket);
            }
        }
        
        return ticketsCriticos;
    }

    /**
     * Obtener estadísticas de tickets
     * @return Array con [abiertos, en_proceso, resueltos, cerrados, cancelados]
     */
    public int[] obtenerEstadisticasTickets() throws Exception {
        List<Ticket> todosTickets = ticketDAO.obtenerTodos();
        int[] estadisticas = new int[5]; // [abierto, en_proceso, resuelto, cerrado, cancelado]
        
        for (Ticket ticket : todosTickets) {
            switch (ticket.getTickEstado()) {
                case Abierto:
                    estadisticas[0]++;
                    break;
                case En_Proceso:
                    estadisticas[1]++;
                    break;
                case Resuelto:
                    estadisticas[2]++;
                    break;
                case Cerrado:
                    estadisticas[3]++;
                    break;
                case Cancelado:
                    estadisticas[4]++;
                    break;
            }
        }
        
        return estadisticas;
    }

    /**
     * Buscar tickets por texto (título o descripción)
     * @param texto Texto a buscar
     * @return Lista de tickets que coinciden
     */
    public List<Ticket> buscarTicketsPorTexto(String texto) throws Exception {
        if (texto == null || texto.trim().isEmpty()) {
            return obtenerTodosLosTickets();
        }
        
        List<Ticket> todosTickets = ticketDAO.obtenerTodos();
        List<Ticket> ticketsEncontrados = new ArrayList<>();
        String textoBusqueda = texto.toLowerCase().trim();
        
        for (Ticket ticket : todosTickets) {
            boolean coincide = false;
            
            if (ticket.getTickTitulo() != null && 
                ticket.getTickTitulo().toLowerCase().contains(textoBusqueda)) {
                coincide = true;
            }
            
            if (ticket.getTickDescripcion() != null && 
                ticket.getTickDescripcion().toLowerCase().contains(textoBusqueda)) {
                coincide = true;
            }
            
            if (ticket.getTickNumero() != null && 
                ticket.getTickNumero().toLowerCase().contains(textoBusqueda)) {
                coincide = true;
            }
            
            if (coincide) {
                ticketsEncontrados.add(ticket);
            }
        }
        
        return ticketsEncontrados;
    }

    // Métodos privados de utilidad

    /**
     * Generar número automático para ticket
     * @return Número de ticket en formato TK-YYYY-NNNN
     */
    private String generarNumeroTicketAutomatico() throws Exception {
        String prefijo = "TK-" + LocalDate.now().getYear() + "-";
        List<Ticket> tickets = ticketDAO.obtenerTodos();
        
        int maxNumero = 0;
        for (Ticket ticket : tickets) {
            String numero = ticket.getTickNumero();
            if (numero != null && numero.startsWith(prefijo)) {
                try {
                    String numeroStr = numero.substring(prefijo.length());
                    int numeroInt = Integer.parseInt(numeroStr);
                    maxNumero = Math.max(maxNumero, numeroInt);
                } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                    // Ignorar números con formato incorrecto
                }
            }
        }
        
        int siguienteNumero = maxNumero + 1;
        return prefijo + String.format("%04d", siguienteNumero);
    }

    /**
     * Crear un ticket de mantenimiento preventivo automático
     * @param activo Activo para el cual crear el ticket
     * @return Ticket de mantenimiento preventivo
     */
    private Ticket crearTicketPreventivoAutomatico(Activo activo) throws Exception {
        Ticket ticket = new Ticket();
        
        ticket.setActId(activo.getActId());
        ticket.setTickNumero(generarNumeroTicketAutomatico());
        ticket.setTickTipo(Ticket.Tipo.Preventivo);
        ticket.setTickPrioridad(Ticket.Prioridad.Media);
        
        // Obtener un usuario del sistema para asignar como reportador (Jefe_Informatica)
        List<Usuario> usuarios = usuarioDAO.findAll();
        Usuario usuarioSistema = null;
        for (Usuario usuario : usuarios) {
            if (usuario.getUsuRol() == Usuario.Rol.Jefe_Informatica) {
                usuarioSistema = usuario;
                break;
            }
        }
        
        // Si no hay admin, usar el primer usuario disponible
        if (usuarioSistema == null && !usuarios.isEmpty()) {
            usuarioSistema = usuarios.get(0);
        }
        
        if (usuarioSistema != null) {
            ticket.setTickReportadoPor(usuarioSistema.getUsuId());
        } else {
            throw new Exception("No se encontró ningún usuario válido para reportar el ticket automático");
        }
        
        // Título descriptivo
        String titulo = String.format("Mantenimiento Preventivo - %s %s", 
            activo.getActMarca() != null ? activo.getActMarca() : "N/A",
            activo.getActModelo() != null ? activo.getActModelo() : "");
        ticket.setTickTitulo(titulo.trim());
        
        // Descripción detallada
        StringBuilder descripcion = new StringBuilder();
        descripcion.append("TICKET GENERADO AUTOMÁTICAMENTE\n\n");
        descripcion.append("Mantenimiento preventivo programado para el siguiente activo:\n\n");
        descripcion.append("• Número de Activo: ").append(activo.getActNumeroActivo()).append("\n");
        descripcion.append("• Marca: ").append(activo.getActMarca() != null ? activo.getActMarca() : "N/A").append("\n");
        descripcion.append("• Modelo: ").append(activo.getActModelo() != null ? activo.getActModelo() : "N/A").append("\n");
        descripcion.append("• Ubicación: ").append(activo.getUbicacionNombre() != null ? activo.getUbicacionNombre() : "N/A").append("\n\n");
        
        descripcion.append("TAREAS A REALIZAR:\n");
        descripcion.append("□ Limpieza general del equipo y componentes\n");
        descripcion.append("□ Verificación de estado de componentes principales\n");
        descripcion.append("□ Actualización de software y drivers\n");
        descripcion.append("□ Revisión de conexiones y cables\n");
        descripcion.append("□ Verificación de temperatura y ventilación\n");
        descripcion.append("□ Prueba de funcionamiento general\n");
        descripcion.append("□ Documentación del estado actual\n");
        descripcion.append("□ Recomendaciones para próximo mantenimiento\n\n");
        
        descripcion.append("NOTAS:\n");
        descripcion.append("• Este ticket fue generado automáticamente por el sistema\n");
        descripcion.append("• Programar con el área correspondiente antes de realizar el mantenimiento\n");
        descripcion.append("• Documentar todas las acciones realizadas\n");
        
        ticket.setTickDescripcion(descripcion.toString());
        
        // Fechas
        LocalDateTime ahora = LocalDateTime.now();
        ticket.setTickFechaApertura(ahora);
        ticket.setTickFechaVencimiento(ahora.plusWeeks(4)); // 4 semanas para completar
        
        // Estado inicial
        ticket.setTickEstado(Ticket.Estado.Abierto);
        
        return ticket;
    }

    /**
     * Validar si una transición de estado es válida
     * @param estadoActual Estado actual del ticket
     * @param nuevoEstado Nuevo estado propuesto
     * @return true si la transición es válida
     */
    private boolean esTransicionEstadoValida(Ticket.Estado estadoActual, Ticket.Estado nuevoEstado) {
        if (estadoActual == nuevoEstado) {
            return true; // No hay cambio
        }
        
        switch (estadoActual) {
            case Abierto:
                return nuevoEstado == Ticket.Estado.En_Proceso || 
                       nuevoEstado == Ticket.Estado.Cancelado;
                       
            case En_Proceso:
                return nuevoEstado == Ticket.Estado.Resuelto || 
                       nuevoEstado == Ticket.Estado.Abierto ||
                       nuevoEstado == Ticket.Estado.Cancelado;
                       
            case Resuelto:
                return nuevoEstado == Ticket.Estado.Cerrado || 
                       nuevoEstado == Ticket.Estado.En_Proceso; // Reabrir si no está bien
                       
            case Cerrado:
                return nuevoEstado == Ticket.Estado.En_Proceso; // Reabrir si es necesario
                
            case Cancelado:
                return nuevoEstado == Ticket.Estado.Abierto; // Reactivar
                
            default:
                return false;
        }
    }
}
