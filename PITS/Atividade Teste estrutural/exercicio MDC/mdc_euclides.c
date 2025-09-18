#include <stdio.h>

int mdc(int a, int b) {
    int temp;
    while (b != 0) {
        temp = b;
        b = a % b;
        a = temp;
    }
    return a;
}

int main() {
    int num1, num2;

    printf("Digite o primeiro numero: ");
    scanf("%d", &num1);
    printf("Digite o segundo numero: ");
    scanf("%d", &num2);

    // Certifica-se de que os números são positivos para o cálculo
    if (num1 < 0) num1 = -num1;
    if (num2 < 0) num2 = -num2;

    printf("O MDC de %d e %d eh: %d\n", num1, num2, mdc(num1, num2));

    return 0;
}
