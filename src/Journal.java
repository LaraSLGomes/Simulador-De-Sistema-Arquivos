import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class Journal {
    private final Path caminhoJournal;

    public Journal(Path caminhoJournal) {
        this.caminhoJournal = caminhoJournal;
        try {
            if (!Files.exists(caminhoJournal)) {
                Files.createFile(caminhoJournal);
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao criar o arquivo de Journaling", e);
        }
    }

    public synchronized void registrarOperacao(String entrada) {
        try {
            String linha = entrada + System.lineSeparator();
            Files.write(caminhoJournal, linha.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException("Falha ao escrever no Journal", e);
        }
    }

    public List<String> lerHistorico() throws IOException {
        return Files.readAllLines(caminhoJournal);
    }
}