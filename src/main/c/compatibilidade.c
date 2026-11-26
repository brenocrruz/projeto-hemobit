#include <stdio.h>

// Mapeamento dos índices para os tipos sanguíneos
// 0:A+, 1:A-, 2:B+, 3:B-, 4:AB+, 5:AB-, 6:O+, 7:O-
const char* TIPOS[] = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

// Matriz de compatibilidade (Linha = Receptor, Coluna = Doador)
// 1 = Compatível, 0 = Incompatível
int matrizCompatibilidade[8][8] = {
    // Doadores: A+ A- B+ B- AB+ AB- O+ O-
    {1, 1, 0, 0, 0, 0, 1, 1}, // Receptor A+  (pode receber A+, A-, O+, O-)
    {0, 1, 0, 0, 0, 0, 0, 1}, // Receptor A-  (pode receber A-, O-)
    {0, 0, 1, 1, 0, 0, 1, 1}, // Receptor B+  (pode receber B+, B-, O+, O-)
    {0, 0, 0, 1, 0, 0, 0, 1}, // Receptor B-  (pode receber B-, O-)
    {1, 1, 1, 1, 1, 1, 1, 1}, // Receptor AB+ (pode receber todos)
    {0, 1, 0, 1, 0, 1, 0, 1}, // Receptor AB- (pode receber A-, B-, AB-, O-)
    {0, 0, 0, 0, 0, 0, 1, 1}, // Receptor O+  (pode receber O+, O-)
    {0, 0, 0, 0, 0, 0, 0, 1}  // Receptor O-  (pode receber apenas O-)
};

// Função que verifica a compatibilidade
int isCompativel(int receptor, int doador) {
    return matrizCompatibilidade[receptor][doador];
}

int main() {
    printf("--- Testes de Compatibilidade ABO/Rh ---\n");
    
    // Teste 1: Receptor O- (índice 7) recebendo de A+ (índice 0) -> Deve ser INCOMPATÍVEL (0)
    printf("Receptor O- recebendo A+: %s\n", isCompativel(7, 0) ? "COMPATIVEL" : "INCOMPATIVEL");
    
    // Teste 2: Receptor A+ (índice 0) recebendo de O- (índice 7) -> Deve ser COMPATÍVEL (1)
    printf("Receptor A+ recebendo O-: %s\n", isCompativel(0, 7) ? "COMPATIVEL" : "INCOMPATIVEL");
    
    // Teste 3: Receptor AB+ (índice 4) recebendo de B- (índice 3) -> Deve ser COMPATÍVEL (1)
    printf("Receptor AB+ recebendo B-: %s\n", isCompativel(4, 3) ? "COMPATIVEL" : "INCOMPATIVEL");

    return 0;
}