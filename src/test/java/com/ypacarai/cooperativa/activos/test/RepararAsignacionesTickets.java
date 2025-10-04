package com.ypacarai.cooperativa.activos.test;

import com.ypacarai.cooperativa.activos.dao.TicketDAO;
import com.ypacarai.cooperativa.activos.dao.TicketAsignacionDAO;
import com.ypacarai.cooperativa.activos.model.Ticket;
import com.ypacarai.cooperativa.activos.model.TicketAsignacion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.ypacarai.cooperativa.activos.config.DatabaseConfigComplete;

/**
 * Script para reparar asignaciones de tickets
 * Actualiza el campo tick_asignado_a basándose en las asignaciones de la tabla ticket_asignaciones
 */
public class RepararAsignacionesTickets {
    
    public static void main(String[] args) {
        System.out.println("=== Reparando asignaciones de tickets ===");
        System.out.println();
        
        try {
            TicketDAO ticketDAO = new TicketDAO();
            TicketAsignacionDAO asignacionDAO = new TicketAsignacionDAO();
            
            // Obtener todos los tickets
            List<Ticket> tickets = ticketDAO.obtenerTodos();
            System.out.println("📋 Total de tickets encontrados: " + tickets.size());
            
            int ticketsReparados = 0;
            int ticketsConAsignaciones = 0;
            int ticketsSinAsignaciones = 0;
            
            for (Ticket ticket : tickets) {
                List<TicketAsignacion> asignaciones = asignacionDAO.obtenerTecnicosAsignados(ticket.getTickId());
                
                if (asignaciones.isEmpty()) {
                    ticketsSinAsignaciones++;
                    continue;
                }
                
                ticketsConAsignaciones++;
                
                // Buscar el técnico responsable principal
                TicketAsignacion responsablePrincipal = null;
                for (TicketAsignacion asignacion : asignaciones) {
                    if (asignacion.getTasRolAsignacion() == TicketAsignacion.RolAsignacion.Responsable) {
                        responsablePrincipal = asignacion;
                        break;
                    }
                }
                
                // Si no hay responsable, usar el primer técnico asignado
                if (responsablePrincipal == null && !asignaciones.isEmpty()) {
                    responsablePrincipal = asignaciones.get(0);
                }
                
                if (responsablePrincipal != null) {
                    // Verificar si ya está asignado correctamente
                    Integer tecnicoAsignadoActual = ticket.getTickAsignadoA();
                    int tecnicoAsignadoDeberia = responsablePrincipal.getUsuId();
                    
                    if (tecnicoAsignadoActual == null || !tecnicoAsignadoActual.equals(tecnicoAsignadoDeberia)) {
                        // Actualizar el campo tick_asignado_a
                        boolean actualizado = actualizarTicketAsignado(ticket.getTickId(), tecnicoAsignadoDeberia);
                        
                        if (actualizado) {
                            ticketsReparados++;
                            System.out.println("✅ Ticket " + ticket.getTickId() + 
                                             " - Asignado a técnico ID " + tecnicoAsignadoDeberia +
                                             " (antes: " + tecnicoAsignadoActual + ")");
                        } else {
                            System.out.println("❌ Error al reparar ticket " + ticket.getTickId());
                        }
                    }
                }
            }
            
            System.out.println();
            System.out.println("📊 RESUMEN:");
            System.out.println("   Total de tickets: " + tickets.size());
            System.out.println("   Tickets con asignaciones: " + ticketsConAsignaciones);
            System.out.println("   Tickets sin asignaciones: " + ticketsSinAsignaciones);
            System.out.println("   Tickets reparados: " + ticketsReparados);
            System.out.println();
            
            if (ticketsReparados > 0) {
                System.out.println("🎉 Se repararon " + ticketsReparados + " tickets");
                System.out.println("   Ahora el panel de mantenimiento técnico debería mostrar los tickets correctamente");
            } else {
                System.out.println("ℹ️ No se encontraron tickets que necesiten reparación");
            }
            
            // Crear algunos tickets de prueba si no hay tickets asignados al técnico jose
            if (ticketsReparados == 0) {
                crearTicketsDePrueba();
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error durante la reparación: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static boolean actualizarTicketAsignado(int ticketId, int tecnicoId) {
        String sql = "UPDATE TICKET SET tick_asignado_a = ?, actualizado_en = NOW() WHERE tick_id = ?";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tecnicoId);
            pstmt.setInt(2, ticketId);
            
            int filasActualizadas = pstmt.executeUpdate();
            return filasActualizadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar ticket " + ticketId + ": " + e.getMessage());
            return false;
        }
    }
    
    private static void crearTicketsDePrueba() {
        System.out.println();
        System.out.println("🔧 Creando tickets de prueba para técnico jose (ID: 4)...");
        
        String[] sqls = {
            "INSERT INTO TICKET (act_id, tick_numero, tick_tipo, tick_prioridad, tick_titulo, tick_descripcion, tick_estado, tick_fecha_apertura, tick_fecha_vencimiento, tick_asignado_a, tick_reportado_por) " +
            "SELECT 1, 'MANT-001-DEMO', 'Preventivo', 'Alta', 'Mantenimiento preventivo - Monitor principal', 'Revisión general del equipo y limpieza de componentes', 'Abierto', NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), 4, 1 " +
            "WHERE EXISTS (SELECT 1 FROM ACTIVO WHERE act_id = 1) AND EXISTS (SELECT 1 FROM USUARIO WHERE usu_id = 4)",
            
            "INSERT INTO TICKET (act_id, tick_numero, tick_tipo, tick_prioridad, tick_titulo, tick_descripcion, tick_estado, tick_fecha_apertura, tick_fecha_vencimiento, tick_asignado_a, tick_reportado_por) " +
            "SELECT 1, 'MANT-002-DEMO', 'Correctivo', 'Media', 'Verificación de conectividad', 'Revisar problemas de conexión reportados por usuario', 'Abierto', NOW(), DATE_ADD(NOW(), INTERVAL 3 DAY), 4, 1 " +
            "WHERE EXISTS (SELECT 1 FROM ACTIVO WHERE act_id = 1) AND EXISTS (SELECT 1 FROM USUARIO WHERE usu_id = 4)",
            
            "INSERT INTO TICKET (act_id, tick_numero, tick_tipo, tick_prioridad, tick_titulo, tick_descripcion, tick_estado, tick_fecha_apertura, tick_fecha_vencimiento, tick_asignado_a, tick_reportado_por) " +
            "SELECT 1, 'MANT-003-DEMO', 'Preventivo', 'Baja', 'Actualización de software', 'Instalar actualizaciones pendientes del sistema operativo', 'En_Proceso', NOW(), DATE_ADD(NOW(), INTERVAL 10 DAY), 4, 1 " +
            "WHERE EXISTS (SELECT 1 FROM ACTIVO WHERE act_id = 1) AND EXISTS (SELECT 1 FROM USUARIO WHERE usu_id = 4)"
        };
        
        int creados = 0;
        for (String sql : sqls) {
            try (Connection conn = DatabaseConfigComplete.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                int filasInsertadas = pstmt.executeUpdate();
                if (filasInsertadas > 0) {
                    creados++;
                }
                
            } catch (SQLException e) {
                System.err.println("Error al crear ticket de prueba: " + e.getMessage());
            }
        }
        
        if (creados > 0) {
            System.out.println("✅ Se crearon " + creados + " tickets de prueba");
            System.out.println("   Todos asignados directamente al técnico jose (ID: 4)");
        } else {
            System.out.println("⚠️ No se pudieron crear tickets de prueba");
            System.out.println("   Verifica que exista el activo con ID 1 y el usuario con ID 4");
        }
    }
}