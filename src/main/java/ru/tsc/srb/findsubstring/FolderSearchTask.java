package ru.tsc.srb.findsubstring;

import java.io.BufferedWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class FolderSearchTask extends RecursiveTask<String> {

    private File path;
    private String searchedSubstring;
    private List<String> fileExtensions;
    private BufferedWriter bufferedWriter;

    public FolderSearchTask(File path, String searchedSubstring, List<String> fileExtensions, BufferedWriter bufferedWriter) {
        this.path = path;
        this.searchedSubstring = searchedSubstring;
        this.fileExtensions = fileExtensions;
        this.bufferedWriter = bufferedWriter;
    }

    private String findAndProcessedFolderAndFile(File path) {
        String nameFile;
        List<RecursiveTask<String>> forks = new ArrayList<>();
        if (path.canRead()) {
            for (File entry : path.listFiles()) {
                if (entry.isFile()) {
                    if (fileExtensions.contains(entry.getName().substring(entry.getName().lastIndexOf(".") + 1))) {
                        DocumentSearchTask task = new DocumentSearchTask(entry, searchedSubstring, bufferedWriter);
                        forks.add(task);
                        task.fork();
                    }
                } else if (entry.listFiles() != null) {
                    FolderSearchTask folderSearchTask = new FolderSearchTask(entry, searchedSubstring, fileExtensions, bufferedWriter);
                    folderSearchTask.compute();
                }
            }
            for (RecursiveTask<String> task : forks) {
                nameFile = task.join();
                System.out.println("Обработан файл: " + nameFile);
            }
            return "Все файлы были обработаны";
        } else {
            return "Корневая папка не доступна для чтения";
        }
    }

    @Override
    protected String compute() {
        return findAndProcessedFolderAndFile(path);
    }
}
