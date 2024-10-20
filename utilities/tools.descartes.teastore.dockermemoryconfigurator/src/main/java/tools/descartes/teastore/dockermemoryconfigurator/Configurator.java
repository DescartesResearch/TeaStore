package tools.descartes.teastore.dockermemoryconfigurator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Configures the Tomcat heap space inside a docker container. Implemented in
 * Java to compensate for potentially missing GNU/Linux tools inside the
 * container when running this using bash.
 * 
 * @author Joakim von Kistowski
 *
 */
public final class Configurator {

  private static final long DEFAULT_PERCENTAGE = 80;

  private Configurator() {

  }

  /**
   * Runs the configurator.
   * 
   * @param args
   *          Percentage of total memory to be used for heap as the only
   *          parameter.
   */
  public static void main(String[] args) {
    long percentage = readPercentage(args);
    long totalkb = readTotalMemoryInKB();
    long cgroupkb = readCGroupMemoryInKB();

    // System.out.println("Total Host Memory = " + totalkb + " KiB");
    // System.out.println("Container CGroup Limit = " + cgroupkb + " KiB");

    if (cgroupkb != 0 && totalkb != 0 && cgroupkb < totalkb) {
      long heapkb = (cgroupkb * percentage) / 100L;
      System.out.println("Setting heap space to " + heapkb + " KiB");
      writeSetEnvFile(heapkb);

    } else {
      System.out.println("Unable to set heap space, cgroupkb: " + cgroupkb + " totalkb: " + totalkb);
    }
  }

  private static long readPercentage(String[] args) {
    long percentage = DEFAULT_PERCENTAGE;
    if (args.length > 0) {
      String arg0 = args[0].trim();
      if (!arg0.isEmpty()) {
        try {
          percentage = Long.parseLong(arg0);
        } catch (NumberFormatException e) {
          percentage = DEFAULT_PERCENTAGE;
        }
      }
    }
    return percentage;
  }

  /**
   * Reads the total memory from /proc/meminfo.
   * 
   * @return 0 on error.
   */
  private static long readTotalMemoryInKB() {
    File meminfo = new File("/proc/meminfo");
    if (!meminfo.exists()) {
      return 0;
    }

    try (Scanner scan = new Scanner(meminfo)) {
      while (scan.hasNextLine()) {
        String line = scan.nextLine().trim();
        if (line.startsWith("MemTotal:")) {
          String[] tokens = line.split(" ");
          String kbs = tokens[tokens.length - 2].trim();
          try {
            return Long.parseLong(kbs);
          } catch (NumberFormatException e) {
            return 0;
          }
        }
      }
    } catch (IOException e) {
      return 0;
    }

    return 0;
  }

  /**
   * Reads the cgroup memory from /sys/fs/cgroup/memory/memory.limit_in_bytes and
   * converts to KiB.
   * 
   * @return 0 on error.
   */
  private static long readCGroupMemoryInKB() {
    File cgroupbytes1 = new File("/sys/fs/cgroup/memory/memory.limit_in_bytes");
    File cgroupbytes2 = new File("/sys/fs/cgroup/memory.max");
    File cgroupbytes = null;
    System.out.println("cgroup memory max file " + cgroupbytes1.getAbsolutePath() + " exists: " + cgroupbytes1.exists());
    if (cgroupbytes1.exists()) {
    	cgroupbytes = cgroupbytes1;
    }
    System.out.println("cgroup memory max file " + cgroupbytes2.getAbsolutePath() + " exists: " + cgroupbytes2.exists());
    if (cgroupbytes2.exists()) {
    	cgroupbytes = cgroupbytes2;
    }
    if (!cgroupbytes.exists()) {
      System.out.println("cgroup memory limit files not existing");
      return 0;
    }

    try (BufferedReader br = new BufferedReader(new FileReader(cgroupbytes))) {
      try {
        // use double, number may be too large
        double bytes = Double.parseDouble(br.readLine().trim());
        return (long) (bytes / 1024.0);
      } catch (NumberFormatException e) {
        return 0;
      }
    } catch (IOException e) {
      return 0;
    }
  }

  private static void writeSetEnvFile(long heapkb) {
    try {
      new File("/usr/local/tomcat/bin/setenv.sh").createNewFile();
      PrintWriter out = new PrintWriter("/usr/local/tomcat/bin/setenv.sh");
      out.println("export CATALINA_OPTS=\"$CATALINA_OPTS -Xmx" + heapkb + "k\"");
      out.close();
    } catch (IOException e) {
      throw new IllegalStateException("Could not create setenv.sh file");
    }
  }
}
