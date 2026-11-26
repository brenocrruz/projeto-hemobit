package com.hemobit.hemobit.aed.sistema;

public class MatrizCompatibilidade {
    
    // 0:A+, 1:A-, 2:B+, 3:B-, 4:AB+, 5:AB-, 6:O+, 7:O-
    public static final String[] TIPOS = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
    
    // Matriz: Linha = Receptor, Coluna = Doador
    private static final int[][] MATRIZ = {
        {1, 1, 0, 0, 0, 0, 1, 1}, // A+
        {0, 1, 0, 0, 0, 0, 0, 1}, // A-
        {0, 0, 1, 1, 0, 0, 1, 1}, // B+
        {0, 0, 0, 1, 0, 0, 0, 1}, // B-
        {1, 1, 1, 1, 1, 1, 1, 1}, // AB+
        {0, 1, 0, 1, 0, 1, 0, 1}, // AB-
        {0, 0, 0, 0, 0, 0, 1, 1}, // O+
        {0, 0, 0, 0, 0, 0, 0, 1}  // O-
    };

    public static boolean isCompativel(int indiceReceptor, int indiceDoador) {
        return MATRIZ[indiceReceptor][indiceDoador] == 1;
    }

    public static void main(String[] args) {
        System.out.println("--- Testes de Compatibilidade ABO/Rh ---");
        System.out.println("Receptor O- recebendo A+: " + (isCompativel(7, 0) ? "COMPATIVEL" : "INCOMPATIVEL"));
        System.out.println("Receptor A+ recebendo O-: " + (isCompativel(0, 7) ? "COMPATIVEL" : "INCOMPATIVEL"));
        System.out.println("Receptor AB+ recebendo B-: " + (isCompativel(4, 3) ? "COMPATIVEL" : "INCOMPATIVEL"));
    }
}