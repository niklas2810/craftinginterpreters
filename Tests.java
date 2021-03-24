import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

public class Tests {

  public static void main(String[] args) {
    new Tests().execute();
  }

  private final File cwd = new File(".");
  private final File testDir = new File("tests");
  private final Map<String, String> testers = new HashMap<>();

  public void execute() {
    if(!testDir.exists() || !testDir.isDirectory()) {
      System.err.println("Can not read from test directory " + testDir.getAbsolutePath());
      System.exit(1);
    }
    registerJloxTester();

    System.out.println(String.format("Found %d performance test runners", testers.size()));
    runPerformanceTests();
  }

  private void registerJloxTester() {
    File location = new File("jlox.jar");

    if(!location.exists()) {
      location = new File(String.format("jlox%starget%sjlox.jar", File.separator, File.separator));
    }

    if(!location.exists()) {
      System.err.println("File jlox.jar is not present in current directory or in jlox/target/");
      System.exit(1);
    }

    System.out.println("Using jar at location " + location.getAbsolutePath());
    testers.put("Java", "java -jar \"" + location.getAbsolutePath() + "\"");
  }

  private void runPerformanceTests() {
      System.out.println("=".repeat(50));
      for(File f : testDir.listFiles((dir, name) -> name.endsWith(".lox"))) {
          String shortName=f.getName().substring(0, f.getName().indexOf(".lox"));
          String prompt = "-".repeat((43-shortName.length())/2) + " FILE: " + shortName + " ";
          System.out.println(prompt + "-".repeat(50-prompt.length()));

          testers.forEach((name, cmd) -> {
            long start = System.currentTimeMillis();
            try {
              Process p = Runtime.getRuntime().exec(cmd + " ./tests/" + f.getName(), new String[0], cwd);
              p.waitFor();
              if(p.exitValue() != 0) {
                System.err.println("ERROR: " + name + " returned " + p.exitValue() + " for " + shortName);
                System.err.println("Application Output:");
                try(BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                  String line;

                  while((line = reader.readLine()) != null)
                    System.out.println(line);
                } catch (IOException e) {
                  System.err.println("Could not read application standard output");
                }

                try(BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
                  String line;

                  while((line = reader.readLine()) != null)
                    System.out.println(line);
                } catch (IOException e) {
                  System.err.println("Could not read application error output");
                }
                System.exit(1);
              }
            } catch(Exception e) {
              System.out.print("[FAILED due to " + e.getClass().getSimpleName() + "] ");
            }
            long end = System.currentTimeMillis();

            System.out.println(name + ": " + (end-start) + "ms");
          });

          System.out.println("=".repeat(50));
      }
  }
}