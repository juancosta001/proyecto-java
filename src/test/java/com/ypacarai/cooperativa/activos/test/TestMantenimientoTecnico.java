package com.ypacarai.cooperativa.activos.test;

import com.ypacarai.cooperativa.activos.dao.TicketDAO;
import com.ypacarai.cooperativa.activos.dao.UsuarioDAO;
import com.ypacarai.cooperativa.activos.model.Ticket;
import com.ypacarai.cooperativa.activos.model.Usuario;
import java.util.List;

/**
 * Test para verificar que los tickets del técnico se cargan correctamente
 */
public class TestMantenimientoTecnico {
    
    public static void main(String[] args) {
        System.out.println("=== Test: Verificación de Mantenimientos del Técnico ===");
        System.out.println();
        
        try {
            TicketDAO ticketDAO = new TicketDAO();
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            
            // Buscar usuario técnico (jose)
            Usuario tecnico = null;
            List<Usuario> usuarios = usuarioDAO.findAll();
            for (Usuario u : usuarios) {
                if ("jose".equals(u.getUsuNombre()) && u.getUsuRol() == Usuario.Rol.Tecnico) {
                    tecnico = u;
                    break;
                }
            }
            
            if (tecnico == null) {
                System.out.println("❌ No se encontró el usuario técnico 'jose'");
                System.out.println("   Usuarios disponibles:");
                for (Usuario u : usuarios) {
                    System.out.println("   - " + u.getUsuNombre() + " (ID: " + u.getUsuId() + ", Rol: " + u.getUsuRol() + ")");
                }
                return;
            }
            
            System.out.println("✅ Usuario técnico encontrado:");
            System.out.println("   Nombre: " + tecnico.getUsuNombre());
            System.out.println("   ID: " + tecnico.getUsuId());
            System.out.println("   Rol: " + tecnico.getUsuRol());
            System.out.println();
            
            // Obtener todos los tickets primero
            List<Ticket> todosTickets = ticketDAO.obtenerTodos();
            System.out.println("📋 Total de tickets en el sistema: " + todosTickets.size());
            
            if (!todosTickets.isEmpty()) {
                System.out.println("   Primeros 3 tickets:");
                for (int i = 0; i < Math.min(3, todosTickets.size()); i++) {
                    Ticket t = todosTickets.get(i);
                    System.out.println("   - ID: " + t.getTickId() + 
                                     ", Asignado a: " + t.getTickAsignadoA() + 
                                     ", Estado: " + t.getTickEstado() +
                                     ", Título: " + t.getTickTitulo());
                }
            }
            System.out.println();
            
            // Obtener tickets del técnico específico
            List<Ticket> ticketsTecnico = ticketDAO.obtenerPorTecnico(tecnico.getUsuId());
            System.out.println("🔧 Tickets asignados al técnico '" + tecnico.getUsuNombre() + "': " + ticketsTecnico.size());
            
            if (ticketsTecnico.isEmpty()) {
                System.out.println("⚠️ No hay tickets asignados al técnico");
                System.out.println("   Posibles causas:");
                System.out.println("   1. No hay tickets creados en el sistema");
                System.out.println("   2. Los tickets no están asignados al técnico (tick_asignado_a = " + tecnico.getUsuId() + ")");
                System.out.println("   3. Todos los tickets están cerrados/completados");
                System.out.println();
                
                // Verificar si hay tickets sin asignar
                long ticketsSinAsignar = todosTickets.stream()
                    .filter(t -> t.getTickAsignadoA() == null)
                    .count();
                System.out.println("   Tickets sin asignar: " + ticketsSinAsignar);
                
                // Verificar tickets por estado
                System.out.println("   Tickets por estado:");
                for (Ticket.Estado estado : Ticket.Estado.values()) {
                    long count = todosTickets.stream()
                        .filter(t -> t.getTickEstado() == estado)
                        .count();
                    System.out.println("     " + estado + ": " + count);
                }
            } else {
                System.out.println("✅ Tickets encontrados:");
                for (Ticket ticket : ticketsTecnico) {
                    System.out.println("   - ID: " + ticket.getTickId());
                    System.out.println("     Título: " + ticket.getTickTitulo());
                    System.out.println("     Tipo: " + ticket.getTickTipo());
                    System.out.println("     Estado: " + ticket.getTickEstado());
                    System.out.println("     Prioridad: " + ticket.getTickPrioridad());
                    System.out.println("     Activo: " + ticket.getActivoNumero());
                    System.out.println("     Ubicación: " + ticket.getUbicacionNombre());
                    System.out.println();
                }
            }
            
            System.out.println("🔍 DIAGNÓSTICO PARA EL PANEL:");
            System.out.println("   El panel MantenimientoTecnicoPanel busca tickets con:");
            System.out.println("   - Estado: Abierto o En_Proceso");
            System.out.println("   - Asignado a: " + tecnico.getUsuId());
            
            long ticketsValidos = ticketsTecnico.stream()
                .filter(t -> t.getTickEstado() == Ticket.Estado.Abierto || 
                            t.getTickEstado() == Ticket.Estado.En_Proceso)
                .count();
                
            System.out.println("   - Tickets válidos para mostrar: " + ticketsValidos);
            
            if (ticketsValidos == 0) {
                System.out.println();
                System.out.println("💡 SOLUCIÓN SUGERIDA:");
                System.out.println("   Para crear tickets de prueba, ejecuta:");
                System.out.println("   1. Ir al módulo de Activos");
                System.out.println("   2. Crear algunos tickets de mantenimiento");
                System.out.println("   3. Asignarlos al técnico 'jose' (ID: " + tecnico.getUsuId() + ")");
                System.out.println("   4. Asegurar que estén en estado 'Abierto' o 'En_Proceso'");
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error durante la verificación: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n=== Fin del diagnóstico ===");
    }
}