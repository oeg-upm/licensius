package oeg.rdflicense2;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import static oeg.rdflicense2.TripleStore.readIndex;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
public class Rdflicense2Application {

	public static void main(String[] args) {
		SpringApplication.run(Rdflicense2Application.class, args);
                dailymethod();
                readIndex();
                startup();
	}
        
        @Scheduled(fixedDelay = 1000*60*60*24, initialDelay = 1000*60*60*24)
        public static void dailymethod()
        {
            TripleStore.pullgit();
        }
        
        public static void startup()
        {
            TripleStore.startSPARQLServer();
        }

}
