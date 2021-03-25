import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Tests {

  public static void main(String[] args) { new Tests().execute(); }

  private static final int TEST_RUNS = 75;

  private final File cwd = new File(".");
  private final File testDir = new File("tests");
  private final Map<String, String> testers = new HashMap<>();

  public void execute() {
    if (!testDir.exists() || !testDir.isDirectory()) {
      System.err.println("Can not read from test directory " +
                         testDir.getAbsolutePath());
      System.exit(1);
    }
    registerJloxTester();

    System.out.println(
        String.format("Found %d performance test runners", testers.size()));
    testers.forEach((name, command) -> System.out.printf("Using runner %s with \"%s\"\n", name, command));
    runPerformanceTests();
  }

  private void registerJloxTester() {
    String path = "jlox.jar";
    File location = new File(path);

    if (!location.exists()) {
      path = String.format("jlox%starget%sjlox.jar", File.separator,
                           File.separator);
      location = new File(path);

      if (!location.exists()) {
        System.err.println(
            "File jlox.jar is not present in current directory or in jlox/target/");
        System.exit(1);
      }
    }

    testers.put("Java", "java -jar " + path);
  }

  private void runPerformanceTests() {
    System.out.println("=".repeat(50));
    for (File f : testDir.listFiles((dir, name) -> name.endsWith(".lox"))) {
      String shortName = f.getName().substring(0, f.getName().indexOf(".lox"));
      String prompt = "-".repeat((43 - shortName.length()) / 2) +
                      " FILE: " + shortName + " ";
      System.out.println(prompt + "-".repeat(50 - prompt.length()));

      testers.forEach((name, cmd) -> {
        long sum = 0;
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;

        for (int i = 0; i < TEST_RUNS; ++i) {
          long curr = runTestCase(f, cmd);
          sum += curr;
          if (curr < min)
            min = curr;
          if (curr > max)
            max = curr;
        }

        long average = sum / TEST_RUNS;

        System.out.printf("%s: %dms on average (%d-%dms in %d test runs)\n",
                          name, average, min, max, TEST_RUNS);
      });

      System.out.println("=".repeat(50));
    }
  }

  private long runTestCase(File f, String cmd) {
    long start = System.currentTimeMillis();
    try {
      Process p = Runtime.getRuntime().exec(cmd + " ./tests/" + f.getName(),
                                            new String[0], cwd);
      p.waitFor();
      if (p.exitValue() != 0) {
        System.err.println("ERROR: " + cmd + " returned " + p.exitValue() +
                           " for " + f.getName());
        System.err.println("Application Output:");
        try (BufferedReader reader = new BufferedReader(
                 new InputStreamReader(p.getInputStream()))) {
          String line;

          while ((line = reader.readLine()) != null)
            System.out.println(line);
        } catch (IOException e) {
          System.err.println("Could not read application standard output");
        }

        try (BufferedReader reader = new BufferedReader(
                 new InputStreamReader(p.getErrorStream()))) {
          String line;

          while ((line = reader.readLine()) != null)
            System.out.println(line);
        } catch (IOException e) {
          System.err.println("Could not read application error output");
        }
        System.exit(1);
      }
    } catch (Exception e) {
      System.out.print("[FAILED due to " + e.getClass().getSimpleName() + "] ");
    }
    long end = System.currentTimeMillis();

    return end - start;
  }
}