package com.ypacarai.cooperativa.activos.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Configuración de conexión a la base de datos MySQL
 * Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA
 */
public class DatabaseConfigComplete {
    
    private static final String PROPERTIES_FILE = "application.properties";
    private static Properties properties;
    
    // Parámetros de conexión
    private static String DB_HOST;
    private static String DB_PORT;
    private static String DB_DATABASE;
    private static String DB_USERNAME;
    private static String DB_PASSWORD;
    private static String DB_URL;
    
    static {
        loadProperties();
        initializeConnectionParameters();
    }
    
    /**
     * Carga las propiedades desde application.properties
     */
    private static void loadProperties() {
        properties = new Properties();
        try (InputStream input = DatabaseConfigComplete.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) {
                System.err.println("No se pudo encontrar el archivo " + PROPERTIES_FILE);
                throw new RuntimeException("Archivo de configuración no encontrado: " + PROPERTIES_FILE);
            }
            properties.load(input);
            System.out.println("Propiedades de configuración cargadas correctamente");
        } catch (IOException e) {
            System.err.println("Error al cargar propiedades: " + e.getMessage());
            throw new RuntimeException("Error al cargar configuración", e);
        }
    }
    
    /**
     * Inicializa los parámetros de conexión
     */
    private static void initializeConnectionParameters() {
        DB_HOST = properties.getProperty("db.host", "localhost");
        DB_PORT = properties.getProperty("db.port", "3306");
        DB_DATABASE = properties.getProperty("db.database", "sistema_activos_ypacarai");
        DB_USERNAME = properties.getProperty("db.username", "root");
        DB_PASSWORD = properties.getProperty("db.password", "");
        
        // Construir URL de conexión
        DB_URL = String.format(
            "jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Asuncion",
            DB_HOST, DB_PORT, DB_DATABASE
        );
        
        System.out.println("Parámetros de conexión inicializados - Host: " + DB_HOST + ":" + DB_PORT + ", Database: " + DB_DATABASE);
    }
    
    /**
     * Obtiene una conexión a la base de datos
     * @return Connection
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Cargar el driver de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            // Conexión establecida exitosamente
            return connection;
            
        } catch (ClassNotFoundException e) {
            System.err.println("Driver MySQL no encontrado: " + e.getMessage());
            throw new SQLException("Driver MySQL no disponible", e);
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Cierra una conexión de manera segura
     * @param connection
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                // Conexión cerrada correctamente
            } catch (SQLException e) {
                System.err.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
    }
    
    /**
     * Verifica si la conexión a la base de datos está disponible
     * @return true si la conexión es exitosa
     */
    public static boolean testConnection() {
        try (Connection connection = getConnection()) {
            boolean isValid = connection.isValid(5); // Timeout de 5 segundos
            System.out.println("Test de conexión: " + (isValid ? "EXITOSO" : "FALLIDO"));
            return isValid;
        } catch (SQLException e) {
            System.err.println("Test de conexión fallido: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtiene información de configuración
     * @return Properties
     */
    public static Properties getProperties() {
        return (Properties) properties.clone();
    }
    
    /**
     * Obtiene la URL de conexión
     * @return String URL
     */
    public static String getDatabaseUrl() {
        return DB_URL;
    }
    
    /**
     * Obtiene el nombre de la base de datos
     * @return String database name
     */
    public static String getDatabaseName() {
        return DB_DATABASE;
    }
}
