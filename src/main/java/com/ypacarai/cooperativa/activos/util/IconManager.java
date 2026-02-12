package com.ypacarai.cooperativa.activos.util;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestor de iconos usando símbolos Unicode básicos compatibles con Windows
 */
public class IconManager {
    
    private static IconManager instance;
    private Font iconFont;
    
    // Mapeo de iconos con símbolos Unicode básicos
    private final Map<String, String> iconos = new HashMap<>();
    
    private IconManager() {
        inicializarFuentes();
        configurarIconos();
    }
    
    public static IconManager getInstance() {
        if (instance == null) {
            instance = new IconManager();
        }
        return instance;
    }
    
    /**
     * Método para debug - mostrar información sobre iconos
     */
    public void mostrarInfoDebug() {
        System.out.println("=== IconManager Debug Info ===");
        System.out.println("Fuente utilizada: " + iconFont.getFamily());
        System.out.println("Tamaño fuente: " + iconFont.getSize());
        System.out.println("Iconos configurados: " + iconos.size());
        System.out.println("Ejemplo - FICHA: '" + getIcon("FICHA") + "'");
        System.out.println("Ejemplo - DASHBOARD: '" + getIcon("DASHBOARD") + "'");
        System.out.println("===============================");
    }
    
    private void inicializarFuentes() {
        // Usar Segoe UI Emoji que viene por defecto en Windows 10+ y soporta emojis
        try {
            iconFont = new Font("Segoe UI Emoji", Font.PLAIN, 12);
            System.out.println("IconManager: Usando Segoe UI Emoji para emojis");
        } catch (Exception e) {
            // Fallback a Segoe UI estándar
            iconFont = new Font("Segoe UI", Font.PLAIN, 12);
            System.out.println("IconManager: Fallback a Segoe UI");
        }
    }
    
    private void configurarIconos() {
        // Iconos principales del sistema
        iconos.put("FICHA", "📋");
        iconos.put("DASHBOARD", "📊");  
        iconos.put("COMPUTER", "💻");
        iconos.put("TICKET", "🎫");
        iconos.put("TRASLADO", "🚚");
        iconos.put("MANTENIMIENTO", "🔧");
        iconos.put("REPORTES", "📈");
        iconos.put("USUARIOS", "👥");
        iconos.put("CONFIG", "⚙️");
        iconos.put("ACTIVO", "🖥️");
        
        // Iconos de formularios y operaciones CRUD
        iconos.put("NUEVO", "🆕");
        iconos.put("CREAR", "➕");
        iconos.put("EDITAR", "✏️");
        iconos.put("ELIMINAR", "🗑️");
        iconos.put("VER", "👁️");
        iconos.put("BUSCAR", "🔍");
        iconos.put("FILTRAR", "🔽");
        iconos.put("LIMPIAR", "🧹");
        iconos.put("ACTUALIZAR", "🔄");
        iconos.put("REFRESCAR", "🔄");
        
        // Iconos de acciones
        iconos.put("GUARDAR", "💾");
        iconos.put("CANCELAR", "❌");
        iconos.put("CERRAR", "❌");
        iconos.put("ENVIAR", "📤");
        iconos.put("IMPRIMIR", "🖨️");
        iconos.put("EXPORTAR", "📦");
        iconos.put("IMPORTAR", "📥");
        iconos.put("COPIAR", "📋");
        iconos.put("PEGAR", "📂");
        
        // Iconos de estado y validación
        iconos.put("SUCCESS", "✅");
        iconos.put("ERROR", "❌");
        iconos.put("WARNING", "⚠️");
        iconos.put("INFO", "ℹ️");
        iconos.put("ESTADO_OK", "✅");
        iconos.put("ESTADO_ENVIADA", "✅");
        iconos.put("ESTADO_BORRADOR", "✏️");
        iconos.put("ESTADO_ARCHIVADA", "📁");
        iconos.put("ACTIVO_ESTADO", "✅");
        iconos.put("INACTIVO_ESTADO", "❌");
        
        // Iconos de fechas y tiempo
        iconos.put("FECHA", "📅");
        iconos.put("CALENDARIO", "📅");
        iconos.put("RELOJ", "⏰");
        iconos.put("TIEMPO", "🕐");
        iconos.put("VENCIDO", "⏰");
        iconos.put("PROGRAMADO", "⏲️");
        
        // Iconos específicos de mantenimiento
        iconos.put("HERRAMIENTAS", "🔧");
        iconos.put("COMPONENTES", "🔩");
        iconos.put("REPARACION", "🛠️");
        iconos.put("PREVENTIVO", "🔧");
        iconos.put("CORRECTIVO", "⚡");
        iconos.put("SERVICIO", "🔧");
        iconos.put("TERCERIZADO", "🏢");
        
        // Iconos de datos y análisis
        iconos.put("ESTADISTICAS", "📊");
        iconos.put("GRAFICO", "📈");
        iconos.put("GRAFICO_BAJO", "📉");
        iconos.put("TABLA", "📋");
        iconos.put("LISTA", "📝");
        iconos.put("DETALLE", "📄");
        iconos.put("RESUMEN", "📑");
        
        // Iconos de alertas y notificaciones
        iconos.put("ALERTA", "⚠️");
        iconos.put("NOTIFICACION", "🔔");
        iconos.put("MENSAJE", "💬");
        iconos.put("IMPORTANTE", "❗");
        iconos.put("CRITICO", "🔴");
        iconos.put("URGENTE", "🚨");
        
        // Iconos de navegación y menú
        iconos.put("MENU", "☰");
        iconos.put("INICIO", "🏠");
        iconos.put("ATRAS", "⬅️");
        iconos.put("ADELANTE", "➡️");
        iconos.put("ARRIBA", "⬆️");
        iconos.put("ABAJO", "⬇️");
        iconos.put("EXPAND", "📤");
        iconos.put("COLLAPSE", "📥");
        
        // Iconos de documentos y archivos
        iconos.put("DOCUMENTO", "📄");
        iconos.put("ARCHIVO", "📁");
        iconos.put("CARPETA", "📁");
        iconos.put("PDF", "📄");
        iconos.put("EXCEL", "📊");
        iconos.put("WORD", "📝");
        iconos.put("IMAGEN", "🖼️");
        
        // Iconos de comunicación
        iconos.put("EMAIL", "📧");
        iconos.put("TELEFONO", "📞");
        iconos.put("CONTACTO", "📞");
        iconos.put("CHAT", "💬");
        iconos.put("COMENTARIO", "💭");
        iconos.put("OBSERVACIONES", "💭");
        
        // Iconos de seguridad y permisos
        iconos.put("SEGURIDAD", "🔒");
        iconos.put("BLOQUEADO", "🔒");
        iconos.put("DESBLOQUEADO", "🔓");
        iconos.put("CLAVE", "🔑");
        iconos.put("CONTRASEÑA", "🔒");
        iconos.put("NOMBRE_USUARIO", "🔑");
        iconos.put("USUARIO", "👤");
        iconos.put("PERMISOS", "🛡️");
        iconos.put("ADMIN", "👑");
        iconos.put("ROL", "👑");
        iconos.put("SUPERVISOR", "👥");
        iconos.put("PERSONA", "👤");
        iconos.put("GENTE", "👥");
        
        // Otros iconos útiles
        iconos.put("CONSEJO", "💡");
        iconos.put("INFORMACION", "ℹ️");
        iconos.put("IDEA", "💡");
        
        // Iconos adicionales comunes
        iconos.put("CONFIGURACION", "⚙️");
        iconos.put("AJUSTES", "⚙️");
        iconos.put("OPCIONES", "⚙️");
        iconos.put("PREFERENCIAS", "⚙️");
        iconos.put("PROBLEMA", "⚠️");
        iconos.put("SOLUCION", "✅");
        iconos.put("PROCESO", "⚙️");
        iconos.put("TAREA", "✓");
        iconos.put("COMPLETADO", "✅");
        iconos.put("PENDIENTE", "⏳");
        iconos.put("EN_PROCESO", "⚙️");
    }
    
    /**
     * Obtiene el icono como símbolo Unicode
     */
    public String getIcon(String key) {
        return iconos.getOrDefault(key, "?");
    }
    
    /**
     * Obtiene la fuente optimizada para mostrar iconos
     */
    public Font getIconFont(int size) {
        return iconFont.deriveFont((float) size);
    }
    
    /**
     * Verifica si el sistema soporta emojis (siempre true para símbolos básicos)
     */
    public boolean isEmojiSupported() {
        return true; // Los símbolos básicos siempre funcionan
    }
    
    /**
     * Crea un texto con icono concatenado
     */
    public String withIcon(String key, String text) {
        return getIcon(key) + " " + text;
    }
    
    /**
     * Obtiene solo el símbolo
     */
    public String getEmojiForced(String key) {
        return getIcon(key);
    }
}