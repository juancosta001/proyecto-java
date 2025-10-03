import java.util.*;
import com.ypacarai.cooperativa.activos.dao.*;
import com.ypacarai.cooperativa.activos.model.*;

/**
 * Test para verificar la creación de tickets con asignación de técnicos
 */
public class TestCreacionTicketsConTecnicos {
    
    public static void main(String[] args) {
        try {
            System.out.println("=== TEST CREACIÓN DE TICKETS CON TÉCNICOS ===");
            
            TicketDAO ticketDAO = new TicketDAO();
            TicketAsignacionDAO asignacionDAO = new TicketAsignacionDAO();
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            
            // 1. Crear un ticket de prueba
            System.out.println("\n1. Creando ticket de prueba...");
            Ticket ticketTest = new Ticket();
            ticketTest.setActId(1); // Asumiendo que existe activo con ID 1
            ticketTest.setTickTipo(Ticket.Tipo.Correctivo);
            ticketTest.setTickPrioridad(Ticket.Prioridad.Media);
            ticketTest.setTickEstado(Ticket.Estado.Abierto); // Establecer estado
            ticketTest.setTickTitulo("Test - Problema en equipo");
            ticketTest.setTickDescripcion("Ticket de prueba para verificar asignación de técnicos");
            ticketTest.setTickReportadoPor(1); // Asumiendo que existe usuario con ID 1
            
            // Crear el ticket
            Ticket ticketCreado = ticketDAO.guardar(ticketTest);
            System.out.println("✅ Ticket creado con ID: " + ticketCreado.getTickId());
            
            // 2. Buscar técnicos disponibles
            System.out.println("\n2. Buscando técnicos disponibles...");
            List<Usuario> todosusuarios = usuarioDAO.findAll();
            List<Usuario> tecnicos = new ArrayList<>();
            
            for (Usuario usuario : todosusuarios) {
                if (usuario.getUsuRol() == Usuario.Rol.Tecnico || 
                    usuario.getUsuRol() == Usuario.Rol.Jefe_Informatica) {
                    tecnicos.add(usuario);
                    System.out.println("  - Técnico encontrado: " + usuario.getUsuNombre() + " (ID: " + usuario.getUsuId() + ")");
                }
            }
            
            if (tecnicos.isEmpty()) {
                System.out.println("❌ No se encontraron técnicos disponibles");
                return;
            }
            
            // 3. Asignar técnicos al ticket
            System.out.println("\n3. Asignando técnicos al ticket...");
            List<TicketAsignacion> asignaciones = new ArrayList<>();
            
            // Asignar el primer técnico como responsable
            if (tecnicos.size() > 0) {
                TicketAsignacion asignacion1 = new TicketAsignacion(
                    ticketCreado.getTickId(), 
                    tecnicos.get(0).getUsuId(), 
                    TicketAsignacion.RolAsignacion.Responsable
                );
                asignacion1.setTasObservaciones("Técnico responsable principal");
                asignaciones.add(asignacion1);
                System.out.println("  - Asignado como responsable: " + tecnicos.get(0).getUsuNombre());
            }
            
            // Si hay más técnicos, asignar el segundo como colaborador
            if (tecnicos.size() > 1) {
                TicketAsignacion asignacion2 = new TicketAsignacion(
                    ticketCreado.getTickId(), 
                    tecnicos.get(1).getUsuId(), 
                    TicketAsignacion.RolAsignacion.Colaborador
                );
                asignacion2.setTasObservaciones("Técnico de apoyo");
                asignaciones.add(asignacion2);
                System.out.println("  - Asignado como colaborador: " + tecnicos.get(1).getUsuNombre());
            }
            
            // Ejecutar la asignación
            boolean resultado = asignacionDAO.asignarTecnicos(ticketCreado.getTickId(), asignaciones);
            
            if (resultado) {
                System.out.println("✅ Técnicos asignados exitosamente!");
                
                // 4. Verificar las asignaciones
                System.out.println("\n4. Verificando asignaciones...");
                List<TicketAsignacion> asignacionesVerificadas = asignacionDAO.obtenerTecnicosAsignados(ticketCreado.getTickId());
                
                System.out.println("Técnicos asignados al ticket " + ticketCreado.getTickId() + ":");
                for (TicketAsignacion asig : asignacionesVerificadas) {
                    System.out.println("  - " + asig.getUsuarioNombre() + 
                                     " (" + asig.getTasRolAsignacion() + 
                                     ") - " + asig.getTasObservaciones());
                }
                
                System.out.println("\n🎉 TEST COMPLETADO EXITOSAMENTE!");
                
            } else {
                System.out.println("❌ Error al asignar técnicos");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error en el test: " + e.getMessage());
            e.printStackTrace();
        }
    }
}