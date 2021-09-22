package oeg.rdflicense2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
public class Rdflicense2Application {

	public static void main(String[] args) {
                System.setProperty("org.apache.jena.LEVEL", "OFF");


		SpringApplication.run(Rdflicense2Application.class, args);
       //         TripleStore.clonegit();
       //         dailymethod();
                TripleStore.loadlicensesIndex(TripleStore.INDEXFILE);
                TripleStore.loadlicenses(TripleStore.DATAFOLDER);
//                TripleStore.startSPARQLServer();
	}
        
        /**
         * Refreshes the memory in data
         */
        @Scheduled(fixedDelay = 1000*60*60*24, initialDelay = 1000*60*60*24)
        public static void dailymethod()
        {
            TripleStore.pullgit();
        }
        

}
