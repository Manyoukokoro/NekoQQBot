package org.nekotori.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

public class LittleUtils {



    public static String resolve(String s){
        List<String> polandExp = toPoland(s);
        Deque<String> stack = new ArrayDeque<>();
        for(String ss:polandExp){
            switch (ss){
                case "+":{
                    BigDecimal add = new BigDecimal(stack.pop()).add(new BigDecimal(stack.pop()));
                    stack.push(add.toString());
                    break;
                }
                case "-":{
                    String pop2 = stack.pop();
                    BigDecimal add = new BigDecimal(stack.pop()).add(new BigDecimal(pop2).negate());
                    stack.push(add.toString());
                    break;
                }
                case "*":{
                    BigDecimal add = new BigDecimal(stack.pop()).multiply(new BigDecimal(stack.pop()));
                    stack.push(add.toString());
                    break;
                }
                case "/":{
                    String pop2 = stack.pop();
                    BigDecimal add = new BigDecimal(stack.pop()).divide(new BigDecimal(pop2), RoundingMode.FLOOR);
                    stack.push(add.toString());
                    break;
                }
                case "^":{
                    String pop2 = stack.pop();
                    if(Integer.parseInt(pop2)>65535){
                        stack.push("0");
                    }else{
                        BigDecimal add = new BigDecimal(stack.pop()).pow(Integer.parseInt(pop2));
                        stack.push(add.toString());
                    }
                    break;
                }
                default: stack.push(ss);
            }
        }
        return stack.pop();
    }

    public static List<String> toPoland(String s){
        String[] split = s.split("[\\+\\-\\*/()^]");
        List<String> collect = Arrays.stream(split)
                .filter(z -> !"".equals(z)).collect(Collectors.toList());
        split = collect.toArray(new String[]{});
        ArrayDeque<String> stack = new ArrayDeque<>();
        ArrayList<String> temp = new ArrayList<>();
        ArrayList<String> out = new ArrayList<>();
        for(String x:split){
            while(s.startsWith("+")
                    ||s.startsWith("-")
                    ||s.startsWith("*")
                    ||s.startsWith("/")
                    ||s.startsWith("^")
                    ||s.startsWith("(")
                    ||s.startsWith(")")){
                temp.add(s.substring(0,1));
                s = s.substring(1);
            }
            if(s.startsWith(x)){
                temp.add(x);
                s = s.replaceFirst(x,"");
            }
        }
        while(s.startsWith("+")
                ||s.startsWith("-")
                ||s.startsWith("*")
                ||s.startsWith("/")
                ||s.startsWith("^")
                ||s.startsWith("(")
                ||s.startsWith(")")){
            temp.add(s.substring(0,1));
            s = s.substring(1);
        }
        for(String o:temp){
            if(!o.matches("[\\+\\-\\*/\\^()]")){
                out.add(o);
                continue;
            }
            switch (o){
                case "^":{
                    while(!stack.isEmpty() && stack.peek().matches("[\\^]")){
                        out.add(stack.pop());
                    }
                    stack.push(o);
                    break;
                }
                case "*":
                case "/":{
                    while(!stack.isEmpty() && stack.peek().matches("[\\*/]")){
                        out.add(stack.pop());
                    }
                    stack.push(o);
                    break;
                }
                case "+":
                case "-":{
                    while(!stack.isEmpty() && stack.peek().matches("[\\+\\-\\*/\\^]")){
                        out.add(stack.pop());
                    }
                    stack.push(o);
                    break;
                }
                case ")":{
                    while(!stack.isEmpty() && !stack.peek().matches("\\(")){
                        out.add(stack.pop());
                    }
                    stack.pop();
                    break;
                }
                default: stack.push(o);
            }
        }
        while(!stack.isEmpty()){
            out.add(stack.pop());
        }
        return out;
    }


    public static String multiply(String num1, String num2) {
        if("0".equals(num1)||"0".equals(num2)){
            return "0";
        }
        String res = "0";
        for(int i=0;i<num2.length();i++){
            char c = num2.charAt(i);
            String s = singleMultiply(num1, c);
            s = tenTimes(s, num2.length() - 1 - i);
            res = longAdd(res,s);
        }
        return res;
    }

    public static String tenTimes(String num,int times){
        StringBuilder numBuilder = new StringBuilder(num);
        for(int i = 0; i<times; i++){
            numBuilder.append("0");
        }
        return numBuilder.toString();
    }

    public static String singleMultiply(String num1,char num2) {
        String a = reverse(num1);
        int b = num2-'0';
        int up = 0;
        StringBuilder sb = new StringBuilder();
        for(int i =0;i<=num1.length();i++){
            int temp1 = i<a.length()?a.charAt(i)-'0':0;
            int res = temp1*b + up;
            sb.append(res % 10);
            up = res / 10;
        }
        String reverse = reverse(sb.toString());
        return reverse.replaceFirst("0*","");
    }


    public static String longAdd(String num1,String num2) {
        String a = reverse(num1);
        String b = reverse(num2);
        int up = 0;
        StringBuilder sb = new StringBuilder();
        for(int i = 0;i<=a.length()||i<=b.length();i++){
            int temp1 = i<a.length()?a.charAt(i)-'0':0;
            int temp2 = i<b.length()?b.charAt(i)-'0':0;
            int res = temp1+temp2+up;
            sb.append(res % 10);
            up = res / 10;
        }
        String reverse = reverse(sb.toString());
        return reverse.replaceFirst("0*","");
    }


    public static String reverse(String num){
        StringBuilder sb = new StringBuilder();
        for(int i=num.length()-1;i>=0;i--){
            sb.append(num.charAt(i));
        }
        return sb.toString();
    }

}
