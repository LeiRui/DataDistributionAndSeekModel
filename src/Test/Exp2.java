package Test;

import data.DiscreteUniformData;
import query.Query;

public class Exp2 {
    public static void main(String[] args) {
        DiscreteUniformData exa = new DiscreteUniformData();
        //exa.createTable();



        double[] r1 = new double[]{2};
        double[] r2 = new double[]{8};

        for(int i=0;i<r1.length;i++) {
            Query query = new Query(6, 5, r1[i], r2[i], true,true,new double[]{3,10,11,59,59,888}); //注意i从0开始
            exa.expExecutor(query); // 其实这里可以省去expExecutor中的simulate部分，暂时这样吧
        }
    }
}
