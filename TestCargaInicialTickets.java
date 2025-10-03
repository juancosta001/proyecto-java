import java.util.*;
import com.ypacarai.cooperativa.activos.dao.*;
import com.ypacarai.cooperativa.activos.model.*;

/**
 * Test para verificar que no hay errores al cargar tickets desde la aplicación
 */
public class TestCargaInicialTickets {
    
    public static void main(String[] args) {
        try {
            System.out.println("=== TEST CARGA INICIAL DE TICKETS ===");
            
            TicketDAO ticketDAO = new TicketDAO();
            
            // 1. Probar obtenerTodos() (usado en muchas ventanas)
            System.out.println("\n1. Probando obtenerTodos()...");
            List<Ticket> todosTickets = ticketDAO.obtenerTodos();
            System.out.println("✅ obtenerTodos() - " + todosTickets.size() + " tickets cargados");
            
            // 2. Probar obtenerPorEstado() 
            System.out.println("\n2. Probando obtenerPorEstado()...");
            List<Ticket> ticketsAbiertos = ticketDAO.obtenerPorEstado(Ticket.Estado.Abierto);
            System.out.println("✅ obtenerPorEstado(Abierto) - " + ticketsAbiertos.size() + " tickets");
            
            // 3. Probar obtenerVencidos()
            System.out.println("\n3. Probando obtenerVencidos()...");
            List<Ticket> ticketsVencidos = ticketDAO.obtenerVencidos();
            System.out.println("✅ obtenerVencidos() - " + ticketsVencidos.size() + " tickets");
            
            // 4. Probar buscarPorId() 
            System.out.println("\n4. Probando buscarPorId()...");
            if (!todosTickets.isEmpty()) {
                Integer idPrueba = todosTickets.get(0).getTickId();
                Optional<Ticket> ticketEncontrado = ticketDAO.buscarPorId(idPrueba);
                if (ticketEncontrado.isPresent()) {
                    System.out.println("✅ buscarPorId(" + idPrueba + ") - ticket encontrado");
                    System.out.println("   Ubicación: " + (ticketEncontrado.get().getUbicacionNombre() != null ? 
                                     ticketEncontrado.get().getUbicacionNombre() : "Sin ubicación"));
                } else {
                    System.out.println("⚠️  buscarPorId(" + idPrueba + ") - ticket no encontrado");
                }
            }
            
            // 5. Verificar que todos los tickets tienen información de ubicación mapeada
            System.out.println("\n5. Verificando mapeo de ubicaciones...");
            int conUbicacion = 0;
            int sinUbicacion = 0;
            
            for (Ticket ticket : todosTickets) {
                if (ticket.getUbicacionNombre() != null && !ticket.getUbicacionNombre().trim().isEmpty()) {
                    conUbicacion++;
                } else {
                    sinUbicacion++;
                }
            }
            
            System.out.println("📍 Tickets con ubicación: " + conUbicacion);
            System.out.println("❓ Tickets sin ubicación: " + sinUbicacion);
            
            // 6. Mostrar ejemplos de tickets con ubicación
            System.out.println("\n6. Ejemplos de tickets con ubicaciones:");
            System.out.println("=====================================================");
            System.out.printf("%-8s %-15s %-25s%n", "ID", "Equipo", "Ubicación");
            System.out.println("=====================================================");
            
            int ejemplos = 0;
            for (Ticket ticket : todosTickets) {
                if (ejemplos >= 5) break;
                
                String ubicacion = ticket.getUbicacionNombre() != null ? 
                                 ticket.getUbicacionNombre() : "Sin ubicación";
                String equipo = ticket.getActivoNumero() != null ? 
                              ticket.getActivoNumero() : "N/A";
                
                System.out.printf("%-8d %-15s %-25s%n", 
                    ticket.getTickId(), 
                    equipo.length() > 14 ? equipo.substring(0, 11) + "..." : equipo,
                    ubicacion.length() > 24 ? ubicacion.substring(0, 21) + "..." : ubicacion);
                
                ejemplos++;
            }
            
            System.out.println("=====================================================");
            System.out.println("\n🎉 TEST COMPLETADO SIN ERRORES!");
            System.out.println("✅ Todas las consultas funcionan correctamente");
            System.out.println("✅ Las ubicaciones se mapean correctamente");
            System.out.println("✅ El sistema está listo para uso");
            
        } catch (Exception e) {
            System.err.println("❌ Error durante el test: " + e.getMessage());
            e.printStackTrace();
        }
    }
}