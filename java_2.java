import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.BufferedWriter;
public class p4 {
 public static void main(String[] args) {
 if (args.length == 0) {
 System.out.println("Error: not enough params. Type 'help' for
details");
 return;
 }
 String[] keys = {"createcat", "numin", "atr", "search", "copycon",
"copycat", "help", "findfile"};
 ArrayList<String> comms = new ArrayList<>();
 ArrayList<String> args_otd = new ArrayList<>();
 String input = String.join(" ", args).trim();
 String[] words = input.split(" ");
 boolean isFirstCommand = true;
 String value = "";
 for (String word : words) {
 if (Arrays.asList(keys).contains(word)) {
 if (!isFirstCommand) {
 args_otd.add(value);
 value = "";
 }
 comms.add(word);
 isFirstCommand = false;
 } else {
 if (!value.isEmpty()) {
 value += " ";
 }
 value += word;
 }
 }
 args_otd.add(value);
 List<Thread> threads = new ArrayList<>();
 for (int i = 0; i < comms.size(); i++) {
 String command = comms.get(i);
 String[] commandArgs = args_otd.get(i).split(" ");
 Thread thread = new Thread(new Potok(command, commandArgs));
 threads.add(thread);
 thread.start();
 }
 for (Thread thread : threads) {
 try {
 thread.join();
 } catch (InterruptedException e) {
 e.printStackTrace();
 }
 }
 print_temp("temp.txt");
 }
 static class Potok implements Runnable {
 private final String command;
 private final String[] arguments;
 public Potok(String command, String[] arguments) {
 this.command = command;
 this.arguments = arguments;
 }
 @Override
 public void run() {
 System.out.println("Start stream : " + command);
 long startTime = System.currentTimeMillis();
 long startMemory = Runtime.getRuntime().totalMemory() -
Runtime.getRuntime().freeMemory();
 switch (command) {
 case "help":
 write_to_temp("\nYou can use following commands: help,
copycon {file.txt} {text}, createcat {name}, numin {catalog}, ");
 write_to_temp("atr {file.txt}, copycat {catalog} {place},
search {file.txt}, findfile \n");
 break;
 case "copycon":
 if (arguments.length < 2)
 {
 write_to_temp("Error: not enough set parameter for
command copycon {file.txt} {text}\n");
 } else
 {
 Path f = Paths.get(arguments[0]);
 if (Files.exists(f))
 {
 write_to_temp("Error: file exists");
 } else {
 // создание файла
 try {
 Files.createFile(f);
write_to_temp("File is created");
 } catch (IOException e) {
 write_to_temp("Error: cannot create file");
 e.printStackTrace();
 }
// добавление текста
 try (BufferedWriter writer = new
BufferedWriter(new FileWriter(arguments[0]))) {
 String text = arguments[1];
 for (int t = 3; t < arguments.length; t++) {
 text = text + " " + arguments[t-1];
 }
writer.write(text);
write_to_temp("Text successfully written");
 } catch (IOException e) {
 write_to_temp("Error: cannot write in file");
 e.printStackTrace();
 }
 }
}
 break;
 case "createcat":
 if (arguments.length > 1) {
 write_to_temp("Error: command createcat accepts only
one parameter {p}");
 } else if (arguments.length == 0) {
 write_to_temp("Error: not set parameter {p} for
command createcat");
 } else {
 Path d = Paths.get(arguments[0]);
 if (Files.exists(d)) {
 write_to_temp("Error: directory exists");
 } else {
 try {
 // создание каталога
 Files.createDirectories(d);
write_to_temp("Directory is created");
 } catch (IOException e) {
 write_to_temp("Error: cannot create
directory");
 e.printStackTrace();
 }
 }
 }
 break;
 case "numin":
 if (arguments.length > 1) {
 write_to_temp("Error: command numin accepts only one
parameter {p}");
 } else if (arguments.length == 0) {
 write_to_temp("Error: not set parameter {p} for
command numin");
 } else {
 File p = new File(arguments[0]);
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
write_to_temp("Number of inserted directories: "
+ num);
 } else {
 write_to_temp("Error: directory " + arguments[0]
+ " doesn't exist");
 }
 }
break;
 case "atr":
 if (arguments.length > 1) {
 write_to_temp("Error: command atr accepts only one
parameter {p}");
 } else if (arguments.length == 0) {
 write_to_temp("Error: not set parameter {p} for
command atr");
 } else {
 File p = new File(arguments[0]);
 if (!p.exists()) {
 write_to_temp("Error: directory doesn't exist");
 } else {
 Path path =
FileSystems.getDefault().getPath(arguments[0]);
 BasicFileAttributeView view =
Files.getFileAttributeView(path, BasicFileAttributeView.class);
 BasicFileAttributes atr = null;
try {
 atr = view.readAttributes();
 } catch (IOException e) {
 throw new RuntimeException(e);
 }
write_to_temp("Creation time of the file " +
atr.creationTime());
 write_to_temp("Size of file: " + atr.size() + "
byte");
 write_to_temp("Last modified time of the file: "
+ atr.lastModifiedTime());
 write_to_temp("Last accessed time if the file " +
atr.lastAccessTime());
 write_to_temp("Directory or not: " +
atr.isDirectory());
 }
 }
break;
 case "copycat":
 if (arguments.length > 2) {
 write_to_temp("Error: command copycat accepts only
two parameters");
 } else if (arguments.length < 2) {
 write_to_temp("Error: not enough set parameters {p1}
{p2} for command copycat");
 } else {
 File src = new File(arguments[0]);
 File dest = new File(arguments[1]);
 if (!src.isDirectory() || !dest.isDirectory()) {
 write_to_temp("Source and dest must be
directory");
 } else if (!src.exists() || !dest.exists()) {
 write_to_temp("Source or dest directory doesn't
exist");
 } else {
 copyDirectory(src, dest);
write_to_temp("Copied successfully");
 }
 }
break;
 case "search":
 if (arguments.length > 1) {
 write_to_temp("Error: command search accepts only one
parameter");
 } else if (arguments.length == 0) {
 write_to_temp("Error: not set parameter {p} for
command search");
 } else {
 List<File> foundFiles = new ArrayList<>();
String filen = arguments[0];
 File dir = new File(System.getProperty("user.dir"));
 findFile(dir, filen, foundFiles);
if (foundFiles.isEmpty()) {
 write_to_temp("File wasn`t found");
 } else {
 write_to_temp("Found file:");
 for (File file : foundFiles) {
 write_to_temp(file.getAbsolutePath());
 }
 }
 }
break;
 case "findfile":
 if (arguments.length == 0) {
 write_to_temp("Error: not set parameters {pattern} &
{file} & ...");
 return;
 }
 if (arguments.length == 1) {
 write_to_temp("Error: not set parameters {pattern} or
{file}");
 return;crea cope
 }
List<Thread> threads = new ArrayList<>();
String pattern = arguments[0];
 for (int i = 1; i < arguments.length; i++) {
 String fileName = arguments[i];
Thread thread = new Thread(new SearchRunner(pattern,
fileName));
 threads.add(thread);
 thread.start();
 }
for (Thread thread : threads) {
 try {
 thread.join();
 } catch (InterruptedException e) {
 e.printStackTrace();
 }
 }
break;
 default:
 write_to_temp("Error: unrecognized command. Type 'help'
for details.");
 break;
 }
 long endTime = System.currentTimeMillis();
 long endMemory = Runtime.getRuntime().totalMemory() -
Runtime.getRuntime().freeMemory();
 System.out.println("Stop stream : " + command + ". Time: " +
(endTime - startTime) +
 "ms. Memory: " + (endMemory - startMemory) + " bytes");
 }
 }
 public static void write_to_temp(String line) {
 try {
 BufferedWriter writer = new BufferedWriter(new
FileWriter("temp.txt", true));
 writer.write(line);
 writer.newLine();
 writer.close();
 } catch (IOException e) {
 e.printStackTrace();
 }
 }
 public static void print_temp(String file) {
 try {
 File tempf = new File(file);
 BufferedReader reader = new BufferedReader(new
FileReader(tempf));
 String line;
 System.out.println();
 while ((line = reader.readLine()) != null) {
 System.out.println(line);
 }
 reader.close();
 tempf.delete();
 } catch (IOException e) {
 e.printStackTrace();
 }
 }
 static class SearchRunner implements Runnable {
 private final String command = "findfile";
 private final String pattern;
 private final String fileName;
 public SearchRunner(String pattern, String fileName) {
 this.pattern = pattern;
 this.fileName = fileName;
 }
 @Override
 public void run() {
 System.out.println("\nStart stream " + command + " '" + pattern +
"' in " + " '" + fileName + "'");
 long startTime = System.currentTimeMillis();
 long startMemory = Runtime.getRuntime().totalMemory() -
Runtime.getRuntime().freeMemory();
 {
 {
 List<String> outputLines = new ArrayList<>();
File file = new File(fileName);
 if (!file.exists()) {
 write_to_temp(("Error: file '" + fileName + "' does
not exist."));
 return;
 }
try (BufferedReader reader = new BufferedReader(new
FileReader(file))) {
 String line;
 int lineNumber = 0;
 while ((line = reader.readLine()) != null) {
 lineNumber++;
if (line.toLowerCase().contains(pattern)) {
 outputLines.add("Found \"" + pattern + "\" in
\"" + fileName + "\" at line " + lineNumber + ": " + line);
 }
 }
 } catch (IOException e) {
 e.printStackTrace();
 }
synchronized (p4.class) {
 try (FileWriter writer = new FileWriter("temp.txt",
true)) {
 for (String fline : outputLines) {
 writer.write(fline + "\n");
 }
 } catch (IOException e) {
 e.printStackTrace();
 }
 }
 }
 }
 long endTime = System.currentTimeMillis();
 long endMemory = Runtime.getRuntime().totalMemory() -
Runtime.getRuntime().freeMemory();
 System.out.println("\tStop stream: " + command + ". Time: " +
(endTime - startTime) + "ms. Memory: " + (endMemory - startMemory) + "
bytes");
 }
 }
 private static void findFile(File dir, String NamePattern, List<File>
foundFiles) {
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
