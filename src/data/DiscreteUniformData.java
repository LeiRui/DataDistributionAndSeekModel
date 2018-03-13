package data;

import com.sun.rowset.internal.Row;
import distribution.DiscreteUniform;
import query.Query;
import sun.security.krb5.internal.crypto.CksumType;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class DiscreteUniformData extends CKData<DiscreteUniform>{
    //private static Random random = new Random();
    //public static String path = "DiscreteUniformTable"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+".csv";
    public String queryPath;
    public PrintWriter pw;
    public DiscreteUniformData() {
        /*
        CKn=3;
        CKdist = new ArrayList();
        for(int i=0; i< CKn; i++) {
            DiscreteUniform ck = new DiscreteUniform(1,1,4);//1,2,3,....,100均匀分布
            CKdist.add(ck);
        }
        rows = 64;

        queryPath = "DiscreteUniformTable"+"_Exp1_CKn"+CKn+"_rows"+rows+".csv";
        pw = null;
        */
        CKn=6;
        CKdist = new ArrayList();
        DiscreteUniform year = new DiscreteUniform(1,1,2018);
        DiscreteUniform month = new DiscreteUniform(1,1,12);
        DiscreteUniform day = new DiscreteUniform(1,1,30);
        DiscreteUniform hour = new DiscreteUniform(1,1,60);
        DiscreteUniform min = new DiscreteUniform(1,1,60);
        DiscreteUniform sec = new DiscreteUniform(1,1,60);
        CKdist.add(month);
        CKdist.add(day);
        CKdist.add(hour);
        CKdist.add(min);
        CKdist.add(sec);
        CKdist.add(year);

        rows = 10000;
        queryPath = "exp2/DiscreteUniformTable"+"_Exp2_CKn"+CKn+"_rows"+rows+".csv";
        pw = null;
    }

    /*
      根据离散均匀分布的参数，生成数据表格，写入CSV
      注意第一遍生成之后去重
     */
    public void createTable(String path1,String path2){
        // 按照各列的分布函数产生数据
        double[][] table = new double[rows][];
        Set<RowData> rowTable = new HashSet<>(); // no duplicates
        List<RowData> rowTableD = new ArrayList<>();// with duplicates
        for(int i=0; i<rows;i++) {
            table[i] = new double[CKn];
            for(int j=0; j<CKn; j++) {
                //按照每一列的分布函数随机产生值
                table[i][j] = CKdist.get(j).getValue();
            }
            RowData rowdata =new RowData();
            rowdata.CKn = this.CKn;
            rowdata.data=table[i];
            rowTable.add(rowdata);
            rowTableD.add(rowdata);
        }

        //按照CKSet主次排序，以行为单位重新排列数据
        List sortedTable = new ArrayList(rowTable);
        Collections.sort(sortedTable);

        Collections.sort(rowTableD);

        //把排序后的数据写入表格
        PrintWriter pw1 = null;
        try {
            pw1 = new PrintWriter(new File(path1));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Iterator iter = sortedTable.iterator();
        while(iter.hasNext()) {
            RowData rowdata = (RowData)iter.next();
            StringBuilder builder = new StringBuilder();
            for(int i=0;i<rowdata.CKn-1; i++){
                builder.append(rowdata.data[i]+",");
            }
            builder.append(rowdata.data[rowdata.CKn-1]);
            builder.append('\n');
            pw1.write(builder.toString());
        }
        pw1.close();
        //System.out.println("done!");

        //把排序后的数据写入表格
        PrintWriter pw2 = null;
        try {
            pw2 = new PrintWriter(new File(path2));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Iterator iter2 = rowTableD.iterator();
        while(iter2.hasNext()) {
            RowData rowdata = (RowData)iter2.next();
            StringBuilder builder = new StringBuilder();
            for(int i=0;i<rowdata.CKn-1; i++){
                builder.append(rowdata.data[i]+",");
            }
            builder.append(rowdata.data[rowdata.CKn-1]);
            builder.append('\n');
            pw2.write(builder.toString());
        }
        pw2.close();
    }
   /*
      输入一个查询
      输出在"DiscreteUniformTable.csv"中实际的seek长度
     */
    public int QuerySimulate(Query query, String path){
        int res=0;
        int linecount = 0;
        int pre=0;
        int cur=1;
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        try {

            br = new BufferedReader(new FileReader(path));
            while ((line = br.readLine()) != null) {
                linecount++;
                String[] data =line.split(cvsSplitBy);
                double[] row = new double[CKn];
                for(int i=0;i<data.length;i++) {
                    row[i]=Double.parseDouble(data[i]);
                }
                RowData rowdata = new RowData();
                rowdata.CKn=CKn;
                rowdata.data=row;
                if(query.fit(rowdata)){
                    cur=linecount;
                    res+=cur-(pre+1); // +1是因为读取的行不计在内
                    //System.out.println("res="+res);
                    //System.out.print(rowdata.data[0]+","+rowdata.data[1]+","+rowdata.data[2]);
                    //System.out.println("");
                    pre=cur;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return res;
    }

    /*
     输入一个查询
     输出按照公式计算的seek长度
     注意使用这个函数需要先createTable
     */
    public double QueryFormula(Query query){
        double res = 0;

        //从query中提取目标点，然后分段利用getBefore和getBetween计算seek的无关点数
        int qi = query.qi;
        double r1=query.r1;
        double r2=query.r2;

        double []p1 = query.CKPoints;
        p1[qi] = r1;

        //System.out.println(getBefore(new RowData(query.CKn,p1))*rows);

        // 下面找在query的range查询范围内的相邻离散点
        RowData previous = null;
        RowData current = null;
        DiscreteUniform discreteUniform = CKdist.get(qi);
        double pos = discreteUniform.start;
        for(int i=0; i<discreteUniform.number; i++, pos+=discreteUniform.delta) {
            double lc = pos-r1;
            double rc = pos-r2;
            if(query.lisclosed) {
                if(lc < 0)
                    continue;
            }
            else {
                if (Math.abs(lc - 0.0) <= 0.000001)//TODO 这里不确定两个double相等可以不可以
                    continue;
                if (lc < 0)
                    continue;
            }

            if(query.risclosed) {
                if(rc > 0)
                    continue;
            }
            else {
                if (Math.abs(rc - 0.0) <= 0.000001)//TODO 这里不确定两个double相等可以不可以
                    continue;
                if (rc > 0)
                    continue;
            }

            //剩下的就是满足range的了
            double []p = query.CKPoints;
            p[qi] = pos;
            if(previous == null) {
                previous = new RowData(CKn,p);
                res+=getBefore(previous);
                //System.out.println("res = "+res*rows);
            }
            else {
                current = new RowData(CKn,p);
                res += getBetween(previous,current,qi);
                //System.out.println("res = "+res*rows);
            }
        }

        res *= rows;
        return res;
    }



    /**
     * 输入一个查询
     * @return 到cassandra中查询的耗时
     */
    public double QueryCassandra() { //TODO
        return 0;
    }

    //一次查询写一行结果，分别是query参数、simulate总结果、formulate分步骤结果
    //注意，formulate的rows就是用的此类的属性，simulate按照rows生成数据之后有去重再查找,所以simulate的数值会比没有考虑去重的formulate结果要小一些

    public void expExecutor(Query query){
        try {
            pw = new PrintWriter(new FileOutputStream(queryPath,true)); // append
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        StringBuilder builder = new StringBuilder();
        //int CKn, int i, int r1, int r2, boolean l, boolean r, double[] ckpoints
        for(int i = 0 ; i< query.CKn; i++){
            if(i!=query.qi) {
                builder.append(""+query.CKPoints[i]);
            }
            else {
                if(query.lisclosed)
                    builder.append("[");
                else
                    builder.append("(");
                builder.append(query.r1+"-"+query.r2);
                if(query.risclosed)
                    builder.append("]");
                else
                    builder.append(")");
            }
            if(i==query.CKn-1)
                builder.append(",");
            else
                builder.append("&");
        }
        pw.write(builder.toString());

        int res=0;
        int resDup=0;
        Random random = new Random();
        int loopnum=10;
        for(int loop = 0; loop<loopnum; loop++) {
            String path1 = "exp2/DiscreteUniformTable"+"_Exp2_CKn"+CKn+"_rows"+rows+"_"
                    + builder.toString()
                    + new SimpleDateFormat("_yyyyMMddHHmmss_").format(new Date())
                    //+ random.nextInt(10000)
                    + ".csv";
            String path2 = "exp2/DiscreteUniformTableDuplicated"+"_Exp2_CKn"+CKn+"_rows"+rows+"_"
                    + builder.toString()
                    + new SimpleDateFormat("_yyyyMMddHHmmss_").format(new Date())
                    //+ random.nextInt(10000)
                    + ".csv";
            createTable(path1,path2);
            res += QuerySimulate(query, path1);
            resDup += QuerySimulate(query, path2);
        }
        pw.write(""+res/(double)loopnum+","+resDup/(double)loopnum+",,");

        double resFormulate=QueryFormula(query);
        pw.write('\n');
        pw.close();
        System.out.println(res/(double)loopnum+"  "+resDup/(double)loopnum+"   "+resFormulate);
    }

    public double getBefore(RowData point){
        int CKn=point.CKn;
        double []data = point.data;
        double res=0;
        double pre=1;
        for(int i=0;i< CKn; i++) {
            double r = CKdist.get(i).getLeftRangeExclude(data[i]);
            if(i>0){
                pre *= CKdist.get(i-1).getPoint(data[i-1]);
            }
            double x = pre*r;
            res = res + x;
            //System.out.println("res1="+x*rows);
            //StringBuilder builder = new StringBuilder();
            //builder.append(x*rows)
            pw.write(""+x*rows+",");
        }
        return res;
    }

    public double getBetween(RowData p1, RowData p2, int qi){
        int CKn = p1.CKn;
        double r1 = p1.data[qi];
        double r2 = p2.data[qi]; // 要求r1和r2在该列是离散相邻分布的，并且p1在前

        double res = 0;
        double h=1;
        for(int i=0;i<qi; i++) { // note:这里的qi已经是从0开始的，所以不要用qi-1
            h *= CKdist.get(i).getPoint(p1.data[i]);
        }

        double h1=h*CKdist.get(qi).getPoint(p1.data[qi]);
        for(int i=qi+1; i<CKn; i++) {
            double x =  h1*CKdist.get(i).getRightRangeExclude(p1.data[i]);
            res += x;
            h1 *= CKdist.get(i).getPoint(p1.data[i]);
            //System.out.println("res2="+x*rows);
            pw.write(""+x*rows+",");
        }

        double h2=h*CKdist.get(qi).getPoint(p2.data[qi]);
        for(int i=qi+1; i<CKn; i++) {
            double x = h2*CKdist.get(i).getLeftRangeExclude(p1.data[i]);
            res += x;
            h2*= CKdist.get(i).getPoint(p1.data[i]);
            //System.out.println("res2="+x*rows);
            pw.write(""+x*rows+",");
        }

        return res;
    }

}
