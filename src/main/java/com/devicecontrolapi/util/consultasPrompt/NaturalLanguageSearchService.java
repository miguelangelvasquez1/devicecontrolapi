package com.devicecontrolapi.util.consultasPrompt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;

@Service
public class NaturalLanguageSearchService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private WebClient.Builder webClientBuilder;
    
    @Value("${ai.api.key}")
    private String apiKey;
    
    @Value("${ai.api.url}")
    private String apiUrl;
        
    
    // Opción 2: Usar Google Gemini
    public String processWithGemini(String prompt, String context) {
        WebClient client = webClientBuilder
            .baseUrl("https://generativelanguage.googleapis.com")
            .build();
        
        Map<String, Object> request = new HashMap<>();
        Map<String, Object> contents = new HashMap<>();
        List<Map<String, String>> parts = new ArrayList<>();
        Map<String, String> part = new HashMap<>();
        part.put("text", buildPrompt(prompt, context));
        parts.add(part);
        contents.put("parts", parts);
        request.put("contents", List.of(contents));
        
        try {
            @SuppressWarnings("rawtypes")
            Map response = client.post()
                .uri("/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
                
            return extractGeminiResponse(response);
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar con Gemini", e);
        }
    }
    
    // Método principal de búsqueda
    public Map<String, Object> searchWithNaturalLanguage(String userPrompt) {
        try {
            // 1. Obtener esquema de la base de datos
            String dbSchema = getDatabaseSchema();
            // System.out.println("Esquema:");
            // System.out.println(dbSchema);
            
            // 2. Generar consulta SQL usando IA
            String sqlQuery = generateSQLQuery(userPrompt, dbSchema);
            System.out.println(sqlQuery);
            
            // 3. Validar y ejecutar consulta
            if (isValidSQL(sqlQuery)) {
                List<Map<String, Object>> results = executeQuery(sqlQuery);
                
                return Map.of(
                    "success", true,
                    "query", sqlQuery,
                    "results", results,
                    "count", results.size()
                );
            } else {
                return Map.of(
                    "success", false,
                    "error", "Consulta SQL inválida generada"
                );
            }
        } catch (Exception e) {
            return Map.of(
                "success", false,
                "error", e.getMessage()
            );
        }
    }
    
    private String buildPrompt(String userPrompt, String dbSchema) {
        return """
            Eres un experto en SQL. Convierte la siguiente consulta en lenguaje natural
            a  una consulta SQL en texto plano, sin markdown.
            Ten en cuenta los nombres de las tablas de la base de datos.
            
            Esquema de la base de datos:
            %s
            
            Consulta del usuario: %s
            
            Reglas:
            1. Devuelve SOLO la consulta SQL, sin explicaciones
            2. Usa solo las tablas y columnas del esquema proporcionado
            3. Limita los resultados a 100 registros máximo
            4. No uses DELETE, UPDATE, INSERT, DROP o ALTER
            5. Solo consultas SELECT
            
            SQL:
            """.formatted(dbSchema, userPrompt);
    }
    
    private String getDatabaseSchema() {
        StringBuilder schema = new StringBuilder();
        
        // Obtener todas las tablas
        List<String> tables = jdbcTemplate.queryForList(
            "SELECT table_name FROM information_schema.tables WHERE table_schema = 'devicecontroldb'",
            String.class
        );
        
        for (String table : tables) {
            schema.append("Tabla: ").append(table).append("\n");
            
            // Obtener columnas de cada tabla
            List<Map<String, Object>> columns = jdbcTemplate.queryForList(
                """
                SELECT column_name, data_type 
                FROM information_schema.columns 
                WHERE table_name = ? AND table_schema = 'devicecontroldb'
                """,
                table
            );
            
            for (Map<String, Object> column : columns) {
                schema.append("  - ")
                    .append(column.get("column_name"))
                    .append(" (")
                    .append(column.get("data_type"))
                    .append(")\n");
            }
            schema.append("\n");
        }
        
        return schema.toString();
    }
    
    private String generateSQLQuery(String prompt, String schema) {
        // Usa el modelo que prefieras
        // return processWithOllama(prompt, schema);
        return processWithGemini(prompt, schema);
    }
    
    private boolean isValidSQL(String sql) {
        String upperSQL = sql.toUpperCase().trim();
        
        // Verificar que solo sea SELECT
        if (!upperSQL.startsWith("SELECT")) {
            return false;
        }
        
        // Verificar que no contenga comandos peligrosos
        String[] dangerousCommands = {
            "DELETE", "UPDATE", "INSERT", "DROP", "ALTER", 
            "CREATE", "TRUNCATE", "EXEC", "EXECUTE"
        };
        
        for (String cmd : dangerousCommands) {
            if (upperSQL.contains(cmd)) {
                return false;
            }
        }
        
        return true;
    }
    
    private List<Map<String, Object>> executeQuery(String sql) {
        // Añadir límite si no existe
        if (!sql.toUpperCase().contains("LIMIT")) {
            sql += " LIMIT 100";
        }
        
        return jdbcTemplate.queryForList(sql);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private String extractGeminiResponse(Map response) {
        try {
            List<Map> candidates = (List<Map>) response.get("candidates");
            Map content = (Map) candidates.get(0).get("content");
            List<Map> parts = (List<Map>) content.get("parts");
            return (String) parts.get(0).get("text");
        } catch (Exception e) {
            throw new RuntimeException("Error al extraer respuesta de Gemini");
        }
    }
}