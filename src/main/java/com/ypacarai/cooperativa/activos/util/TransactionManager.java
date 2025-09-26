package com.ypacarai.cooperativa.activos.util;

import java.sql.Connection;
import java.sql.SQLException;

import com.ypacarai.cooperativa.activos.config.DatabaseConfigComplete;

/**
 * Utilidad para manejo seguro de transacciones que previene gaps en AUTO_INCREMENT
 */
public class TransactionManager {
    
    /**
     * Ejecuta una operación en una transacción segura
     * Si falla, hace rollback para evitar consumir AUTO_INCREMENT innecesariamente
     * 
     * @param operation La operación a ejecutar
     * @return El resultado de la operación
     * @throws Exception Si la operación falla
     */
    public static <T> T executeInTransaction(TransactionOperation<T> operation) throws Exception {
        Connection conn = null;
        try {
            conn = DatabaseConfigComplete.getConnection();
            
            // Iniciar transacción
            conn.setAutoCommit(false);
            
            // Ejecutar operación
            T result = operation.execute(conn);
            
            // Si llegamos aquí, todo salió bien
            conn.commit();
            return result;
            
        } catch (Exception e) {
            // Si hay error, hacer rollback
            if (conn != null) {
                try {
                    System.out.println("⚠️ Error detectado, haciendo rollback para prevenir gap en AUTO_INCREMENT");
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("❌ Error haciendo rollback: " + rollbackEx.getMessage());
                }
            }
            throw e; // Re-lanzar la excepción original
        } finally {
            // Restaurar auto-commit y cerrar conexión
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    System.err.println("⚠️ Error cerrando conexión: " + closeEx.getMessage());
                }
            }
        }
    }
    
    /**
     * Versión sin retorno para operaciones void
     */
    public static void executeInTransaction(TransactionVoidOperation operation) throws Exception {
        executeInTransaction(conn -> {
            operation.execute(conn);
            return null;
        });
    }
    
    /**
     * Interfaz funcional para operaciones que retornan un valor
     */
    @FunctionalInterface
    public interface TransactionOperation<T> {
        T execute(Connection conn) throws Exception;
    }
    
    /**
     * Interfaz funcional para operaciones void
     */
    @FunctionalInterface
    public interface TransactionVoidOperation {
        void execute(Connection conn) throws Exception;
    }
}
