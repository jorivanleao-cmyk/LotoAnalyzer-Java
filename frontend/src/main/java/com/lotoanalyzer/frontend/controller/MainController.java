package com.lotoanalyzer.frontend.controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class MainController {

    @FXML
    private TextField apiBaseUrlField;
    @FXML
    private Label statusLabel;
    @FXML
    private TextArea outputArea;
    @FXML
    private Button importarButton;
    @FXML
    private Button estatisticasButton;
    @FXML
    private Button tendenciasButton;
    @FXML
    private Button jogosButton;

    private final ApiClientService apiClientService = new ApiClientService();

    @FXML
    public void initialize() {
        apiBaseUrlField.setText("http://localhost:8080/api/loto");
        statusLabel.setText("Backend desconectado");
    }

    @FXML
    public void onImportarHistorico() {
        executar("Importando historico...", () -> {
            String resposta = apiClientService.post(base() + "/importar", "{}");
            outputArea.setText(pretty("Importacao", resposta));
            statusLabel.setText("Historico importado");
        });
    }

    @FXML
    public void onVerEstatisticas() {
        executar("Carregando estatisticas...", () -> {
            String mais = apiClientService.get(base() + "/estatisticas/mais-sorteados");
            String menos = apiClientService.get(base() + "/estatisticas/menos-sorteados");
            outputArea.setText(pretty("Mais sorteados", mais) + "\n\n" + pretty("Menos sorteados", menos));
            statusLabel.setText("Estatisticas atualizadas");
        });
    }

    @FXML
    public void onVerTendencias() {
        executar("Carregando tendencias...", () -> {
            String resposta = apiClientService.get(base() + "/tendencias");
            outputArea.setText(pretty("Tendencias", resposta));
            statusLabel.setText("Tendencias atualizadas");
        });
    }

    @FXML
    public void onGerarJogos() {
        executar("Gerando jogos...", () -> {
            String resposta = apiClientService.get(base() + "/jogos");
            outputArea.setText(pretty("Jogos recomendados", resposta));
            statusLabel.setText("Jogos gerados");
        });
    }

    private String base() {
        return apiBaseUrlField.getText().trim();
    }

    private void executar(String inicio, Task task) {
        statusLabel.setText(inicio);
        try {
            task.run();
        } catch (Exception ex) {
            statusLabel.setText("Falha");
            outputArea.setText("Erro: " + ex.getMessage());
        }
    }

    private String pretty(String titulo, String body) {
        return "=== " + titulo + " ===\n" + body;
    }

    @FunctionalInterface
    private interface Task {
        void run() throws Exception;
    }

    private static class ApiClientService {

        private final HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        String get(String endpoint) throws IOException, InterruptedException {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .timeout(Duration.ofSeconds(30))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw new IOException("Erro HTTP " + response.statusCode() + " em " + endpoint);
            }
            return response.body();
        }

        String post(String endpoint, String jsonBody) throws IOException, InterruptedException {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .timeout(Duration.ofSeconds(60))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody == null ? "{}" : jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw new IOException("Erro HTTP " + response.statusCode() + " em " + endpoint);
            }
            return response.body();
        }
    }
}
