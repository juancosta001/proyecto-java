package com.ypacarai.cooperativa.activos.test;

import com.ypacarai.cooperativa.activos.dao.TicketDAO;
import com.ypacarai.cooperativa.activos.dao.ActivoDAO;
import com.ypacarai.cooperativa.activos.model.Ticket;
import com.ypacarai.cooperativa.activos.model.Activo;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Script para crear tickets de prueba para el técnico
 */
public class CrearTicketsPrueba {
    
    public static void main(String[] args) {
        System.out.println("=== Creando tickets de prueba para técnico ===");
        
        try {
            TicketDAO ticketDAO = new TicketDAO();
            ActivoDAO activoDAO = new ActivoDAO();
            
            // Obtener un activo existente
            List<Activo> activos = activoDAO.findAll();
            if (activos.isEmpty()) {
                System.out.println("❌ No hay activos en el sistema para crear tickets");
                return;
            }
            
            Activo activo = activos.get(0); // Usar el primer activo disponible
            System.out.println("✅ Usando activo: " + activo.getActNumeroActivo());
            
            // ID del técnico jose
            int tecnicoId = 4;
            
            // Crear tickets de prueba
            String[] titulosTickets = {
                "Mantenimiento preventivo - Limpieza general",
                "Revisión trimestral de componentes",
                "Actualización de software pendiente",
                "Verificación de conectividad de red"
            };
            
            Ticket.Prioridad[] prioridades = {
                Ticket.Prioridad.Alta,
                Ticket.Prioridad.Media,
                Ticket.Prioridad.Baja,
                Ticket.Prioridad.Media
            };
            
            for (int i = 0; i < titulosTickets.length; i++) {
                Ticket ticket = new Ticket();
                ticket.setActId(activo.getActId());
                ticket.setTickTipo(Ticket.Tipo.Preventivo);
                ticket.setTickPrioridad(prioridades[i]);
                ticket.setTickTitulo(titulosTickets[i]);
                ticket.setTickDescripcion("Ticket de prueba creado automáticamente para verificar el panel de mantenimiento técnico.");
                ticket.setTickEstado(Ticket.Estado.Abierto);
                ticket.setTickFechaApertura(LocalDateTime.now().minusDays(i));
                ticket.setTickFechaVencimiento(LocalDateTime.now().plusDays(7 + i));
                ticket.setTickAsignadoA(tecnicoId);
                ticket.setTickReportadoPor(1); // Usuario admin
                ticket.setTickNotificacionEnviada(false);
                ticket.setCreadoEn(LocalDateTime.now());
                ticket.setActualizadoEn(LocalDateTime.now());
                
                // Generar número de ticket
                ticket.setTickNumero("MANT-" + System.currentTimeMillis() + "-" + i);
                
                boolean guardado = ticketDAO.save(ticket);
                if (guardado) {
                    System.out.println("✅ Ticket creado - Título: " + titulosTickets[i]);
                } else {
                    System.out.println("❌ Error al crear ticket: " + titulosTickets[i]);
                }
            }
            
            System.out.println();
            System.out.println("🎉 Se crearon " + titulosTickets.length + " tickets de prueba");
            System.out.println("   Asignados al técnico ID: " + tecnicoId + " (jose)");
            System.out.println("   Estado: Abierto");
            System.out.println("   Ahora el panel de mantenimiento debería mostrar estos tickets");
            
        } catch (Exception e) {
            System.out.println("❌ Error al crear tickets: " + e.getMessage());
            e.printStackTrace();
        }
    }
}