package org.example;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Structure;
import jnr.posix.POSIX;
import jnr.posix.POSIXFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
public class Main {
 public interface CLibrary extends Library
 {
 CLibrary INSTANCE = Native.load("c", CLibrary.class);
 int uname(OS_str OS_data);
 int getuid();
 InfoUser getpwuid(int uid);
 }
 public static class InfoUser extends Structure
 {
 public String name;
 public String passwd;
 public int uid;
 public int gid;
 public String gecos;
 public String dir;
 public String shell;
 @Override
 protected List<String> getFieldOrder()
 {
 return Arrays.asList("name", "passwd", "uid", "gid", "gecos",
"dir", "shell");
 }
 }
 public static class OS_str extends Structure
 {
 public byte[] OSname = new byte[65];
 public byte[] hostname = new byte[65];
 public byte[] vers = new byte[65];
 public byte[] arch = new byte[65];
 @Override
 protected List<String> getFieldOrder()
 {
 return Arrays.asList("OSname", "hostname", "vers", "arch");
 }
 }
 public static void main(String[] args)
 {
 // Имя машины
 System.out.printf("\nName of machine\n");
 POSIX posix = POSIXFactory.getPOSIX();
 System.out.println("Machine name: " + posix.gethostname());
 // Информация об ОС
 System.out.printf("\nInformation about OS\n");
 OS_str OSinfo = new OS_str();
 CLibrary.INSTANCE.uname(OSinfo);
 System.out.println("OS name: " + Native.toString(OSinfo.OSname));
 System.out.println("Hostname: " + Native.toString(OSinfo.hostname));
 System.out.println("Version: " + Native.toString(OSinfo.vers));
 System.out.println("Arch: " + Native.toString(OSinfo.arch));
 // Информация о дисках
 System.out.printf("\nInformation about disks\n");
 try
 {
 Process p = Runtime.getRuntime().exec("df -h");
 p.waitFor();
 InputStream is = p.getInputStream();
 BufferedReader reader = new BufferedReader(new
InputStreamReader(is));
 String line;
 while ((line = reader.readLine()) != null)
 {
 System.out.println(line);
 }
 }
 catch (IOException | InterruptedException e)
 {
 e.printStackTrace();
 };
 // Информация о пользователе
 System.out.printf("\nInformation about user\n");
 int uid = CLibrary.INSTANCE.getuid();
 InfoUser inf = CLibrary.INSTANCE.getpwuid(uid);
 System.out.println("Username:" + inf.name);
 System.out.println("Shell: " + inf.shell);
 System.out.println("Home directory: " + inf.dir);
 System.out.println("User ID: " + inf.uid);
 // Сетевые настройки
 System.out.printf("\nNetwork settings\n");
 try {
 Enumeration<NetworkInterface> interfaces =
NetworkInterface.getNetworkInterfaces();
 while (interfaces.hasMoreElements())
 {
 NetworkInterface networkInterface = interfaces.nextElement();
 System.out.println("Interface name: " +
networkInterface.getDisplayName());
 Enumeration<InetAddress> IP =
networkInterface.getInetAddresses();
 while (IP.hasMoreElements())
 {
 InetAddress IP_i = IP.nextElement();
System.out.println("IP: " + IP_i.getHostAddress());
 }
 }
 } catch (SocketException e) {
 e.printStackTrace();
 }
 }
}
