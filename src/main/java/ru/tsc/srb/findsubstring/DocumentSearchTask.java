package ru.tsc.srb.findsubstring;

import java.io.*;
import java.util.concurrent.RecursiveTask;

public class DocumentSearchTask extends RecursiveTask<String> {

    private File file;
    private String searchedSubstring;
    private BufferedWriter bufferedWriter;

    public DocumentSearchTask(File file, String searchedSubstring, BufferedWriter bufferedWriter) {
        this.file = file;
        this.searchedSubstring = searchedSubstring;
        this.bufferedWriter = bufferedWriter;
    }

    @Override
    protected String compute() {
        return foundSubstring();
    }

    private String foundSubstring() {
        String line;
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            return "Файл не найден";
        }
        while (true) {
            try {
                if (!((line = bufferedReader.readLine()) != null)) break;
            } catch (IOException e) {
                return "Файл не доступен для чтения";
            }
            if (line.contains(searchedSubstring)) {
                try {
                    synchronized (this) {
                        bufferedWriter.append(Thread.currentThread().getId() + " " + file.getAbsolutePath() + " : " + line + "\n");
                    }
                } catch (IOException e) {
                    System.out.println("Файл не доступен для записи");
                }
            }
        }
        try {
            bufferedReader.close();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }
}
