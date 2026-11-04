package com.hemobit.hemobit.telemetria;

/**
 * Leitura enviada por um veículo de transporte: posição GPS, temperatura do compartimento e
 * instante de envio (epoch ms). A sequência é crescente por veículo e permite detectar perdas.
 */
public class LeituraTelemetria {
    private String veiculo;
    private Long sequencia;
    private String tipoHemocomponente;
    private Double latitude;
    private Double longitude;
    private Double temperaturaC;
    private Long enviadoEm;

    public String getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(String veiculo) {
        this.veiculo = veiculo;
    }

    public Long getSequencia() {
        return sequencia;
    }

    public void setSequencia(Long sequencia) {
        this.sequencia = sequencia;
    }

    public String getTipoHemocomponente() {
        return tipoHemocomponente;
    }

    public void setTipoHemocomponente(String tipoHemocomponente) {
        this.tipoHemocomponente = tipoHemocomponente;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getTemperaturaC() {
        return temperaturaC;
    }

    public void setTemperaturaC(Double temperaturaC) {
        this.temperaturaC = temperaturaC;
    }

    public Long getEnviadoEm() {
        return enviadoEm;
    }

    public void setEnviadoEm(Long enviadoEm) {
        this.enviadoEm = enviadoEm;
    }
}
