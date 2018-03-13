package Test;

import data.DiscreteUniformData;
import query.Query;

public class Exp1 {
    public static void main(String[] args) {
        DiscreteUniformData exa = new DiscreteUniformData();
        //exa.createTable();


        //int [] qi = new int[]{0};
        double [] r1 = new double[]{1,1,1,1, 2,2,2, 3,3, 4};
        //boolean [] l = new boolean[]{true};
        double [] r2 = new double[]{1,2,3,4, 2,3,4 ,3,4,  4};
        //boolean [] r = new boolean[]{true};
        //double[][] ckpoints = new double[][]{{888,2,3}};

        /*
        for(int i=0;i<r1.length;i++) {
            Query query = new Query(3, 0, r1[i], r2[i], true,true,new double[]{888,2,3}); //注意i从0开始
            exa.expExecutor(query);
        }
        */

        //int [] qi = new int[]{0};
        r1 = new double[]{1,1,1,1, 2,2,2, 3,3, 4};
        //boolean [] l = new boolean[]{true};
        r2 = new double[]{1,2,3,4, 2,3,4 ,3,4,  4};
        //boolean [] r = new boolean[]{true};
        //double[][] ckpoints = new double[][]{{888,2,3}};

        for(int i=0;i<r1.length;i++) {
            Query query = new Query(3, 1, r1[i], r2[i], true,true,new double[]{2,888,3}); //注意i从0开始
            exa.expExecutor(query);
        }

        //int [] qi = new int[]{0};
        r1 = new double[]{1,1,1,1, 2,2,2, 3,3, 4};
        //boolean [] l = new boolean[]{true};
        r2 = new double[]{1,2,3,4, 2,3,4 ,3,4,  4};
        //boolean [] r = new boolean[]{true};
        //double[][] ckpoints = new double[][]{{888,2,3}};

        for(int i=0;i<r1.length;i++) {
            Query query = new Query(3, 2, r1[i], r2[i], true,true,new double[]{2,3,888}); //注意i从0开始
            exa.expExecutor(query);
        }

    }
}
