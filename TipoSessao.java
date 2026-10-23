public enum TipoSessao {

    // O tempo é editável, é só mudar os números abaixo.
    FOCO("Foco", 25),
    DESCANSO_CURTO("Descanso curto", 5),
    DESCANSO_LONGO("Descanso longo", 15);

    private final String nome;
    private final int minutos;

    TipoSessao(String nome, int minutos) {
        this.nome = nome;
        this.minutos = minutos;
    }

    public String getNome() { return nome; }
    public int getMinutos() { return minutos; }

    public int getSegundos() { return minutos * 60; }
}
