#include <stdio.h>

int main() {
    float nota1, nota2, nota3, nota4, media;

    printf("Digite a primeira nota: ");
    scanf("%f", &nota1);
    printf("Digite a segunda nota: ");
    scanf("%f", &nota2);
    printf("Digite a terceira nota: ");
    scanf("%f", &nota3);
    printf("Digite a quarta nota: ");
    scanf("%f", &nota4);

    media = (nota1 + nota2 + nota3 + nota4) / 4.0;

    printf("Sua media eh: %.2f\n", media);

    if (media >= 7.0) {
        printf("Voce foi APROVADO por media!\n");
    } else if (media >= 4.0) {
        printf("Voce esta em PROVA FINAL.\n");
    } else {
        printf("Voce foi REPROVADO.\n");
    }

    return 0;
}
