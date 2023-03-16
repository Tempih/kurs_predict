package ru.liga.Utils;

import ru.liga.Structure.kurs_table.Kurs;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
public class FileProcces {

    public ArrayList<Kurs> getDataFromFile(String  path_to_file) {
        try {
            // CSV file delimiter
            String DELIMITER = ";";

            // create a reader
            BufferedReader br = null;
            try {
                br = Files.newBufferedReader(Paths.get(path_to_file));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // read the file line by line
            ArrayList<Kurs> tokens=new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                // convert line into tokens
                tokens.add(new Kurs(line.split(DELIMITER)));
                // print all tokens
//                for (String token : tokens) {
//                    System.out.println(token);
//                }
            }
            br.close();

            return tokens;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
