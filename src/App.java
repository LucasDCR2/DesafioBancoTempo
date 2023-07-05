// javac -cp "lib/mysql-connector-java-8.0.28.jar;src" src/*.java
// java -cp "lib/mysql-connector-java-8.0.28.jar;src" App

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;


public class App {
    public static void main(String[] args) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        try {
            // Estabelecer a conexão com o banco de dados
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/gerenciador", "root", "casaroot2882");

            // Instanciar o gerenciador de tarefas
            Gerenciador gerenciador = new Gerenciador(connection);
            gerenciador.criarTabelaTarefas();

            int opcao = 0;

            do {

                gerenciador.exibirMenu();
                opcao = scanner.nextInt();

                switch (opcao) {
                    case 0:
                        gerenciador.exibirTarefas();
                        break;
                    case 1:
                        gerenciador.exibirTarefas();
                        gerenciador.adicionarTarefa(scanner);
                        break;
                    case 2:
                        gerenciador.exibirTarefas();
                        gerenciador.removerTarefa(scanner);
                        break;
                    case 3:
                        gerenciador.exibirTarefas();
                        gerenciador.editarTarefa(scanner);
                        break;
                    case 4:
                        gerenciador.exibirTarefas();
                        gerenciador.alterarStatus(scanner);
                        break;
                    case 5:
                        gerenciador.pesquisarTarefasPorPalavraChave(scanner);
                        break;
                    case 6:
                        gerenciador.listarTarefasPorPeriodo(scanner);
                        break;
                    case 7:
                        gerenciador.limparTabelaTarefas();
                        gerenciador.exibirTarefas();
                        break;
                    case 8:
                        gerenciador.exibirTarefas();
                        gerenciador.registrarTempoTrabalho(scanner);
                        break;
                    case 9:
                        System.out.print("Digite o nome do arquivo para exportar as tarefas: ");
                        String arquivoExportacao = scanner.next();
                        gerenciador.exportarTarefasParaCSV(arquivoExportacao);
                        break;
                    case 10:
                        System.out.print("Digite o nome do arquivo para importar as tarefas: ");
                        gerenciador.importarTarefasDeCSV(scanner.next());
                        break;
                    case 11:
                        System.out.println("Encerrando o programa...");
                        break;
                    default:
                        System.out.println("Opcao invalida, tente novamente!");
                        break;
                }
            } while (opcao != 11);

            scanner.close();
            connection.close(); // Fechar a conexão com o banco de dados
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

// javac -cp "lib/mysql-connector-java-8.0.28.jar;src" src/*.java
// java -cp "lib/mysql-connector-java-8.0.28.jar;src" App