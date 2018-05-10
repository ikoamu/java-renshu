package Q18;

/**
 * Java言語 プログラミングレッスン 下
 * p257 問題18-2
 * 
 * ファイル中に特定の文字列を含んでいるかどうかを調べる
 * プログラム FindFile1を作ってください。起動の時の引数は、
 *   java FindFile1 検索文字列　検索対象ファイル
 * のようにして、文字列が見つかったらその行場号と、
 * その行そのものを表示してください。
 * 
 */

import java.io.*;

public class FindFile1 {
  public static void main (String[] args) {
    if (args.length != 2) {
      System.out.println("使用法 : java FindFile1 検索文字列 検索対象ファイル");
      System.exit(0);
    }
    
    String findstring = args[0];
    String filename = args[1];
    System.out.println("検索文字列は「" + findstring + "」です");
    
    try {
      BufferedReader reader = new BufferedReader(new FileReader(filename));
      String line;
      int linenum = 0;
      
      while ((line = reader.readLine()) != null) {
        int n = line.indexOf(findstring);
        
        if (n >= 0) {
          System.out.println(linenum + " : " + line);
          linenum++;
        }
      }
      
      reader.close();
    } catch (IOException e) {
      System.out.println(e);
    }
  }
}