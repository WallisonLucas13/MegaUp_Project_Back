package com.example.MegaUp_Server.services;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CodeKeyGenerator {

    public String gerarKey(){

        Random random = new Random();

        //Simples Key 64 Digitos
        int size = 64;

        String lowerLetters = "q,w,,e,r,t,y,u,i,o,p,,a,,s,d,f,g,h,j,k,l,ç,z,x,c,v,b,n,m";
        String upperLetters = ",Q,W,E,R,T,Y,U,I,O,P,A,S,D,F,G,H,J,K,L,Ç,Z,X,C,V,B,N,M";
        String numbers = ",1,2,3,4,5,6,7,8,9,0";
        String keyLine = lowerLetters + upperLetters + numbers;

        List<String> list = Arrays.asList(keyLine.split(","));
        String keyFinish = "";

        for(int i=0; i<size; i++){
            keyFinish += list.get(random.nextInt(list.size()-1));
        }

        return keyFinish;
    }
}
