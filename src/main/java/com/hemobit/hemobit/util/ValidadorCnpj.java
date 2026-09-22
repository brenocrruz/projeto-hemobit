package com.hemobit.hemobit.util;

public class ValidadorCnpj {
    public static boolean isValid(String cnpj) {

        if (cnpj == null) {
            return false;
        }

        cnpj = cnpj.replaceAll("\\D", "");

        if (cnpj.length() != 14) {
            return false;
        }

        if (cnpj.matches("(\\d)\\1{13}")) {
            return false;
        }

        int[] peso1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] peso2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

        int primeiroDigito = calcularDigito(cnpj, peso1);
        int segundoDigito = calcularDigito(cnpj, peso2);

        return primeiroDigito == Character.getNumericValue(cnpj.charAt(12))
                && segundoDigito == Character.getNumericValue(cnpj.charAt(13));
    }

    private static int calcularDigito(String cnpj, int[] pesos) {

        int soma = 0;

        for (int i = 0; i < pesos.length; i++) {
            soma += Character.getNumericValue(cnpj.charAt(i)) * pesos[i];
        }

        int resto = soma % 11;

        if (resto < 2) {
            return 0;
        }

        return 11 - resto;
    }
}