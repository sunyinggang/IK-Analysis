package com.siper;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.font.KumoFont;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.nlp.tokenizers.ChineseWordTokenizer;
import com.kennycason.kumo.palette.LinearGradientColorPalette;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.awt.*;
import java.io.*;

import java.io.StringReader;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import org.wltea.analyzer.lucene.IKAnalyzer;


public class JD {
    public static void main(String[] args) throws Exception{

        StringBuffer buf = new StringBuffer();
        for (int i=0;i<10;i++){
//     1.用Jsoup解析网页
            String url="https://sclub.jd.com/comment/productPageComments.action?callback=fetchJSON_comment98vv2&productId=100009220534&score=3&sortType=6&page="+i+"&pageSize=10&isShadowSku=0&fold=1";
            CloseableHttpResponse indexRes = sendGet(url);
//        获取json内容，将其转换为字符串
            String indexHtml = EntityUtils.toString(indexRes.getEntity(), "UTF-8");

//        截取成json字符串
            String json2=indexHtml.substring(indexHtml.indexOf('(')+1,indexHtml.lastIndexOf(')'));

//        获取评论
           JSONArray array = JSON.parseObject(json2).getJSONArray("comments");
           for (Object item : array) {
                //获取评论中的内容
               buf.append(JSON.parseObject(item.toString()).getString("content"));
            }
        }
        String s = buf.toString();

        StringReader sr=new StringReader(s);
        IKSegmenter ik=new IKSegmenter(sr, false);
        Lexeme lex=null;
        BufferedWriter bw1=new BufferedWriter(new FileWriter("分词后的评论内容.txt"));
        while((lex=ik.next())!=null){
            bw1.write(lex.getLexemeText()+"\n");
            System.out.println(lex.getLexemeText());
        }

        FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
        frequencyAnalyzer.setWordFrequenciesToReturn(600);
        frequencyAnalyzer.setMinWordLength(2);
        //这边要注意,引用了中文的解析器
        frequencyAnalyzer.setWordTokenizer(new ChineseWordTokenizer());

        //拿到文档里面分出的词,建立一个集合存储起来
        List<WordFrequency> wordFrequencies = frequencyAnalyzer.load("分词后的评论内容.txt");


        Dimension dimension = new Dimension(600, 600);

        //设置图片相关的属性,这边是大小和形状,更多的形状属性,可以从CollisionMode源码里面查找
        WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
        wordCloud.setPadding(2);

        //这边要注意意思,是设置中文字体的,如果不设置,得到的将会是乱码,
        //这是官方给出的代码没有写的,我这边拓展写一下,字体,大小可以设置
        //具体可以参照Font源码
        java.awt.Font font = new java.awt.Font("STSong-Light", 2, 16);
        wordCloud.setKumoFont(new KumoFont(font));
        wordCloud.setBackgroundColor(new Color(255, 255, 255));
        //因为我这边是生成一个圆形,这边设置圆的半径
        wordCloud.setBackground(new CircleBackground(255));
        //设置颜色
        wordCloud.setColorPalette(new LinearGradientColorPalette(Color.RED, Color.BLUE, Color.GREEN, 30, 30));
        wordCloud.setFontScalar(new SqrtFontScalar(12, 45));
        //将文字写入图片
        wordCloud.build((java.util.List<WordFrequency>) wordFrequencies);
                wordCloud.writeToFile("image.png");
    }
    //发送get请求,获取响应结果
    public static CloseableHttpResponse sendGet(String url) throws IOException {
        //创建httpClient客户端
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //创建请求对象,发送请求
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
        //httpGet.setHeader("Cookie", "__jdu=1011906297; shshshfpa=546abe25-2650-27ef-387c-cbbd473fdf61-1541645859; shshshfpb=010d34f55e5d106e8262dc1639c7243399901f2c0392299fd5be3a6228; ipLoc-djd=1-72-2799-0; unpl=V2_ZzNtbUBTFkV9XxUDLEkMA2IGQVpLBBRAd19ABi4QWgIwVkZZclRCFXwURlRnGloUZAEZWUVcQBRFCEdkexhdBGYBGlhLVXNILGYFAX5SCQBXMxFdcl9zFXQIRlx6Hl8NYTMiWnJnHk0qUh8EI1wMW1cFFlhBV0YUfQh2VUsYbE4JAl9dQ1dDHXQPRVx9KV01ZA%3d%3d; __jda=122270672.1011906297.1537011991.1543032744.1543334183.4; __jdc=122270672; __jdv=122270672|www.linkhaitao.com|t_1000039483_lh_rd4zd4|tuiguang|35da9fbffaa744b68bfd3f7cd876fde5|1543334183097; PCSYCityID=412; _gcl_au=1.1.1618589019.1543334587; wlfstk_smdl=kpyxn7dgfu7ntzeriqf1nuoyf1pvmmz6; _pst=%E9%AD%82%E5%AF%84%E6%A2%A6%E4%B9%A1; logintype=wx; unick=%E9%AD%82%E5%AF%84%E6%A2%A6%E4%B9%A1; pin=%E9%AD%82%E5%AF%84%E6%A2%A6%E4%B9%A1; npin=%E9%AD%82%E5%AF%84%E6%A2%A6%E4%B9%A1; _tp=Zj9a6fHTft48nybOFAAH4sbL2FOhPiV6ww52%2BkgoNl8MBXdBGCUdKuMfROuV8QHU; pinId=KCcQw4HqqMKLC3rBXJmYjQ; 3AB9D23F7A4B3C9B=GPGP3C2BU4NAMC7CA2PZAKXKNW6757AO6KM6ENFTRAQ47S4RFIN3BMNNUQ2B3CUWAEQKVQEI5GA7Z245JTB2BIDIBU; mt_xid=V2_52007VwMTUl1QU10cQR9sB2NQRwVbUAJGSkkcCBliBBdXQVECWB9VS19SblEUWlkMB1tKeRpdBW4fElJBW1tLHkgSXAxsBhBiX2hSahxMHFoMZQYSV21YV1wY; shshshfp=cc874848aa1eb0d35dfd56e0e4ba0fb3; JSESSIONID=3AD0CE01D03F107D0B4F45BED45F806D.s1; shshshsID=52730810bd55258389660fdba736586f_15_1543337522651; __jdb=122270672.17.1011906297|4.1543334183; thor=__jdu=1011906297; shshshfpa=546abe25-2650-27ef-387c-cbbd473fdf61-1541645859; shshshfpb=010d34f55e5d106e8262dc1639c7243399901f2c0392299fd5be3a6228; ipLoc-djd=1-72-2799-0; PCSYCityID=412; _pst=%E9%AD%82%E5%AF%84%E6%A2%A6%E4%B9%A1; unick=%E9%AD%82%E5%AF%84%E6%A2%A6%E4%B9%A1; pin=%E9%AD%82%E5%AF%84%E6%A2%A6%E4%B9%A1; _tp=Zj9a6fHTft48nybOFAAH4sbL2FOhPiV6ww52%2BkgoNl8MBXdBGCUdKuMfROuV8QHU; jwotest_product=99; unpl=V2_ZzNtbRVUQBAmD0EGexlVBmJQQlsSBEQUcQxCXHxKXVFnBEFZclRCFXwURlRnGlQUZwEZXkJcRhJFCEdkexhdBGYBGlhLVXNILGYFAX1BDFlXMxFdcl9zFXQIRlx6Hl8NYTMiWnJnHk0qUh8EI1wMW1cFFlhBV0YUfQh2VUsYbE4JAl9dQ1dDHXQPRVx9KV01ZA%3d%3d; __jda=122270672.1011906297.1537011991.1543342650.1543440108.7; __jdc=122270672; thor=EBE4F58722D3C53DD96909AC59EFA6D1BC94658FBAC118C866693EA1EBF5169985038D47FF0615A96C39E195C704E4269C0AE2B142F2A6CF58BFF0E3C588B282CFE4B6DB95B893DBB7528C8A117BF09C2BB8B1A6955DEA1B2D00A191464B5CC90B094977CD8D55F54EAE17D856F65E1A4577319BD2627227472617F7462C4E24; pinId=KCcQw4HqqMKLC3rBXJmYjQ; __jdv=122270672|www.linkhaitao.com|t_1000039483_lh_rd7iam|tuiguang|f224b66c11824ba7ab6055596b0e16b5|1543440132756; 3AB9D23F7A4B3C9B=GPGP3C2BU4NAMC7CA2PZAKXKNW6757AO6KM6ENFTRAQ47S4RFIN3BMNNUQ2B3CUWAEQKVQEI5GA7Z245JTB2BIDIBU; mt_xid=V2_52007VwMTUl1QU10cQR9sUWJRElFbDAFGHkgRXRliAxMCQVFSXEtVGl8GYFYbWloIUwkceRpdBW4fElJBW1pLH0sSXwZsABRiX2hSahxMHFoMZQYSV21YV1wY; shshshfp=cc874848aa1eb0d35dfd56e0e4ba0fb3; _gcl_au=1.1.816281936.1543440149; shshshsID=d687c8924c94ae61b9d19c6054056eef_4_1543440174304; __jdb=122270672.7.1011906297|7.1543440108");
        //httpGet.setHeader("Connection", "keep-alive");
        httpGet.setHeader("Referer","https://item.jd.com/100009220534.html");
        CloseableHttpResponse response = httpClient.execute(httpGet);
        return response;
    }
}
