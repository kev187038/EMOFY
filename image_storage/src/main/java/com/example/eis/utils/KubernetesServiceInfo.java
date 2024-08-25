package com.example.eis.utils;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.util.Config;

public class KubernetesServiceInfo {

    private final CoreV1Api api;

    public KubernetesServiceInfo() {
        CoreV1Api tempApi = null;
        try {
            // Configura il client di Kubernetes usando il contesto corrente
            ApiClient client = Config.defaultClient();
            Configuration.setDefaultApiClient(client);

            // Crea un'istanza dell'API Core V1
            tempApi = new CoreV1Api();
        } catch (Exception e) {
            // Gestisci le eccezioni durante la configurazione
            System.err.println("Errore durante la configurazione del client Kubernetes: " + e.getMessage());
            e.printStackTrace();
            // Potresti decidere di gestire il fallback in un altro modo, ad esempio, creando un'istanza vuota
        }

        this.api = tempApi;
    }

    /**
     * Ottieni l'IP e la porta del servizio Kubernetes.
     * 
     * @param serviceName Il nome del servizio.
     * @param namespace Lo spazio dei nomi in cui si trova il servizio.
     * @return Una stringa contenente l'IP e la porta del servizio.
     * @throws ApiException Se c'è un errore durante la chiamata all'API di Kubernetes.
     */
    public String getServiceUrl(String serviceName, String namespace) throws ApiException {
        if (this.api == null) {
            throw new ApiException("API client non inizializzato correttamente.");
        }
        
        // Ottieni i dettagli del servizio
        V1Service service = api.readNamespacedService(serviceName, namespace, null);

        // Estrai l'IP del cluster e la porta
        String clusterIp = service.getSpec().getClusterIP();
        Integer port = service.getSpec().getPorts().get(0).getPort();

        // Costruisci l'URL del servizio
        return clusterIp + ":" + port;
    }
}
