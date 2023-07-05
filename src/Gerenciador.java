// javac -cp "lib/mysql-connector-java-8.0.28.jar;src" src/*.java
// java -cp "lib/mysql-connector-java-8.0.28.jar;src" App

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


//========================================================================================< Gerenciador >========================================================================================//
    
public class Gerenciador {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private Connection connection;
    //private List<Tarefa> tarefas;

    public Gerenciador(Connection connection) {
        this.connection = connection;
        //this.tarefas = new ArrayList<>();
    }

    public void criarTabelaTarefas() {
        try {
            String createQuery = "CREATE TABLE IF NOT EXISTS tarefas ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT,"
                    + "descricao VARCHAR(255) NOT NULL,"
                    + "data_previsao DATE NOT NULL,"
                    + "concluida BOOLEAN NOT NULL DEFAULT FALSE,"
                    + "tempo_trabalho BIGINT NOT NULL DEFAULT 0"
                    + ")";
            PreparedStatement createStatement = connection.prepareStatement(createQuery);
            createStatement.executeUpdate();

            System.out.println("Tabela 'tarefas' criada com sucesso!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


//========================================================================================< Exibir Menu >========================================================================================//


    public void exibirMenu() {
        System.out.println("\n--- Menu ---");
        System.out.println("0. Exibir todas as tarefas");
        System.out.println("1. Adicionar tarefa");
        System.out.println("2. Remover tarefa");
        System.out.println("3. Editar tarefa");
        System.out.println("4. Alterar status da tarefa");
        System.out.println("5. Exibir tarefas por palavra-chave");
        System.out.println("6. Exibir tarefas por periodo");
        System.out.println("7. Limpar tabela");
        System.out.println("8. Registrar tempo de trabalho em uma tarefa");
        System.out.println("9. Exportar tarefas");
        System.out.println("10. Importar tarefas");
        System.out.println("11. Encerrar programa");
        System.out.println("\nEscolha uma opcao: ");
    }


//========================================================================================< AdicionarTarefa >====================================================================================//


    public void adicionarTarefa(Scanner scanner) {
        System.out.println("Descricao da tarefa: ");
        scanner.nextLine();
        String descricao = scanner.nextLine();

        boolean dataValida = false;
        while (!dataValida) {
            System.out.println("\nData de previsao da tarefa (DD/MM/AAAA): ");
            String dataString = scanner.next();

            try {
                Date dataPrevisao = dateFormat.parse(dataString);

                String insertQuery = "INSERT INTO tarefas (descricao, data_previsao, concluida) VALUES (?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
                preparedStatement.setString(1, descricao);
                preparedStatement.setDate(2, new java.sql.Date(dataPrevisao.getTime()));
                preparedStatement.setBoolean(3, false);
                preparedStatement.executeUpdate();

                System.out.println("A tarefa foi adicionada com sucesso!");
                dataValida = true;
            } catch (ParseException | SQLException e) {
                System.out.println("Data invalida. Tente novamente.");
            }
        }
    }


//========================================================================================< RemoverTarefa >=====================================================================================//


    public void removerTarefa(Scanner scanner) {
        System.out.println("Digite o indice da tarefa que deseja remover: ");
        int numero = scanner.nextInt();

        try {
            String deleteQuery = "DELETE FROM tarefas WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);
            preparedStatement.setInt(1, numero);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("A tarefa foi removida com sucesso!");
                exibirTarefas();
            } else {
                System.out.println("Indice invalido, tente novamente!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


//========================================================================================< EditarTarefa >======================================================================================//


    public void editarTarefa(Scanner scanner) {
        System.out.println("Digite o indice da tarefa que deseja editar: ");
        int indice = scanner.nextInt();

        scanner.nextLine();

        try {
            String selectQuery = "SELECT * FROM tarefas WHERE id = ?";
            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
            selectStatement.setInt(1, indice);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                System.out.println("Digite a nova descrição da tarefa: ");
                String novaDescricao = scanner.nextLine();

                System.out.println("Digite a nova data de previsão da tarefa (dd/MM/yyyy): ");
                String novaDataStr = scanner.nextLine();

                try {
                    Date novaData = dateFormat.parse(novaDataStr);

                    String updateQuery = "UPDATE tarefas SET descricao = ?, data_previsao = ? WHERE id = ?";
                    PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                    updateStatement.setString(1, novaDescricao);
                    updateStatement.setDate(2, new java.sql.Date(novaData.getTime()));
                    updateStatement.setInt(3, indice);
                    updateStatement.executeUpdate();

                    System.out.println("A tarefa foi editada com sucesso!");
                    exibirTarefas();
                } catch (ParseException | SQLException e) {
                    System.out.println("Formato de data inválido. A edição da data falhou!");
                }
            } else {
                System.out.println("indice invalido, tente novamente!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


//========================================================================================< Alterar Status >=====================================================================================//


    public void alterarStatus(Scanner scanner) {
        System.out.println("Digite o indice da tarefa que deseja alterar o status: ");
        int indice = scanner.nextInt();

        try {
            String selectQuery = "SELECT * FROM tarefas WHERE id = ?";
            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
            selectStatement.setInt(1, indice);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                boolean concluida = resultSet.getBoolean("concluida");
                boolean novoStatus = !concluida;

                String updateQuery = "UPDATE tarefas SET concluida = ? WHERE id = ?";
                PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                updateStatement.setBoolean(1, novoStatus);
                updateStatement.setInt(2, indice);
                updateStatement.executeUpdate();

                String status = novoStatus ? "concluída" : "pendente";
                System.out.println("Status da tarefa alterado para " + status + "\n");
                exibirTarefas();
            } else {
                System.out.println("indice invalido. Tente novamente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


//========================================================================================< pesquisarPalavraChave >==============================================================================//


    public void pesquisarTarefasPorPalavraChave(Scanner scanner) {
        System.out.println("Digite a palavra-chave para pesquisa: ");
        scanner.nextLine();
        String palavraChave = scanner.nextLine();

        try {
            String selectQuery = "SELECT * FROM tarefas WHERE descricao LIKE ?";
            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
            selectStatement.setString(1, "%" + palavraChave + "%");
            ResultSet resultSet = selectStatement.executeQuery();

            setarAtributosTarefas(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


//========================================================================================< TarefasPorPeriodo >=================================================================================//

    public void listarTarefasPorPeriodo(Scanner scanner) {
        System.out.println("Digite a data inicial (dd/MM/yyyy): ");
        String dataInicialStr = scanner.next();
        System.out.println("Digite a data final (dd/MM/yyyy): ");
        String dataFinalStr = scanner.next();

        try {
            Date dataInicial = dateFormat.parse(dataInicialStr);
            Date dataFinal = dateFormat.parse(dataFinalStr);

            String selectQuery = "SELECT * FROM tarefas WHERE data_previsao BETWEEN ? AND ?";
            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
            selectStatement.setDate(1, new java.sql.Date(dataInicial.getTime()));
            selectStatement.setDate(2, new java.sql.Date(dataFinal.getTime()));
            ResultSet resultSet = selectStatement.executeQuery();

            setarAtributosTarefas(resultSet);
        } catch (ParseException | SQLException e) {
            e.printStackTrace();
        }
    }


//========================================================================================< ExibirTarefas >=====================================================================================//
    
        public void exibirTarefas() {
        // recupera tarefas da tabela
        // executa a instrução sql 
        try {
            String selectQuery = "SELECT * FROM tarefas";
            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
            ResultSet resultSet = selectStatement.executeQuery();

            setarAtributosTarefas(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


//========================================================================================< setarAtributos >===================================================================================//


    private void setarAtributosTarefas(ResultSet resultSet) throws SQLException {
    List<Tarefa> tarefas = new ArrayList<>();

    while (resultSet.next()) {
        int id = resultSet.getInt("id");
        String descricao = resultSet.getString("descricao");
        Date dataPrevisao = resultSet.getDate("data_previsao");
        boolean concluida = resultSet.getBoolean("concluida");
        long tempoTrabalho = resultSet.getLong("tempo_trabalho");

        Tarefa tarefa = new Tarefa(id, descricao, dataPrevisao, concluida);
        tarefa.setTempoTrabalho(tempoTrabalho);
        tarefas.add(tarefa);
    }

    if (tarefas.isEmpty()) {
        System.out.println("Nenhuma tarefa encontrada.");
    } else {
        Collections.sort(tarefas, Comparator.comparingInt(Tarefa::getId));

        System.out.println("Lista de Tarefas:");
        for (int i = 0; i < tarefas.size(); i++) {
            Tarefa tarefa = tarefas.get(i);
            String concluida = tarefa.isConcluida() ? "Concluida" : "Pendente";
            System.out.println("---" + tarefa.getId() + ". Descricao: " + tarefa.getDescricao() +
                    " | Data: " + dateFormat.format(tarefa.getDataPrevisao()) +
                    " | Status: " + concluida +
                    " | Tempo de Trabalho: " + formatarTempo(tarefa.getTempoTrabalho()) + "---");
            }
        }
    }


//=========================================================================================< limparTabela >=========================================================================================//


    public void limparTabelaTarefas() {
        try {
            String deleteQuery = "DELETE FROM tarefas";
            Statement statement = connection.createStatement();
            int rowsAffected = statement.executeUpdate(deleteQuery);

            if (rowsAffected > 0) {
                System.out.println("Tabela 'tarefas' foi limpa com sucesso!");
            } else {
                System.out.println("A tabela 'tarefas' já está vazia.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


//========================================================================================< TarefaEspecifica >========================================================================================//


    private void TarefaEspecifica(Tarefa tarefa) {
        String concluida = tarefa.isConcluida() ? "Concluida" : "Pendente";
        System.out.println("---" + tarefa.getId() + ". Descricao: " + tarefa.getDescricao() +
                " | Data: " + dateFormat.format(tarefa.getDataPrevisao()) +
                " | Status: " + concluida +
                " | Tempo de Trabalho: " + formatarTempo(tarefa.getTempoTrabalho()) + "---");
    }


//=======================================================================================< RegistrarTempoTrab >======================================================================================//


    public void registrarTempoTrabalho(Scanner scanner) {
        System.out.println("Digite o indice da tarefa para registrar o tempo de trabalho: ");
        int indice = scanner.nextInt();
        System.out.println();

        try {
            String selectQuery = "SELECT * FROM tarefas WHERE id = ?";
            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
            selectStatement.setInt(1, indice);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String descricao = resultSet.getString("descricao");
                Date dataPrevisao = resultSet.getDate("data_previsao");
                boolean concluida = resultSet.getBoolean("concluida");
                long tempoTrabalhoAnterior = resultSet.getLong("tempo_trabalho");

                Tarefa tarefa = new Tarefa(id, descricao, dataPrevisao, concluida);
                tarefa.setTempoTrabalho(tempoTrabalhoAnterior);


                System.out.println("Registrando tempo de trabalho para a tarefa:");
                TarefaEspecifica(tarefa);

                System.out.println("Iniciando contador de tempo de trabalho...");
                long tempoInicial = System.currentTimeMillis();
                System.out.println();

                boolean pausado = false;
                do {
                    System.out.println("1. Pausar/Retomar");
                    System.out.println("2. Parar");
                    System.out.println("Escolha uma opcao: ");
                    int opcao = scanner.nextInt();
                

                    switch (opcao) {
                        case 1:
                            if (!pausado) {
                                pausado = true;
                                System.out.println();
                                System.out.println("Tempo de trabalho pausado.");
                            } else {
                                pausado = false;
                                System.out.println();
                                System.out.println("Tempo de trabalho retomado.");
                            }
                            break;
                        case 2:
                            long tempoFinal = System.currentTimeMillis();
                            long tempoTrabalho = tempoFinal - tempoInicial;

                            tempoTrabalho += tempoTrabalhoAnterior;

                            System.out.println("Tempo de Trabalho: " + formatarTempo(tempoTrabalho));
                            long tempoTrabalhoFormatado = tempoTrabalho;

                            // Atualizar o tempo total de trabalho da tarefa no banco de dados
                            String updateQuery = "UPDATE tarefas SET tempo_trabalho = ? WHERE id = ?";
                            PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                            updateStatement.setLong(1, tempoTrabalhoFormatado);
                            updateStatement.setInt(2, indice);
                            updateStatement.executeUpdate();

                            System.out.println("Tempo de trabalho registrado com sucesso!");

                            return;
                        default:
                            System.out.println("Opção inválida. Tente novamente!");
                            break;
                    }
                } while (true);
            } else {
                System.out.println("indice invalido. Tente novamente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    } 


    private String formatarTempo(long tempo) {
        long segundos = tempo / 1000;
        long horas = segundos / 3600;
        long minutos = (segundos % 3600) / 60;
        segundos = segundos % 60;
    
        return String.format("%02d:%02d:%02d", horas, minutos, segundos);
    }    


//=====================================================================================< Exportar >==================================================================================//


    public void exportarTarefasParaCSV(String nomeArquivo) {
        // Verificar se o nome do arquivo possui a extensão .csv
        if (!nomeArquivo.endsWith(".csv")) {
            nomeArquivo += ".csv"; // Adicionar a extensão .csv
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeArquivo))) {
            String selectQuery = "SELECT * FROM tarefas";
            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
            ResultSet resultSet = selectStatement.executeQuery();

            // Escrever cabeçalho do CSV
            writer.write("ID,Descrição,Data de Previsão,Concluída,Tempo de Trabalho\n");

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String descricao = resultSet.getString("descricao");
                Date dataPrevisao = resultSet.getDate("data_previsao");
                boolean concluida = resultSet.getBoolean("concluida");
                long tempoTrabalho = resultSet.getLong("tempo_trabalho");

                // Formatar os valores para CSV
                String tempoTrabalhoFormatado = formatarTempo(tempoTrabalho);
                String linhaCSV = String.format("%d,%s,%s,%b,%s\n", id, descricao, dateFormat.format(dataPrevisao), concluida, tempoTrabalhoFormatado);

                writer.write(linhaCSV);
            }

            System.out.println("Tarefas exportadas para o arquivo CSV com sucesso!");

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }


//=====================================================================================< Importar >==================================================================================//


    public void importarTarefasDeCSV(String nomeArquivo) {

        if (!nomeArquivo.endsWith(".csv")) {
            nomeArquivo += ".csv";
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(nomeArquivo))) {
            // Ignorar a primeira linha que contém o cabeçalho
            reader.readLine();

            String linha;
            while ((linha = reader.readLine()) != null) {
                // Dividir a linha em colunas separadas por vírgula
                String[] colunas = linha.split(",");
                // Extrair os valores das colunas
                int id = Integer.parseInt(colunas[0]);
                String descricao = colunas[1];
                Date dataPrevisao = dateFormat.parse(colunas[2]);
                boolean concluida = Boolean.parseBoolean(colunas[3]);
                LocalTime tempoTrabalho = LocalTime.parse(colunas[4], DateTimeFormatter.ofPattern("HH:mm:ss"));
                // Adicionar a tarefa importada ao banco de dados
                adicionarTarefasImportadas(id, descricao, dataPrevisao, concluida, tempoTrabalho);
            }

            System.out.println("Tarefas importadas do arquivo CSV com sucesso!");
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }


//================================================================================< Add Tarefa Import >===========================================================================//


   public void adicionarTarefasImportadas(int id, String descricao, Date dataPrevisao, boolean concluida, LocalTime tempoTrabalho) {
    try {
        String insertQuery = "INSERT INTO tarefas (id, descricao, data_previsao, concluida, tempo_trabalho) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
        preparedStatement.setInt(1, id);
        preparedStatement.setString(2, descricao);
        preparedStatement.setDate(3, new java.sql.Date(dataPrevisao.getTime()));
        preparedStatement.setBoolean(4, concluida);
        preparedStatement.setLong(5, tempoTrabalho.toNanoOfDay() / 1_000_000);  // Converter para milissegundos
        preparedStatement.executeUpdate();

        System.out.println("A tarefa " + id + " foi adicionada com sucesso!");
    } catch (SQLException e) {
        System.out.println("Erro ao adicionar a tarefa: " + e.getMessage());
    }
}

}


//=====================================================================================< Fim >==================================================================================//


// javac -cp "lib/mysql-connector-java-8.0.28.jar;src" src/*.java
// java -cp "lib/mysql-connector-java-8.0.28.jar;src" App

