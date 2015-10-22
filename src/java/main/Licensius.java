/*
Copyright (c) 2013, OEG
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the <ORGANIZATION> nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package main;


import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.varia.NullAppender;


/**
 *
 * @author Victor
 */
public class Licensius {

    /**
     * Inicializa el logger
     */
    public static void initLogger() {
        PatternLayout layout = new PatternLayout("%d{ABSOLUTE} %5p %C{1}:%L - %m%n");
        try {
            FileAppender appender = new FileAppender(layout, "logs.txt", false); //appender de archivo de logs
            ConsoleAppender cappender = new ConsoleAppender(layout);
            Logger.getRootLogger().addAppender(appender);
            Logger.getRootLogger().addAppender(cappender);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Logger.getRootLogger().setLevel((Level) Level.DEBUG);
        Logger.getRootLogger().info("Ejecutado: " + new Date().toString() + " - Iniciando logger");
        Logger.getRootLogger().info("=========================================================");
        Logger.getLogger("com.hp.hpl.jena.rdf.model.impl.RDFDefaultErrorHandler").setLevel(Level.OFF);
        Logger.getLogger("com.hp.hpl.jena").setLevel(Level.OFF);
    }    
    
    
}
