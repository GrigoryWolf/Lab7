package common.core.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import common.core.Invoker;
import common.core.utility.Printer;
import common.core.model.Route;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Класс, который управляет записью и чтением json файлов
 * @author grigoryvolkov
 */
public class FileManager {
    private final Scanner scan;
    private final Gson gson;
    private final Type type = new TypeToken<LinkedHashMap<Integer, Route>>() {}.getType();
    private Printer printer;
    public FileManager(String path, Printer printer) throws IOException {
        this.printer = printer;
        File file = new File(path);
        if (file.createNewFile())
            printer.print("Файл создан");
        FileReader reader = new FileReader(file);
        scan = new Scanner(reader);
        gson = new GsonBuilder().create();
    }

}
