package com.ypacarai.cooperativa.activos.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.Scanner;

/**
 * Utilidades generales del sistema
 * Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA
 */
public class SystemUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(SystemUtils.class);
    
    // Formatters para fechas
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    
    /**
     * Formatea una fecha LocalDateTime a string legible
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "N/A";
        }
        return dateTime.format(DATETIME_FORMATTER);
    }
    
    /**
     * Formatea una fecha LocalDateTime solo la fecha
     */
    public static String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "N/A";
        }
        return dateTime.format(DATE_FORMATTER);
    }
    
    /**
     * Valida que una cadena no sea null o vacía
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
    
    /**
     * Valida que una cadena sea null o vacía
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * Capitaliza la primera letra de una cadena
     */
    public static String capitalize(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
    
    /**
     * Genera un separador de línea
     */
    public static String generateSeparator(int length, char character) {
        return String.valueOf(character).repeat(length);
    }
    
    /**
     * Genera un separador estándar
     */
    public static String generateSeparator(int length) {
        return generateSeparator(length, '-');
    }
    
    /**
     * Cierra recursos de base de datos de manera segura
     */
    public static void closeQuietly(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.warn("Error al cerrar conexión: {}", e.getMessage());
            }
        }
    }
    
    /**
     * Cierra PreparedStatement de manera segura
     */
    public static void closeQuietly(PreparedStatement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                logger.warn("Error al cerrar statement: {}", e.getMessage());
            }
        }
    }
    
    /**
     * Obtiene la versión del sistema desde properties
     */
    public static String getSystemVersion() {
        try (InputStream input = SystemUtils.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                Properties props = new Properties();
                props.load(input);
                return props.getProperty("sistema.version", "1.0.0");
            }
        } catch (IOException e) {
            logger.warn("No se pudo leer la versión del sistema: {}", e.getMessage());
        }
        return "1.0.0";
    }
    
    /**
     * Obtiene el nombre del sistema desde properties
     */
    public static String getSystemName() {
        try (InputStream input = SystemUtils.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                Properties props = new Properties();
                props.load(input);
                return props.getProperty("sistema.nombre", "Sistema de Gestión de Activos");
            }
        } catch (IOException e) {
            logger.warn("No se pudo leer el nombre del sistema: {}", e.getMessage());
        }
        return "Sistema de Gestión de Activos";
    }
    
    /**
     * Obtiene la organización desde properties
     */
    public static String getOrganization() {
        try (InputStream input = SystemUtils.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                Properties props = new Properties();
                props.load(input);
                return props.getProperty("sistema.organizacion", "Cooperativa Ypacaraí LTDA");
            }
        } catch (IOException e) {
            logger.warn("No se pudo leer la organización: {}", e.getMessage());
        }
        return "Cooperativa Ypacaraí LTDA";
    }
    
    /**
     * Muestra el header del sistema
     */
    public static void showSystemHeader() {
        System.out.println();
        System.out.println(generateSeparator(60, '='));
        System.out.println("🏢 " + getSystemName().toUpperCase());
        System.out.println("   " + getOrganization());
        System.out.println("   Versión: " + getSystemVersion());
        System.out.println(generateSeparator(60, '='));
        System.out.println();
    }
    
    /**
     * Pausa la ejecución hasta que el usuario presione Enter
     */
    public static void pressEnterToContinue() {
        System.out.print("\nPresione Enter para continuar...");
        try (Scanner scanner = new Scanner(System.in)) {
            scanner.nextLine();
        }
    }
    
    /**
     * Trunca un texto a un máximo de caracteres
     */
    public static String truncate(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * Valida un email básico
     */
    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) {
            return false;
        }
        return email.contains("@") && email.contains(".");
    }
    
    /**
     * Obtiene el timestamp actual como string
     */
    public static String getCurrentTimestamp() {
        return LocalDateTime.now().format(DATETIME_FORMATTER);
    }
    
    /**
     * Log de inicio de operación
     */
    public static void logOperationStart(String operation, String user) {
        logger.info("=== INICIO: {} === Usuario: {} === Timestamp: {}", 
                   operation, user, getCurrentTimestamp());
    }
    
    /**
     * Log de fin de operación
     */
    public static void logOperationEnd(String operation, String user, boolean success) {
        String status = success ? "EXITOSO" : "FALLIDO";
        logger.info("=== FIN: {} === Usuario: {} === Estado: {} === Timestamp: {}", 
                   operation, user, status, getCurrentTimestamp());
    }
}
