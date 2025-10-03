import java.util.*;
import com.ypacarai.cooperativa.activos.dao.*;
import com.ypacarai.cooperativa.activos.model.*;

/**
 * Test para verificar que la ventana del técnico muestre múltiples tickets
 * cuando se crean tickets por ubicación
 */
public class TestVentanaTecnicoMultiples {
    
    public static void main(String[] args) {
        try {
            System.out.println("=== TEST VENTANA TÉCNICO CON MÚLTIPLES TICKETS ===");
            
            TicketDAO ticketDAO = new TicketDAO();
            TicketAsignacionDAO asignacionDAO = new TicketAsignacionDAO();
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            ActivoDAO activoDAO = new ActivoDAO();
            
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
            
            // 2. Obtener algunos activos para crear tickets
            System.out.println("\n2. Obteniendo activos para crear tickets...");
            List<Activo> activos = activoDAO.findAll();
            if (activos.size() < 3) {
                System.out.println("❌ Se necesitan al menos 3 activos en la base de datos");
                return;
            }
            
            // Tomar los primeros 3 activos
            List<Activo> activosParaTickets = activos.subList(0, Math.min(3, activos.size()));
            System.out.println("✅ Se usarán " + activosParaTickets.size() + " activos para crear tickets");
            
            // 3. Crear múltiples tickets (simulando creación por ubicación)
            System.out.println("\n3. Creando múltiples tickets...");
            List<Integer> ticketsCreados = new ArrayList<>();
            
            for (int i = 0; i < activosParaTickets.size(); i++) {
                Activo activo = activosParaTickets.get(i);
                
                Ticket ticket = new Ticket();
                ticket.setActId(activo.getActId());
                ticket.setTickTipo(Ticket.Tipo.Correctivo);
                ticket.setTickPrioridad(Ticket.Prioridad.Media);
                ticket.setTickEstado(Ticket.Estado.Abierto);
                ticket.setTickTitulo("Mantenimiento Ubicación - Equipo " + (i + 1));
                ticket.setTickDescripcion("Ticket de prueba para verificar múltiples asignaciones\n\n" +
                    "Equipo: " + activo.getActNumeroActivo() + " - " + activo.getActMarca() + " " + activo.getActModelo());
                ticket.setTickReportadoPor(1);
                
                // Crear el ticket
                Ticket ticketGuardado = ticketDAO.guardar(ticket);
                ticketsCreados.add(ticketGuardado.getTickId());
                System.out.println("  ✅ Ticket creado - ID: " + ticketGuardado.getTickId() + " para activo: " + activo.getActNumeroActivo());
                
                // Asignar el técnico al ticket
                TicketAsignacion asignacion = new TicketAsignacion(
                    ticketGuardado.getTickId(),
                    tecnico.getUsuId(),
                    TicketAsignacion.RolAsignacion.Responsable
                );
                asignacion.setTasObservaciones("Asignación de prueba - Ticket " + (i + 1));
                
                List<TicketAsignacion> asignaciones = Arrays.asList(asignacion);
                boolean asignado = asignacionDAO.asignarTecnicos(ticketGuardado.getTickId(), asignaciones);
                
                if (asignado) {
                    System.out.println("    ➤ Técnico asignado correctamente");
                } else {
                    System.out.println("    ❌ Error al asignar técnico");
                }
            }
            
            // 4. Verificar que el técnico puede ver todos los tickets
            System.out.println("\n4. Verificando tickets asignados al técnico...");
            List<Integer> ticketsAsignados = asignacionDAO.obtenerTicketsAsignados(tecnico.getUsuId());
            
            System.out.println("Tickets asignados al técnico " + tecnico.getUsuNombre() + ":");
            System.out.println("  Total de tickets asignados: " + ticketsAsignados.size());
            
            // Verificar que nuestros tickets están incluidos
            int ticketsEncontrados = 0;
            for (Integer ticketId : ticketsCreados) {
                if (ticketsAsignados.contains(ticketId)) {
                    ticketsEncontrados++;
                    System.out.println("  ✅ Ticket " + ticketId + " está asignado");
                } else {
                    System.out.println("  ❌ Ticket " + ticketId + " NO está asignado");
                }
            }
            
            // 5. Obtener detalles completos de los tickets
            System.out.println("\n5. Obteniendo detalles completos de tickets...");
            List<Ticket> ticketsCompletos = ticketDAO.obtenerPorIds(ticketsAsignados);
            
            System.out.println("Tickets que aparecerán en la ventana del técnico:");
            for (Ticket ticket : ticketsCompletos) {
                if (ticket.getTickEstado() == Ticket.Estado.Abierto || 
                    ticket.getTickEstado() == Ticket.Estado.En_Proceso) {
                    System.out.println("  📋 ID: " + ticket.getTickId() + 
                                     " | Equipo: " + (ticket.getActivoNumero() != null ? ticket.getActivoNumero() : "N/A") +
                                     " | Título: " + ticket.getTickTitulo() +
                                     " | Estado: " + ticket.getTickEstado());
                }
            }
            
            System.out.println("\n🎉 TEST COMPLETADO!");
            System.out.println("📊 Resumen:");
            System.out.println("  - Tickets creados: " + ticketsCreados.size());
            System.out.println("  - Tickets asignados al técnico: " + ticketsAsignados.size());
            System.out.println("  - Tickets encontrados de nuestro test: " + ticketsEncontrados);
            
            if (ticketsEncontrados == ticketsCreados.size()) {
                System.out.println("✅ ÉXITO: Todos los tickets aparecerán en la ventana del técnico");
            } else {
                System.out.println("❌ PROBLEMA: Algunos tickets no aparecerán en la ventana del técnico");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error en el test: " + e.getMessage());
            e.printStackTrace();
        }
    }
}