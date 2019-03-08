package trafficsimulator.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author z003ru0y
 * Main runnable class of the whole project
 */
@SpringBootApplication
@ComponentScan(
	{
		"trafficsimulator.server",
		"trafficsimulator.shared",
		"trafficsimulator.simulator",
		"trafficsimulator.parser",
		"trafficsimulator.generator"
	})
public class TrafficSimulator
{
  
  /**
   * @param args not important
   * @throws Exception never
   */
  public static void main(String[] args) throws Exception {
    SpringApplication.run(TrafficSimulator.class, args);
  }

}
