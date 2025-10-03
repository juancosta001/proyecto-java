package com.ypacarai.cooperativa.activos.test;

import java.util.ArrayList;
import java.util.List;

import com.ypacarai.cooperativa.activos.dao.TicketAsignacionDAO;
import com.ypacarai.cooperativa.activos.dao.TicketDAO;
import com.ypacarai.cooperativa.activos.dao.UsuarioDAO;
import com.ypacarai.cooperativa.activos.model.Ticket;
import com.ypacarai.cooperativa.activos.model.TicketAsignacion;
import com.ypacarai.cooperativa.activos.model.Usuario;

/**
 * Test para la funcionalidad de múltiples técnicos asignados
 * Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA
 */
public class TestMultiplesTecnicos {
    
    public static void main(String[] args) {
        System.out.println("🎯 === TEST DE MÚLTIPLES TÉCNICOS ASIGNADOS ===");
        
        try {
            // Inicializar DAOs
            TicketDAO ticketDAO = new TicketDAO();
            TicketAsignacionDAO asignacionDAO = new TicketAsignacionDAO();
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            
            // Obtener técnicos disponibles
            System.out.println("\n📋 Obteniendo técnicos disponibles...");
            List<Usuario> tecnicos = usuarioDAO.obtenerTecnicos();
            System.out.println("✅ Técnicos encontrados: " + tecnicos.size());
            
            for (Usuario tecnico : tecnicos) {
                System.out.println("  👨‍💻 " + tecnico.getUsuNombre() + " (ID: " + tecnico.getUsuId() + ")");
            }
            
            // Obtener un ticket existente para probar
            System.out.println("\n🎫 Obteniendo tickets para probar asignaciones...");
            List<Ticket> tickets = ticketDAO.obtenerTodos();
            
            if (tickets.isEmpty()) {
                System.out.println("❌ No hay tickets para probar. Creando uno de prueba...");
                // Aquí podrías crear un ticket de prueba si quisieras
                return;
            }
            
            Ticket ticketPrueba = tickets.get(0);
            System.out.println("✅ Usando ticket: " + ticketPrueba.getTickId() + " - " + ticketPrueba.getTickTitulo());
            
            // Crear asignaciones múltiples
            System.out.println("\n👥 Creando asignaciones múltiples...");
            List<TicketAsignacion> asignaciones = new ArrayList<>();
            
            if (tecnicos.size() >= 2) {
                // Técnico responsable
                TicketAsignacion responsable = new TicketAsignacion(
                    ticketPrueba.getTickId(),
                    tecnicos.get(0).getUsuId(),
                    TicketAsignacion.RolAsignacion.Responsable,
                    "Técnico principal del ticket"
                );
                asignaciones.add(responsable);
                
                // Técnico colaborador
                TicketAsignacion colaborador = new TicketAsignacion(
                    ticketPrueba.getTickId(),
                    tecnicos.get(1).getUsuId(),
                    TicketAsignacion.RolAsignacion.Colaborador,
                    "Técnico de apoyo"
                );
                asignaciones.add(colaborador);
                
                System.out.println("  👑 Responsable: " + tecnicos.get(0).getUsuNombre());
                System.out.println("  🤝 Colaborador: " + tecnicos.get(1).getUsuNombre());
                
                // Agregar supervisor si hay más técnicos
                if (tecnicos.size() >= 3) {
                    TicketAsignacion supervisor = new TicketAsignacion(
                        ticketPrueba.getTickId(),
                        tecnicos.get(2).getUsuId(),
                        TicketAsignacion.RolAsignacion.Supervisor,
                        "Supervisión del proceso"
                    );
                    asignaciones.add(supervisor);
                    System.out.println("  👁️ Supervisor: " + tecnicos.get(2).getUsuNombre());
                }
                
                // Asignar técnicos
                System.out.println("\n💾 Guardando asignaciones...");
                boolean exito = asignacionDAO.asignarTecnicos(ticketPrueba.getTickId(), asignaciones);
                
                if (exito) {
                    System.out.println("✅ Asignaciones guardadas exitosamente");
                    
                    // Verificar las asignaciones
                    System.out.println("\n🔍 Verificando asignaciones guardadas...");
                    List<TicketAsignacion> asignacionesGuardadas = asignacionDAO.obtenerTecnicosAsignados(ticketPrueba.getTickId());
                    
                    System.out.println("📊 Total de asignaciones: " + asignacionesGuardadas.size());
                    
                    for (TicketAsignacion asig : asignacionesGuardadas) {
                        System.out.println("  🏷️ " + asig.getUsuarioNombre() + 
                                         " - " + asig.getTasRolAsignacion() + 
                                         " (" + asig.getTasObservaciones() + ")");
                    }
                    
                    // Probar resumen de asignaciones
                    System.out.println("\n📝 Resumen de asignaciones:");
                    String resumen = asignacionDAO.obtenerResumenAsignaciones(ticketPrueba.getTickId());
                    System.out.println("  " + resumen);
                    
                    // Probar estadísticas
                    System.out.println("\n📈 Estadísticas por técnico:");
                    var estadisticas = asignacionDAO.obtenerEstadisticasPorTecnico();
                    estadisticas.forEach((nombre, total) -> 
                        System.out.println("  👨‍💻 " + nombre + ": " + total + " asignaciones activas")
                    );
                    
                } else {
                    System.out.println("❌ Error al guardar asignaciones");
                }
                
            } else {
                System.out.println("⚠️ Se necesitan al menos 2 técnicos para probar asignaciones múltiples");
            }
            
            System.out.println("\n🎉 === TEST COMPLETADO ===");
            
        } catch (Exception e) {
            System.err.println("❌ Error durante el test: " + e.getMessage());
            e.printStackTrace();
        }
    }
}