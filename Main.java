import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main
{
    public static void main (String[]arg) throws IOException
    {
        String command = Arrays.toString(arg);
        command = command.substring(1, command.length() - 1);
        String[] vvod = command.split(" ");
        for (int k = 0; k < vvod.length - 1; k++)
        {
            vvod[k] = vvod[k].substring(0, vvod[k].length() - 1);
        }
        String[] m_comm = {"createcat", "numin", "atr", "search", "copycon", "copycat", "help", "findfile"};
        ArrayList<Integer> num_c = new ArrayList<>();
        for (int i = 0; i < vvod.length; i++)
        {
            String vvod_prov = vvod[i];
            boolean found = Arrays.stream(m_comm).anyMatch(vvod_prov::equals);
            if (found)
            {
                num_c.add(i);
            }
        }
        ArrayList<String> comm = new ArrayList<>();
        for (int i = 0; i < num_c.size() - 1; i++)
        {
            int kk = num_c.get(i);
            StringBuilder sum = new StringBuilder(vvod[kk]);
            int j1 = num_c.get(i) + 1;
            int j2 = num_c.get(i + 1);
            for (int j = j1; j < j2; j++)
            {
                sum.append(" ").append(vvod[j]);
            }
            comm.add(sum.toString());
        }
        if(num_c.size() > 1)
        {
            int last_c = num_c.get(num_c.size() - 1);
            StringBuilder sum = new StringBuilder(vvod[last_c]);
            for (int i = last_c + 1; i < vvod.length; i++)
            {
                sum.append(" ").append(vvod[i]);
            }
            comm.add(sum.toString());
        }
        /*for(int i = 0; i < comm.size(); i++) {
            String el = comm.get(i);
            System.out.println(el);
            System.out.printf("\n");
        }*/
        if(num_c.size() > 1)
        {
            for (int i = 0; i < num_c.size(); i++)
            {
                String term = "java Main " + comm.get(i);
                try
                {
                    Process proc = Runtime.getRuntime().exec(term);
                    proc.waitFor();
                    BufferedReader vivod = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                    String strk;
                    while ((strk = vivod.readLine()) != null)
                    {
                        System.out.println(strk);
                    }
                }
                catch (IOException | InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
        if(num_c.size() == 1)
        {
            int last_c = num_c.get(num_c.size() - 1);
            StringBuilder sum = new StringBuilder(vvod[last_c]);
            for (int i = last_c + 1; i < vvod.length; i++)
            {
                sum.append(" ").append(vvod[i]);
            }
            comm.add(sum.toString());
        }
        if(num_c.size() == 0 && vvod.length > 0)
        {
            System.out.printf("Error: command " + vvod[0] + " doesn`t supported. Type 'help' for details\n");
        }
        else if(num_c.size() == 0)
        {
            System.out.printf("Error: not enough params. Type 'help' for details\n");
        }
        if(num_c.size() == 1) {
            for (int i = 0; i < comm.size(); i++) {
                String[] args = (comm.get(i)).split(" ");
                if (command.isEmpty()) {
                    System.out.printf("Error: not enough params. Type 'help' for details\n");

                    /// help - вывод возможных команд
                } else if (args[0].equals("help")) {
                    System.out.printf("%s\n", "You can use following commands: help, copycon {file.txt} {text}, createcat {name}, numin {catalog}, ");
                    System.out.printf("atr {file.txt}, copycat {catalog} {place}, search {file.txt}, findfile \n");

                    /// foundfile - поиск файлов по шаблону
                } else if (args[0].equals("findfile")) {
                    if (args.length < 3)
                    {
                        System.out.println("Error: not enough set parameter for command findfile\n");
                    }
                    else {
                        String pattern = args[1];
                        List<String> for_file = new ArrayList<>();
                        for (int j = 2; j < args.length; j++)
                        {
                            File file = new File(args[j]);
                            if (!file.exists())
                            {
                                System.out.println("Error: file '" + args[j] + "' doesn't exist.");
                                continue;
                            }
                            String terminal = "grep \"" + pattern + "\" " + args[j];
                            try
                            {
                                Process proc = Runtime.getRuntime().exec(terminal);
                                proc.waitFor();
                                BufferedReader vivod = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                                String strk;
                                int num_s = 0;
                                while ((strk = vivod.readLine()) != null)
                                {
                                    num_s++;
                                    for_file.add("--- '" + pattern + "' in " + args[j] + ", line " + num_s + ": " + strk);
                                }
                                try(FileWriter write_to_file = new FileWriter("temp.txt"))
                                {
                                    for(int f = 0; f < for_file.size();f++)
                                    {
                                        write_to_file.write(for_file.get(f) + "\n");
                                    }
                                }
                                catch (IOException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                            catch (IOException | InterruptedException e)
                            {
                                e.printStackTrace();
                            }
                        }
                        try
                        {
                            Process term_cat = new ProcessBuilder("cat", "temp.txt").start();
                            BufferedReader cat_res = new BufferedReader(new InputStreamReader(term_cat.getInputStream()));
                            String cat_v;
                            while ((cat_v = cat_res.readLine()) != null)
                            {
                                System.out.println(cat_v);
                            }
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }

                    /// copycon - создание текстового файла с указанным именем  и ввод в него текста с клавиатуры
                else if (args[0].equals("copycon"))
                {
                    if (args.length < 3)
                    {
                        System.out.println("Error: not enough set parameter for command copycon {file.txt} {text}\n");
                    } else
                    {
                        Path f = Paths.get(args[1]);
                        if (Files.exists(f))
                        {
                            System.out.println("Error: file exists");
                        } else
                        {
                            // создание файла
                            try {
                                Files.createFile(f);
                                System.out.println("File is created");
                            } catch (IOException e) {
                                System.out.println("Error: cannot create file");
                                e.printStackTrace();
                            }
                            // добавление текста
                            try (BufferedWriter writer = new BufferedWriter(new FileWriter(args[1]))) {
                                String text = args[2];
                                for (int t = 3; t < args.length; t++) {
                                    text = text + " " + args[t];
                                }
                                writer.write(text);
                                System.out.println("Text successfully written");
                            } catch (IOException e) {
                                System.out.println("Error: cannot write in file");
                                e.printStackTrace();
                            }
                        }
                    }

                    /// createcat - создание каталога с указанным именем
                } else if (args[0].equals("createcat")) {
                    if (args.length > 2) {
                        System.out.println("Error: command createcat accepts only one parameter {p}");
                    } else if (args.length == 1) {
                        System.out.println("Error: not set parameter {p} for command createcat");
                    } else {
                        Path d = Paths.get(args[1]);
                        if (Files.exists(d)) {
                            System.out.println("Error: directory exists");
                        } else {
                            try {
                                // создание каталога
                                Files.createDirectories(d);
                                System.out.println("Directory is created");
                            } catch (IOException e) {
                                System.out.println("Error: cannot create directory");
                                e.printStackTrace();
                            }
                        }
                    }

                    /// numin - определение количества вложенных каталогов в указанный
                } else if (args[0].equals("numin")) {
                    if (args.length > 2) {
                        System.out.println("Error: command numin accepts only one parameter {p}");
                    } else if (args.length == 1) {
                        System.out.println("Error: not set parameter {p} for command numin");
                    } else {
                        File p = new File(args[1]);
                        if (p.exists()) {
                            int num = 0;
                            File[] findir = p.listFiles();
                            if (findir != null) {
                                for (File file : findir) {
                                    if (file.isDirectory()) {
                                        num++;
                                    }
                                }
                            }
                            System.out.println("Number of inserted directories: " + num);
                        } else {
                            System.out.println("Error: directory " + args[1] + " doesn't exist");
                        }
                    }

                    /// atr - определение атрибутов указанного файла
                } else if (args[0].equals("atr")) {
                    if (args.length > 2) {
                        System.out.println("Error: command atr accepts only one parameter {p}");
                    } else if (args.length == 1) {
                        System.out.println("Error: not set parameter {p} for command atr");
                    } else {
                        File p = new File(args[1]);
                        if (!p.exists()) {
                            System.out.println("Error: directory doesn't exist");
                        } else {
                            Path path = FileSystems.getDefault().getPath(args[1]);
                            BasicFileAttributeView view = Files.getFileAttributeView(path, BasicFileAttributeView.class);
                            BasicFileAttributes atr = view.readAttributes();
                            System.out.println("Creation time of the file " + atr.creationTime());
                            System.out.println("Size of file: " + atr.size() + " byte");
                            System.out.println("Last modified time of the file: " + atr.lastModifiedTime());
                            System.out.println("Last accessed time if the file " + atr.lastAccessTime());
                            System.out.println("Directory or not: " + atr.isDirectory());
                        }
                    }

                    // copycat - копирование указанного каталога в заданное месторасположение
                } else if (args[0].equals("copycat")) {
                    if (args.length > 3) {
                        System.out.println("Error: command copycat accepts only two parameters");
                    } else if (args.length < 3) {
                        System.out.println("Error: not enough set parameters {p1} {p2} for command copycat");
                    } else {
                        File src = new File(args[1]);
                        File dest = new File(args[2]);
                        if (!src.isDirectory() || !dest.isDirectory()) {
                            System.out.println("Source and dest must be directory");
                        } else if (!src.exists() || !dest.exists()) {
                            System.out.println("Source or dest directory doesn't exist");
                        } else {
                            copyDirectory(src, dest);
                            System.out.println("Copied successfully");
                        }
                    }

                    /// search - поиск месторасположения файла с указанным именем
                } else if (args[0].equals("search")) {
                    if (args.length > 2) {
                        System.out.println("Error: command search accepts only one parameter");
                    } else if (args.length == 1) {
                        System.out.println("Error: not set parameter {p} for command search");
                    } else {
                        List<File> foundFiles = new ArrayList<>();
                        String filen = args[1];
                        File dir = new File(System.getProperty("user.dir"));
                        findFile(dir, filen, foundFiles);
                        if (foundFiles.isEmpty()) {
                            System.out.println("File wasn`t found");
                        } else {
                            System.out.println("Found file:");
                            for (File file : foundFiles) {
                                System.out.println(file.getAbsolutePath());
                            }
                        }
                    }
                } else {
                    System.out.printf("Error: command '" + command + "' doesn`t supported. Type 'help' for details\n");
                }
            }
        }
    }
    private static void findFile(File dir, String NamePattern, List<File> foundFiles) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().matches(NamePattern.replace("*", ".*")
                        .replace("?", ".?"))) {
                    foundFiles.add(file);
                } else {
                    findFile(file, NamePattern, foundFiles);
                }
            }
        }
    }

    public static void copyDirectory(File src, File dest)  
    {
        String[] files = src.list();
        for (String file : files) {
            File sourceFile = new File(src, file);
            File destFile = new File(dest, file);
            copyDirectory(sourceFile, destFile);
        }
    }
}

