package org.example;
import com.sun.jna.Memory;
import com.sun.jna.platform.win32.WinGDI;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.GDI32;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
public class p5
{
 public static void main(String[] args)
 {
 new Screenshot().start();
 }
 static class Screenshot extends TimerTask
 {
 private static Timer timer = new Timer();
 private static int k = 0;
 private static int max_k = 4;
 public void start()
 {
 timer.schedule(this, 0, 5000);
 }
 public void run()
 {
 k++;
 WinUser.HDC hScreen = User32.INSTANCE.GetDC(null);
 WinUser.HDC hMemoryDC =
GDI32.INSTANCE.CreateCompatibleDC(hScreen);
 int width =
User32.INSTANCE.GetSystemMetrics(WinUser.SM_CXSCREEN);
 int height =
User32.INSTANCE.GetSystemMetrics(WinUser.SM_CYSCREEN);
 WinDef.HBITMAP hBitmap =
GDI32.INSTANCE.CreateCompatibleBitmap(hScreen, width, height);
 GDI32.INSTANCE.SelectObject(hMemoryDC, hBitmap);
 GDI32.INSTANCE.BitBlt(hMemoryDC, 0, 0, width, height, hScreen, 0,
0, 0x00CC0020);
 BufferedImage img = bitmap_to_img(hBitmap, width, height);
 try
 {
 String filename = "screenshot_" + System.currentTimeMillis()
+ ".png";
 ImageIO.write(img, "png", new File(filename));
 System.out.printf(filename + " successfully taken!\n");
 }
 catch (IOException e)
 {
 e.printStackTrace();
 }
 finally
 {
 GDI32.INSTANCE.DeleteObject(hBitmap);
 GDI32.INSTANCE.DeleteDC(hMemoryDC);
 User32.INSTANCE.ReleaseDC(null, hScreen);
 }
 if (k >= max_k)
 {
 timer.cancel();
 System.out.printf("Screenshots successfully taken!");
 }
 }
 private BufferedImage bitmap_to_img(WinDef.HBITMAP hBitmap, int
width, int height)
 {
 Memory mem = new Memory(width * height * 4);
 WinGDI.BITMAPINFOHEADER features = new WinGDI.BITMAPINFOHEADER();
 features.biSize = features.size();
 features.biWidth = width;
 features.biHeight = -height;
 features.biPlanes = 1;
 features.biBitCount = 32;
 features.biCompression = WinGDI.BI_RGB;
 WinGDI.BITMAPINFO bmi = new WinGDI.BITMAPINFO();
 bmi.bmiHeader = features;
 GDI32.INSTANCE.GetDIBits(GDI32.INSTANCE.CreateCompatibleDC(null),
hBitmap, 0, height, mem, bmi, WinGDI.DIB_RGB_COLORS);
 int[] pixels = new int[width * height];
 mem.read(0, pixels, 0, pixels.length);
 BufferedImage img = new BufferedImage(width, height,
BufferedImage.TYPE_INT_ARGB);
 img.setRGB(0, 0, width, height, pixels, 0, width);
 return img;
 }
 }
}
