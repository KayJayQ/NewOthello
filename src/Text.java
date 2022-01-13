/**
 * @file: Text class, manages localization text
 * 
 * @author: Kay Qiang
 * @author: qiangkj@cmu.edu
 */
package src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Text {
    public static HashMap<Integer, String> textLib;

    //text lib
    String zh_cn = "src/lang/zh-cn.txt";
    String en_us = "src/lang/en-us.txt";
    
    public Text(){
        // NOOP
    }

    // override function using default path
    public void loadText(int lang) {
        if(lang == 0) {
            this.loadText(this.zh_cn);
        } else {
            this.loadText(this.en_us);
        }
    }

    // load language text lib from path
    public void loadText(String path) {
        Text.textLib = new HashMap<Integer, String>();
        File file = new File(path);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                String[] pair = tempStr.split(":");
                Text.textLib.put(Integer.valueOf(pair[0]), new String(pair[1].getBytes(), "utf-8"));
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
