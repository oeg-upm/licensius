package vroddon.commons;

import java.util.ArrayList;
import java.util.List;

/**
 * Utilidades matemáticas varias
 * @author Victor
 */
public class MathUtils {
    
    /**
     * Norma 2 de una lista de doubles.
     * @param vd
     */
    public static double getNorma(List<Double> vd)
    {
        double n = 0;
        for(Double d :vd)
        {
            n += d*d; 
        }
        n = Math.sqrt(n);
        return n;
    }
    
    /**
     * Normaliza una lista de doubles
     * @param vd
     */
    public static List<Double> Normalize(List<Double> vd) {
        double n = getNorma(vd);
        List<Double> salida = new ArrayList();
        for(Double d :vd)
        {
            salida.add(d/n);
        }
        return salida;
    }

    /**
     * Productos escalar de dos listas de doubles
     * @param d1
     * @param d2
     */
    public static double scalProd(List<Double> d1, List<Double> d2) {
        if (d1.size()!=d2.size())
            return 0;
        double d = 0;
        for(int i=0;i<d1.size();i++)
        {
            d=d + (d1.get(i)*d2.get(i)); 
        }
        return d;
    }

    
    /**
     * Promedio de una lista de listas de doubles
     * @param lld
     */
    public static List<Double> getAvg(List<List<Double>> lld)
    {
        double nvectors = lld.size();
        int tam=lld.get(0).size();
        List<Double> ld = new ArrayList();
        for(int i=0;i<tam;i++)
        {
            ld.add(0.0);
        }
        
        for (List<Double> l : lld)
        {
            for(int i =0;i<tam;i++)
            {
                ld.set(i, (ld.get(i) + l.get(i)));
            }
        }

            for(int i =0;i<tam;i++)
            {
           //     if (ld.get(i)>1E-10)
                    ld.set(i, (ld.get(i)/nvectors));
            }

        return ld;
    }

    /**
     * Distancia euclídea entre dos listas de doubles
     * @param pesos
     * @param ld
     */
    public static double euclidDistance(List<Double> pesos, List<Double> ld) {
        int tam = pesos.size();
        double dist=0;
        for(int i=0;i<tam;i++)
        {
            double d1=pesos.get(i);
            double d2=ld.get(i);
            dist = dist + ((d1-d2)*(d1-d2));
        }
        dist=Math.sqrt(dist);
        return dist;
    }    
}
