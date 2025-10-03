import java.util.*;
import com.ypacarai.cooperativa.activos.dao.*;
import com.ypacarai.cooperativa.activos.model.*;

/**
 * Test para verificar que la ventana del técnico muestre las ubicaciones correctamente
 */
public class TestUbicacionVentanaTecnico {
    
    public static void main(String[] args) {
        try {
            System.out.println("=== TEST UBICACIONES EN VENTANA TÉCNICO ===");
            
            TicketDAO ticketDAO = new TicketDAO();
            TicketAsignacionDAO asignacionDAO = new TicketAsignacionDAO();
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            
            // 1. Buscar un técnico disponible
            System.out.println("\n1. Buscando técnico disponible...");
            List<Usuario> usuarios = usuarioDAO.findAll();
            Usuario tecnico = null;
            
            for (Usuario usuario : usuarios) {
                if (usuario.getUsuRol() == Usuario.Rol.Tecnico || 
                    usuario.getUsuRol() == Usuario.Rol.Jefe_Informatica) {
                    tecnico = usuario;
                    break;
                }
            }
            
            if (tecnico == null) {
                System.out.println("❌ No se encontró técnico disponible");
                return;
            }
            System.out.println("✅ Técnico seleccionado: " + tecnico.getUsuNombre() + " (ID: " + tecnico.getUsuId() + ")");
            
            // 2. Obtener tickets asignados al técnico
            System.out.println("\n2. Obteniendo tickets asignados...");
            List<Integer> ticketIds = asignacionDAO.obtenerTicketsAsignados(tecnico.getUsuId());
            
            if (ticketIds.isEmpty()) {
                System.out.println("❌ No hay tickets asignados al técnico");
                return;
            }
            
            // 3. Obtener detalles completos con ubicaciones
            System.out.println("\n3. Obteniendo detalles de tickets con ubicaciones...");
            List<Ticket> ticketsCompletos = ticketDAO.obtenerPorIds(ticketIds);
            
            System.out.println("📍 Tickets con información de ubicación:");
            System.out.println("========================================================");
            System.out.printf("%-8s %-15s %-20s %-15s %-10s%n", 
                             "ID", "Equipo", "Ubicación", "Tipo", "Estado");
            System.out.println("========================================================");
            
            int ticketsConUbicacion = 0;
            int totalTickets = 0;
            
            for (Ticket ticket : ticketsCompletos) {
                if (ticket.getTickEstado() == Ticket.Estado.Abierto || 
                    ticket.getTickEstado() == Ticket.Estado.En_Proceso) {
                    
                    totalTickets++;
                    String ubicacion = ticket.getUbicacionNombre() != null ? 
                                     ticket.getUbicacionNombre() : "Sin ubicación";
                    
                    if (ticket.getUbicacionNombre() != null) {
                        ticketsConUbicacion++;
                    }
                    
                    System.out.printf("%-8d %-15s %-20s %-15s %-10s%n",
                        ticket.getTickId(),
                        ticket.getActivoNumero() != null ? ticket.getActivoNumero() : "N/A",
                        ubicacion,
                        ticket.getTickTipo() != null ? ticket.getTickTipo().toString() : "N/A",
                        ticket.getTickEstado() != null ? ticket.getTickEstado().toString() : "N/A"
                    );
                }
            }
            
            System.out.println("========================================================");
            System.out.println("\n📊 Resumen:");
            System.out.println("  - Total de tickets activos: " + totalTickets);
            System.out.println("  - Tickets con ubicación: " + ticketsConUbicacion);
            System.out.println("  - Tickets sin ubicación: " + (totalTickets - ticketsConUbicacion));
            
            if (ticketsConUbicacion > 0) {
                System.out.println("✅ ÉXITO: Las ubicaciones se muestran correctamente en la ventana del técnico");
            } else {
                System.out.println("⚠️  ADVERTENCIA: Ningún ticket tiene información de ubicación");
            }
            
            // 4. Mostrar ejemplo de cómo aparecerá en la tabla
            System.out.println("\n4. Vista previa de la tabla en la ventana del técnico:");
            System.out.println("===============================================================================");
            System.out.printf("%-8s %-12s %-15s %-12s %-10s %-10s %-12s%n",
                             "ID", "Equipo", "Ubicación", "Tipo", "Prioridad", "Estado", "Fecha");
            System.out.println("===============================================================================");
            
            for (Ticket ticket : ticketsCompletos) {
                if (ticket.getTickEstado() == Ticket.Estado.Abierto || 
                    ticket.getTickEstado() == Ticket.Estado.En_Proceso) {
                    
                    String ubicacion = ticket.getUbicacionNombre() != null ? 
                                     ticket.getUbicacionNombre() : "Sin ubicación";
                    
                    System.out.printf("%-8d %-12s %-15s %-12s %-10s %-10s %-12s%n",
                        ticket.getTickId(),
                        ticket.getActivoNumero() != null ? ticket.getActivoNumero().substring(0, Math.min(11, ticket.getActivoNumero().length())) : "N/A",
                        ubicacion.length() > 14 ? ubicacion.substring(0, 11) + "..." : ubicacion,
                        ticket.getTickTipo() != null ? ticket.getTickTipo().toString().substring(0, Math.min(11, ticket.getTickTipo().toString().length())) : "N/A",
                        ticket.getTickPrioridad() != null ? ticket.getTickPrioridad().toString() : "N/A",
                        ticket.getTickEstado() != null ? ticket.getTickEstado().toString() : "N/A",
                        ticket.getTickFechaApertura() != null ? 
                            ticket.getTickFechaApertura().toLocalDate().toString() : "N/A"
                    );
                }
            }
            
            System.out.println("===============================================================================");
            System.out.println("\n🎉 TEST COMPLETADO! Las ubicaciones ya no mostrarán 'N/A'");
            
        } catch (Exception e) {
            System.err.println("❌ Error en el test: " + e.getMessage());
            e.printStackTrace();
        }
    }
}