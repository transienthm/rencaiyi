package hr.wozai.service.thirdparty.client.utils;

import hr.wozai.service.thirdparty.client.enums.EmailTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by wangbin on 16/6/28.
 */
@Component("similarytyUtil")
public class SimilarityUtil {

    private static int min(int one, int two, int three) {
        int min = one;
        if(two < min) {
            min = two;
        }
        if(three < min) {
            min = three;
        }
        return min;
    }


    public static int ld(String str1, String str2) {
        int d[][];    //矩阵
        int n = str1.length();
        int m = str2.length();
        int i;    //遍历str1的
        int j;    //遍历str2的
        char ch1;    //str1的
        char ch2;    //str2的
        int temp;    //记录相同字符,在某个矩阵位置值的增量,不是0就是1
        if(n == 0) {
            return m;
        }
        if(m == 0) {
            return n;
        }
        d = new int[n+1][m+1];
        for(i=0; i<=n; i++) {    //初始化第一列
            d[i][0] = i;
        }
        for(j=0; j<=m; j++) {    //初始化第一行
            d[0][j] = j;
        }
        for(i=1; i<=n; i++) {    //遍历str1
            ch1 = str1.charAt(i-1);
            //去匹配str2
            for(j=1; j<=m; j++) {
                ch2 = str2.charAt(j-1);
                if(ch1 == ch2) {
                    temp = 0;
                } else {
                    temp = 1;
                }
                //左边+1,上边+1, 左上角+temp取最小
                d[i][j] = min(d[i-1][j]+1, d[i][j-1]+1, d[i-1][j-1]+temp);
            }
        }
        return d[n][m];
    }

    public static double sim(String str1, String str2) {
        int ld = ld(str1, str2);
        return 1 - (double) ld / Math.max(str1.length(), str2.length());
    }

    public void countDiff(Set<String> param) {
        List<String> paramList = new ArrayList();
        paramList.addAll(param);
        for (int i = 0; i < paramList.size(); i++) {
            String a = paramList.get(i);
            for (int j = i+1; j < paramList.size(); j++) {
                String b = paramList.get(j);
                System.out.println(a + "与" + b + "的相似度为:" + sim(a, b));
            }

        }
    }

    public static String longestCommonSubstring(String first, String second) {
        String tmp = "";
        String max = "";
        for (int i=0; i < first.length(); i++){
            for (int j = 0; j < second.length(); j++){
                for (int k = 1; (k+i) <= first.length() && (k+j) <= second.length(); k++){
                    if (first.substring(i, k + i).equals(second.substring(j, k + j))){
                        tmp = first.substring(i, k + i);
                    }
                    else{
                        if (tmp.length() > max.length())
                            max = tmp;
                        tmp = "";
                    }
                }
                if (tmp.length() > max.length())
                    max = tmp;
                tmp = "";
            }
        }
        return max;
    }

    public static void main(String[] args) {



        String str1 = "orgName";
        String str2 = "orgShortName";

        SimilarityUtil similarityUtil = new SimilarityUtil();
        String longestCommonSubStr = longestCommonSubstring(str1, str2);
        //str1 = str1.replace(longestCommonSubStr, "");
        //str2 = str2.replace(longestCommonSubStr, "");
        System.out.println("longestCommonSubStr=" + longestCommonSubStr + " str1:" + str1.replace("org","") + " str2:" + str2);
        handleCommonSubstring(str1, str2);
        System.out.println("ld="+ld(str1.toLowerCase(), str2.toLowerCase()));
        System.out.println("sim="+sim(str1.toLowerCase(), str2.toLowerCase()));
    }

    private static boolean handleCommonSubstring(String first, String second) {
        if (sim(first, second) == 0) {
            return false;
        }
        String str1 = first;
        String str2 = second;
        while (isExistCommonSubstr(str1, str2)) {
            String commonSubStr = longestCommonSubstring(str1, str2);
            str1 = str1.replace(commonSubStr, "");
            str2 = str2.replace(commonSubStr, "");
            System.out.println("str1:" + str1 + " str2:" + str2);
        }

        if (str1 == "" || str2 == "") {
            return true;
        }
        return false;
    }

    private static boolean isExistCommonSubstr(String first, String second) {
        if (longestCommonSubstring(first, second) != "") {
            return true;
        }
        return false;
    }
}
