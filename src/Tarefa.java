import java.util.Date;

public class Tarefa {

    private int id;
    private String descricao;
    private Date dataPrevisao;
    private boolean concluida;
    private long tempoTrabalho;

    public Tarefa() {
        // Construtor padrão vazio
    }

    public Tarefa(int id, String descricao, Date dataPrevisao, boolean concluida) {
        this.id = id;
        this.descricao = descricao;
        this.dataPrevisao = dataPrevisao;
        this.concluida = concluida;
    }

    public Tarefa(String descricao, Date dataPrevisao, boolean concluido, long tempoTrabalho) {
        this.descricao = descricao;
        this.dataPrevisao = dataPrevisao;
        this.concluida = concluido;
        this.tempoTrabalho = tempoTrabalho;
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getDataPrevisao() {
        return dataPrevisao;
    }

    public void setDataPrevisao(Date dataPrevisao) {
        this.dataPrevisao = dataPrevisao;
    }

    public boolean isConcluida() {
        return concluida;
    }

    public void setConcluida(boolean concluida) {
        this.concluida = concluida;
    }

    public long getTempoTrabalho() {
        return tempoTrabalho;
    }

    public void setTempoTrabalho(long tempoTrabalho) {
        this.tempoTrabalho = tempoTrabalho;
    }
}


