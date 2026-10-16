package com.hemobit.hemobit.aed.estruturas;

public class MatrizCompatibilidade {
    
    private static final boolean[][] matrizABORh = {
        {true,  true,  false, false, false, false, true,  true},
        {false, true,  false, false, false, false, false, true},
        {false, false, true,  true,  false, false, true,  true},
        {false, false, false, true,  false, false, false, true},
        {true,  true,  true,  true,  true,  true,  true,  true},
        {false, true,  false, true,  false, true,  false, true},
        {false, false, false, false, false, false, true,  true},
        {false, false, false, false, false, false, false, true}
    };

    public static boolean isCompativel(int indiceReceptor, int indiceDoador) {
        return matrizABORh[indiceReceptor][indiceDoador];
    }
}